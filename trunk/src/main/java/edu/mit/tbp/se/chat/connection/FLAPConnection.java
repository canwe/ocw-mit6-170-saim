package edu.mit.tbp.se.chat.connection;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.logging.Logger;

/**
 * A FLAPConnection is a thin wrapper around a Socket that handles the
 * FLAP framing protocol that the TOC protocol uses.
 */
public class FLAPConnection {

    /**
     * The underlying socket for this *
     */
    private Socket socket;
    /**
     * the downward connection *
     */
    private InputStream in;
    /**
     * the upward connection *
     */
    private OutputStream out;
    /**
     * the client sequence number *
     */
    private short sequenceNumber;


    ////////// Debugging Support //////////

    public final Logger logger;

    public void logProtocolMessage(String header,
                                   String msg) {
        logger.finer(header + msg);
    }

    public void logOutgoing(String msg) {
        final String header = "C -> S: ";
        logProtocolMessage(header,
                msg);
    }

    public void logIncoming(String msg) {
        final String header = "S -> C: ";
        logProtocolMessage(header,
                msg);
    }

    ////////// Server Configuration //////////

    private final String TOC_SERVER = "toc.oscar.aol.com";
    private final int TOC_PORT = 9898;

    ////////// Constructors //////////

    /**
     * Create a new FLAPConnection.
     */
    public FLAPConnection() {
        // do not initialize any of socket, in, or out since the Socket
        // API doesn't allow us to create unconnected Internet sockets.
        this.logger = Logger.getLogger("edu.mit.tbp.se.chat");
    }

    ////////// Public API //////////

    /**
     * Connect to the default server.
     * <p/>
     * When this method returns, the connection user must send in
     * TOC-level messages.  The first message should be the login messages.
     *
     * @param username the username with which to connect.  The username
     *                 must be normalized.  @see edu.mit.tbp.se.chat.message.MessageLayer#normalizeName
     */
    synchronized public void connect(String username)
            throws IOException, SignOnException {

        // From TOC.txt, the steps in connecting to the server are:
        // * Client connects to TOC

        logOutgoing("connect to server");
        this.socket = new Socket(TOC_SERVER,
                TOC_PORT);
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
        this.sequenceNumber = 0;

        // * Client sends "FLAPON\r\n\r\n"

        logOutgoing("FLAPON\\r\\n\\r\\n");
        final String FLAPON_MSG = "FLAPON\r\n\r\n";
        this.out.write(FLAPON_MSG.getBytes());

        // * TOC sends Client FLAP SIGNON

        readFLAPSignon();

        // * Client sends TOC FLAP SIGNON

        writeFLAPSignon(username);

        // everything else we leave to the higher level protocols.

        // * Client sends TOC "toc_signon" message
        // * if login fails TOC drops client's connection
        //   else TOC sends client SIGN_ON reply
        // * if Client doesn't support version it drops the connection


    }

    /**
     * Disconnect from the server and clean up any resources
     * associated with the connection.
     */
    public void disconnect()
            throws IOException {

        this.socket.close();

        this.socket = null;
        this.out = null;
        this.in = null;
    }

    /**
     * Write message to the server.
     */
    synchronized public void writeMessage(String message)
            throws IOException {

        FLAPFrame frame = new FLAPFrame();
        frame.frameType = FLAPFrame.DATA_FRAME_TYPE;
        frame.sequenceNumber = this.sequenceNumber++;
        frame.data = message.getBytes();

        logOutgoing(message);
        writeFLAPFrame(frame);
    }

    private void mySleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            ; // silent catch
        }
    }

    /**
     * Read a message from the server.
     * <p/>
     * This is a blocking call.
     */
    public String readMessage()
            throws IOException {

        FLAPFrame frame = readFLAPFrame();

        if (FLAPFrame.KEEP_ALIVE_FRAME_TYPE == frame.frameType) {

            // if we get a keep-alive message, ignore it and wait for another
            // message.
            logIncoming("KEEP ALIVE");
            mySleep(1000);
            return readMessage();

        } else if (FLAPFrame.DATA_FRAME_TYPE != frame.frameType) {

            // ignore non-data frames
            this.logger.warning("readMessage received non-data frame type: " +
                    frame.frameType);

        }

        // otherwise, we have non-ignored data frame, so extract the data
        String payload = new String(frame.data,
                "US-ASCII");
        logIncoming("(" + frame.data.length + " bytes) " + payload);
        return payload;
    }

    ////////// Initial Signon Handling //////////

    /**
     * Read a FLAPSignon message from the server.
     *
     * @return the FLAP version the server is using
     * @throws IOException     if there is an I/O error
     * @throws SignOnException if sign on fails.
     */
    private byte[] readFLAPSignon()
            throws IOException, SignOnException {

        FLAPFrame frame = readFLAPFrame();

        logIncoming(frame.toString());

        if (FLAPFrame.SIGNON_FRAME_TYPE != frame.frameType) {
            throw new SignOnException("Received invalid frame type");
        }

        if (frame.data.length != 4) {
            this.logger.warning("Received invalid FLAP version");
        }

        return frame.data;
    }

    /**
     * Write the reply to the FLAP signon
     */
    synchronized private void writeFLAPSignon(String username)
            throws IOException {
        // Client To Host:
        // 4 byte FLAP version (1)
        // 2 byte TLV Tag (1)
        // 2 byte Normalized User Name Length
        // N byte Normalized User Name  (NOT null terminated)

        final byte[] VERSION = {0, 0, 0, 1};
        final byte[] TLV = {0, 1};

        // total message is 8 bytes of header + length of username
        byte[] payload = new byte[8 + username.length()];

        System.arraycopy(VERSION,
                0,
                payload,
                0,
                VERSION.length);
        System.arraycopy(TLV,
                0,
                payload,
                VERSION.length,
                TLV.length);
        packShort((short) username.length(),
                payload,
                VERSION.length + TLV.length);
        System.arraycopy(username.getBytes(),
                0,
                payload,
                VERSION.length + TLV.length + 2 /* size of short */,
                username.length());

        FLAPFrame frame = new FLAPFrame();
        frame.frameType = FLAPFrame.SIGNON_FRAME_TYPE;
        frame.sequenceNumber = this.sequenceNumber++;
        frame.data = payload;

        logOutgoing(frame.toString());
        writeFLAPFrame(frame);
    }

    ////////// Utility Code //////////

    /**
     * Extract a short from a byte array assumed to be in network byte
     * order (MSB first).
     *
     * @param data   the bytes to use
     * @param offset the location in the byte array to use.
     * @return the short represented at data[offset].
     */
    private short extractShort(byte[] data,
                               int offset) {
        return (short) (((0xFF & data[offset]) << 8) +
                ((0xFF & data[offset + 1])));
    }

    /**
     * Pack a short value into a byte array in network byte order.
     *
     * @param s      the short value to pack
     * @param dest   the destination byte array
     * @param offset the location to pack
     */
    private void packShort(short s,
                           byte[] dest,
                           int offset) {
        dest[offset + 1] = (byte) (s & 0xFF);
        dest[offset] = (byte) ((s >> 8) & 0xFF);
    }


    ////////// Raw FLAP Frame Handling //////////

    /**
     * Read a raw FLAP Frame from the server.
     */
    private FLAPFrame readFLAPFrame2()
            throws IOException {

        FLAPFrame frame = new FLAPFrame();

        int readSize;

        byte[] data = new byte[FLAPFrame.FLAP_HEADER_SIZE];
        readSize = in.read(data);
        if (readSize < FLAPFrame.FLAP_HEADER_SIZE) {
            String msg = "Could not read entire FLAP header, read " + readSize +
                    " of " + data.length;
            this.logger.warning(msg);
            throw new IOException(msg);
        }

        // check to make sure the frame is correct
        if ('*' != (char) data[FLAPFrame.ASTERISK_OFF]) {
            this.logger.warning("FLAP frame doesn't start with '*'");
            // ...but continue if it is not anyway.
        }

        // extract contents of frame
        frame.frameType = data[FLAPFrame.FRAME_TYPE_OFF];
        frame.sequenceNumber = extractShort(data,
                FLAPFrame.SEQ_OFF);
        short dataLength = extractShort(data,
                FLAPFrame.LEN_OFF);
        if (dataLength < 0) {
            logger.finer("negative dataLength on frame: " + frame);
        }

        // finally, extract the data part of the frame.
        data = new byte[dataLength];
        readSize = in.read(data);
        if (readSize < dataLength) {
            this.logger.warning("Wanted to read " + dataLength +
                    " bytes of frame data, but only read  " + readSize);
        }
        this.logger.finest("read " + readSize + " bytes");
        frame.data = data;

        return frame;
    }


    private void fillBuffer(byte[] buff)
            throws IOException {
        for (int i = 0;
             i < buff.length;
             i++) {
            buff[i] = (byte) in.read();
            logger.finest("read " + Integer.toHexString(buff[i]));
        }
    }

    /**
     * Read a raw FLAP Frame from the server.
     */
    private FLAPFrame readFLAPFrame()
            throws IOException {

        // this version reads byte-by-byte rather than batching operations
        // because batched operations may not return all of the data we
        // want.

        FLAPFrame frame = new FLAPFrame();
        int readDatum;

        // check to make sure the frame is correct
        readDatum = in.read();
        if ('*' != (char) readDatum) {
            this.logger.warning("FLAP frame doesn't start with '*', starts with '" +
                    (int) readDatum + "'");
            // ...but continue if it is not anyway.
        }

        // extract contents of frame
        frame.frameType = (byte) in.read();

        byte[] data = new byte[2];
        fillBuffer(data);
        frame.sequenceNumber = extractShort(data,
                0);
        fillBuffer(data);
        short dataLength = extractShort(data,
                0);
        //this.logger.finest("dataLength = " + dataLength);
        if (dataLength < 0) {
            logger.finer("dataLength < 0: [" + Integer.toHexString(data[0]) + "][" +
                    Integer.toHexString(data[1]) + "]");
        }

        // finally, extract the data part of the frame.
        data = new byte[dataLength];
        fillBuffer(data);
        frame.data = data;

        return frame;
    }

    private void writeFLAPFrame(FLAPFrame frame)
            throws IOException {

        // we need to null terminate the String and include that in the
        // length.
        short dataLength = (short) (frame.data.length + 1);

        out.write('*');
        out.write(frame.frameType);

        byte[] shortData = new byte[2];
        packShort(frame.sequenceNumber,
                shortData,
                0);
        out.write(shortData);

        packShort(dataLength,
                shortData,
                0);
        out.write(shortData);

        // finally write data and termination
        out.write(frame.data);
        out.write('\0');
    }

    /**
     * Callback the ConnectionObserver observing this.
     */
    private void callback() {

    }

    private class FLAPFrame {

        static final byte SIGNON_FRAME_TYPE = 1;
        static final byte DATA_FRAME_TYPE = 2;
        static final byte KEEP_ALIVE_FRAME_TYPE = 5;

        // FLAP Header (6 bytes)
        // -----------
        // Offset   Size  Type
        // 0        1     ASTERISK (literal ASCII '*')
        // 1        1     Frame Type
        // 2        2     Sequence Number
        // 4        2     Data Length
        static final int FLAP_HEADER_SIZE = 6;
        static final int ASTERISK_OFF = 0;
        static final int FRAME_TYPE_OFF = 1;
        static final int SEQ_OFF = 2;
        static final int LEN_OFF = 4;

        public byte frameType;
        public short sequenceNumber;
        public byte[] data;

        public String toString() {
            StringBuffer sb = new StringBuffer();

            // write frame type
            switch (this.frameType) {
                case SIGNON_FRAME_TYPE: {
                    sb.append("SIGNON");
                    break;
                }
                case DATA_FRAME_TYPE: {
                    sb.append("DATA");
                    break;
                }
                default: {
                    sb.append("UNKNOWN(");
                    sb.append(this.frameType);
                    sb.append(')');
                    break;
                }
            }
            ;
            sb.append(' ');

            // write sequence number:
            sb.append("SEQ:");
            sb.append(this.sequenceNumber);
            sb.append(' ');

            // write data:
            sb.append("DATA:[");
            boolean writeSpace = false;
            for (int i = 0;
                 i < this.data.length;
                 i++) {
                if (!writeSpace) {
                    writeSpace = true;
                } else {
                    sb.append(' ');
                }
                // print out in hex
                sb.append(Integer.toString(data[i],
                        16));
            }
            sb.append(']');

            return sb.toString();
        }
    }

    public boolean isConnectionOpened() {
        return null != socket && null != in && null != out;
    }


}

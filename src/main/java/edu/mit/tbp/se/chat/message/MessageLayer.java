package edu.mit.tbp.se.chat.message;

import edu.mit.tbp.se.chat.connection.FLAPConnection;
import edu.mit.tbp.se.chat.connection.SignOnException;

import java.io.IOException;
import java.util.logging.Logger;


/**
 * A MessageLayer is a stateful connection
 */
public class MessageLayer {

    /**
     * The underlying connection for this *
     */
    private FLAPConnection connection;

    public final Logger logger;


    /**
     * Create a new MessageLayer using connection as the underlying
     * connection.
     *
     * @param connection the underlying connection to use.
     */
    public MessageLayer(FLAPConnection connection) {
        this.connection = connection;
        this.logger = Logger.getLogger("edu.mit.tbp.se.chat");
    }

    /**
     * Log in.
     * <p/>
     * This assumes that the underlying connection is already properly
     * connected.
     *
     * @param username the username to log in.  This must match the
     *                 username used to connect to the server.
     * @param password the username's plain-text password.
     * @throws IOException       if there is an underlying problem with the
     *                           connection.
     * @throws SignOnException   if the server does not send a SIGN_ON
     *                           message.
     * @throws AIMErrorException if the server sends a TOC error.
     */
    public void login(String username,
                      String password)
            throws IOException, SignOnException, AIMErrorException {

        // * Client sends TOC "toc_signon" message
        // * if login fails TOC drops client's connection
        //   else TOC sends client SIGN_ON reply
        // * if Client doesn't support version it drops the connection
        //
        // [BEGIN OPTIONAL]
        //     * TOC sends Client CONFIG
        // [END OPTIONAL]
        //
        // * Client sends TOC toc_init_done message

    }

    /**
     * Send a message.
     *
     * @param msg The message to send.
     * @throws IOException if there is a problem sending the message.
     */
    public void sendMessage(TOCMessage msg)
            throws IOException {
        // fill in your code here
    }

    /**
     * Receive a message from the server.
     * <p/>
     * This will block until a message can be received.
     *
     * @return a new TOCMessage sub-class representing the message
     *         received from the server.
     * @throws IOException       if there is an underlying networking problem
     * @throws AIMErrorException if the server sent an error message.
     */
    public TOCMessage receiveMessage()
            throws IOException, AIMErrorException {
        String s = this.connection.readMessage();
        String commandString = TOCMessage.extractServerCommand(s);

        if (AIMErrorException.COMMAND_STRING.equals(commandString)) {
            throw new AIMErrorException(s);
        }

        // fill in your code here
        //TODO
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}


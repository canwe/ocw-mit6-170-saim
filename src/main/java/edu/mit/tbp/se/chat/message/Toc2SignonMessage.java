package edu.mit.tbp.se.chat.message;

public class Toc2SignonMessage extends TOCMessage {

    // server-level constants
    public static final String LOGIN_SERVER = "login.oscar.aol.com";
    public static final int LOGIN_PORT = 5190; //1234;

    // protocol-level constants
    public static final byte[] ROASTING_STRING = "Tic/Toc".getBytes();

    // client-level constants
    public static final String version = "TIC:TBP2";
    public static final String language = "english";

    /**
     * Create a new Toc2SignonMessage.
     *
     * @param username username to signon with
     * @param password associated clear-text password
     */
    public Toc2SignonMessage(String username,
                             String password) {

    }

    /**
     * Roast the user's password
     *
     * @return a roasted version of the user's password.
     */
    public String roastedPassword() {
        // see the TOC documentation for information on this
        //TODO
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    /**
     * Return the login code
     *
     * @return the super-seekret login code!
     */
    public String generateCode() {
        // see the TOC documentation for information on this
        //TODO
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public String toWireFormat() {
        //TODO
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}

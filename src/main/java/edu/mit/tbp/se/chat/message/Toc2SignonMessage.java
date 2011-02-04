package edu.mit.tbp.se.chat.message;

import edu.mit.tbp.se.chat.Utils;

import java.math.BigInteger;

public class Toc2SignonMessage extends TOCMessage {

    public static final BigInteger seekret = BigInteger.valueOf(7696L);

    // server-level constants
    public static final String LOGIN_SERVER = "login.oscar.aol.com";
    public static final int LOGIN_PORT = 5190; //1234;

    // protocol-level constants
    public static final byte[] ROASTING_STRING = "Tic/Toc".getBytes();

    // client-level constants
    public static final String version = "TIC:TBP2";
    public static final String language = "english";

    private final String username;
    private final String password;

    /**
     * Create a new Toc2SignonMessage.
     *
     * @param username username to signon with
     * @param password associated clear-text password
     */
    public Toc2SignonMessage(String username,
                             String password) {
        this.username = Utils.normalise(username);
        this.password = password;
    }

    /**
     * Roast the user's password
     *
     * @return a roasted version of the user's password.
     */
    public String roastedPassword() {
        // see the TOC documentation for information on this
        return Utils.roast(password);
    }

    /**
     * Return the login code
     *
     * @return the super-seekret login code!
     */
    public String generateCode() {
        // see the TOC documentation for information on this
        return seekret
                .multiply(BigInteger.valueOf((long)username.charAt(0)))
                .multiply(BigInteger.valueOf((long)password.charAt(0))).toString();
    }

    @Override
    public String toWireFormat() {
        String temp = "toc_signon login.oscar.aol.com 5159 " + username + " " + password + " " + language + " " + version;
        return (temp);
    }
}

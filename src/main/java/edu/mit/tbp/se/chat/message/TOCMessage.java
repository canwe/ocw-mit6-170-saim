package edu.mit.tbp.se.chat.message;

/**
 * A TOCMessage is a message to or from the server.  There should be
 * one TOCMessage sub-classes for each kind of message the server
 * sends or the client sends.
 * <p/>
 * All non-abstract subclasses must provide a
 * <code>toWireFormat</code> implementation.  This will be used by the
 * code MessageLayer as the TOC protocol string to send to the server.
 */
public abstract class TOCMessage {

    /**
     * Create the message that will be passed down the wire.
     * <p/>
     * The message is not escaped.
     *
     * @return a un-escaped wireformat
     */
    public abstract String toWireFormat();

    /**
     * Put double-quotes around s.
     *
     * @param s the String to quote
     * @return a new String representing &quot;s&quot;
     */
    public static String quoteString(String s) {
        return '"' + s + '"';
    }

    /**
     * Escape special TOC characters.
     *
     * @param s the string to escape
     * @return a new String with all of the characters escaped
     */
    public static String escapeString(String s) {
        String SPECIAL_CHARACTERS = "${}[]()\"\\";
        StringBuffer sb = new StringBuffer();

        for (int i = 0;
             i < s.length();
             i++) {
            char c = s.charAt(i);
            if (SPECIAL_CHARACTERS.indexOf(c) != -1) {
                sb.append('\\');
            }
            sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Parse the fields in a String returned from the server.
     *
     * @param s         the String to parse
     * @param numFields the number of fields desired
     * @return a new array of Strings where each element, in order, is a
     *         field in the String.
     */
    public static String[] parseFields(String s,
                                       int numFields) {
        return s.split(":",
                numFields);
    }

    /**
     * Parse a TOC Boolean String into a boolean value.
     *
     * @param s the String to parse
     * @return the boolean s represents.
     * @throws IllegalArgumentException if s cannot be coerced into a boolean
     */
    public boolean parseBoolean(String s)
            throws IllegalArgumentException {
        s = s.toLowerCase();
        if ('t' == s.charAt(0)) {
            return true;
        } else if ('f' == s.charAt(0)) {
            return false;
        } else {
            throw new IllegalArgumentException("\"" + s + "\" is an invalid boolean");
        }
    }

    /**
     * Extract the initial, command portion a String from the server.
     *
     * @param s the String to parse
     * @return a new String representing the command part of the
     *         server's String.
     */
    public static String extractServerCommand(String s) {
        return parseFields(s,
                2)[0];
    }


}

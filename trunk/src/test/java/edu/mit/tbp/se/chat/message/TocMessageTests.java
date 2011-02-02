package edu.mit.tbp.se.chat.message;

public class TocMessageTests extends junit.framework.TestCase {

    public void testEscapeString() {
        assertEquals("escape a string containing all special characters",
                "\\$\\{\\}\\[\\]\\(\\)\\\"\\\\",
                TOCMessage.escapeString("${}[]()\"\\"));
        String normal = "abcdefghijklmnop";
        assertEquals("a normal string",
                normal,
                TOCMessage.escapeString(normal));

    }


}

/**
 * Dummy implementation for Testing *
 */
class ConcreteTocMessage extends TOCMessage {

    public String toWireFormat() {
        return "";
    }

}
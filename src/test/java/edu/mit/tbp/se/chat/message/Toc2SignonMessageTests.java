package edu.mit.tbp.se.chat.message;

public class Toc2SignonMessageTests extends junit.framework.TestCase {

    public void testRoastedPassword() {
        Toc2SignonMessage msg = new Toc2SignonMessage("test", "password");
        assertEquals("example roasting", "0x2408105c23001130", msg.roastedPassword());
    }

    public void testGenerateCode() {
        // see toc2.txt
        Toc2SignonMessage msg = new Toc2SignonMessage("test", "x5435");
        assertEquals("example generated code", "107128320", msg.generateCode());
    }

}

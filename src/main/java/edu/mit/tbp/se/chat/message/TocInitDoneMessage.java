package edu.mit.tbp.se.chat.message;

public class TocInitDoneMessage extends TOCMessage {

    private static final String CMD = "toc_init_done";

    /**
     * Create a new TocInitDoneMessage.
     */
    public TocInitDoneMessage() {
        // pass
    }

    @Override
    public String toWireFormat() {
        return(CMD);
    }
}

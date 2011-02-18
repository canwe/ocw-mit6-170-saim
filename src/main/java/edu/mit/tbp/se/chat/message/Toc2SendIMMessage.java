package edu.mit.tbp.se.chat.message;

import edu.mit.tbp.se.chat.Utils;

public class Toc2SendIMMessage extends TOCMessage {

    private final String recipient;
    private final String msg;
    private final String auto;

    public Toc2SendIMMessage(String destinationUser,
                             String message) {
        this(destinationUser, message, false);
    }

    public Toc2SendIMMessage(String destinationUser,
                             String message,
                             boolean autoMessage) {
        this.recipient = Utils.normalise(destinationUser);
        this.msg = Utils.encodeText(message);
        auto = autoMessage ? " auto" : "";
    }

    @Override
    public String toWireFormat() {
        return ("toc_send_im " + recipient + " " + msg + auto);
    }
}

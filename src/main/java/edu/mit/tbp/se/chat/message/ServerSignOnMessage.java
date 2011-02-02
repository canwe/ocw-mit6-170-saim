package edu.mit.tbp.se.chat.message;

public class ServerSignOnMessage extends TOCMessage {

    public static final String COMMAND_STRING = "SIGN_ON";

    private String wireformat;

    public ServerSignOnMessage(String wireformat) {
        this.wireformat = wireformat;
    }

    public String toWireFormat() {
        return this.wireformat;
    }

    public String toString() {
        return "ServerSignOnMessage: " + this.toWireFormat();
    }

}

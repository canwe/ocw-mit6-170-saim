package edu.mit.tbp.se.chat.message;

/**
 * An AIMErrorException is an exception raised in response to an ERROR
 * message returned from the server.
 */
public class AIMErrorException extends Exception {

    public static final String COMMAND_STRING = "ERROR";

    private String wireformat;
    private String error;
    private String args;

    public AIMErrorException(String s) {
        super(s);

        this.wireformat = s;

        String[] fields = TOCMessage.parseFields(s,
                3);
        this.error = fields[1];
        if (3 == fields.length) {
            this.args = fields[2];
        } else {
            this.args = "";
        }
    }

    public String getWireFormat() {
        return this.wireformat;
    }

    public String getError() {
        return this.error;
    }

    public String getArgs() {
        return this.args;
    }

}

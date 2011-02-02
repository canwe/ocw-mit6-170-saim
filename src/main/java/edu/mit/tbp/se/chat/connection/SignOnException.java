package edu.mit.tbp.se.chat.connection;

public class SignOnException extends Exception {

    public SignOnException() {
        super();
    }

    public SignOnException(String reason) {
        super(reason);
    }
}

package edu.mit.tbp.se.chat.connection;

/**
 * A ConnectionObserver receives messages from connections.
 */
public interface ConnectionObserver {

    /**
     * Receive a message from a connection.
     *
     * @param message the message received.
     */
    public void recieveMessage(String message);

}

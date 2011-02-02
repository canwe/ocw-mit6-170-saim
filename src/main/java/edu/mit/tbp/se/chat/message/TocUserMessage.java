package edu.mit.tbp.se.chat.message;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A TocUserMessage abstracts the behavior of any TOC message that has
 * a user list as its argument.
 * <p/>
 * The TOC protocol message for all user messages is the same:
 * &quot;TOC_COMMAND userlist...&quot; .  Therefore, this class
 * provides a toWireFormat that properly writes the message.  However,
 * it requires all sub-classes to provide the TOC_COMMAND to start the
 * protocol message.
 */
abstract public class TocUserMessage extends TOCMessage {

    /**
     * the users *
     */
    private List<String> users;

    /**
     * Create a new TocUserMessage with an empty list of buddies.
     */
    public TocUserMessage() {
        this.users = new ArrayList<String>();
    }

    /**
     * Create a new TocUserMessage with the list of buddies containing only user.
     */
    public TocUserMessage(String user) {
        this();
        this.users.add(user);
    }

    /**
     * Create a new TocUserMessage with the given list of users.
     *
     * @param users the list of users.  All elements must
     *              be Strings.  This uses the list as given; it does not clone it.
     */
    public TocUserMessage(List<String> users) {
        this.users = users;
    }

    /**
     * Get the TOC_MESSAGE type for this
     *
     * @return a String with the toc_message type.
     */
    abstract public String getTocMessage();

    public String toWireFormat() {
        StringBuffer sb = new StringBuffer();

        sb.append(getTocMessage());

        for (String username : this.users) {
            username = escapeString(username);
            username = quoteString(username);

            // we have to add a space on the first iteration to separate the
            // name from the TOC command header.
            sb.append(' ');
            sb.append(username);
        }

        return sb.toString();
    }
}

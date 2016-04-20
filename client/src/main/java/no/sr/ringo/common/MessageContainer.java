package no.sr.ringo.common;

import no.sr.ringo.client.Messages;

/**
 * Interface for object which contain messages.
 * e.g. Inbox, Outbox etc...
 *
 * User: andy
 * Date: 1/27/12
 * Time: 11:02 AM
 */
public interface MessageContainer {
    /**
     * Gets the total number of messages
     * @return
     */
    Integer getCount();

    /**
     * Gets the object for iterating through the messages.
     * @return
     */
    Messages getMessages();

    /**
     * Fetches the path part of the URI which corresponds to this resource.
     * e.g. inbox for Inbox, messages for Messagebox
     * @return
     */
    String getPath();

}

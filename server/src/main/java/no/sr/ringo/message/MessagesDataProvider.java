package no.sr.ringo.message;

import eu.peppol.persistence.TransferDirection;

import java.util.List;

/**
 * Used by the Fetch message for client use case to
 * provide the messages and the total number of messages for the implementations
 * operation.
 *
 * e.g messages, inbox, outbox, incoming without account id etc..
 *
 * User: andy
 * Date: 2/22/12
 * Time: 11:36 AM
 */
public interface MessagesDataProvider {
    /**
     * The total number of messages in the result set.
     * @return
     */
    Integer getCount(SearchParams searchParams);

    /**
     * The messages for the current batch
     * @return
     */
    List<MessageMetaData> getMessages(SearchParams searchParams);

    /**
     * Inbox The messages for the current batch
     * @return
     */
    List<MessageMetaData> getMessages(TransferDirection direction);
}

/* Created by steinar on 06.01.12 at 13:54 */
package no.sr.ringo.client;

import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.MessageQueryRestResponse;
import no.sr.ringo.response.Navigation;
import no.sr.ringo.response.RingoResponseHandler;

import java.util.Iterator;

/**
 * The Messages object provides a mechanism for iterating through all
 * messages without first loading all the messages into memory.
 *
 * A batch of messages is retrieved and when more messages are required to continue
 * iteration, they are automatically fetched from the server.
 *
 * @author andy
 */
public class Messages implements Iterable<Message> {

    private final RingoService ringoService;
    private final MessageQueryRestResponse messagesResponse;
    private final RingoResponseHandler<? extends Messages> responseHandler;

    /**
     * This constructor should not be used when writing client code.
     * @param ringoService
     * @param messagesResponse
     * @param responseHandler use to handle parsing the results of clicking on navigation links
     */
    public Messages(RingoService ringoService, MessageQueryRestResponse messagesResponse, RingoResponseHandler<? extends Messages> responseHandler) {
        this.ringoService = ringoService;
        this.messagesResponse = messagesResponse;
        this.responseHandler = responseHandler;
    }

    /**
     * Creates an Iterator for traversing through the individual messages.
     * @return
     */
    public Iterator<Message> iterator() {
        return new MessageIterator(ringoService, messagesResponse, responseHandler);
    }

    /**
     * Returns false if there are no messages true otherwise
     * @return
     */
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    /**
     * Iterator which automatically fetches messages from the RingoServer using the navigation
     * links provided in the rest response.
     */
    private static final class MessageIterator implements Iterator<Message> {

        private Iterator<MessageWithLocations> partIterator ;
        private MessageQueryRestResponse restResponse;
        private final RingoResponseHandler<? extends Messages> responseHandler;
        private final RingoService ringoService;

        public MessageIterator(RingoService ringoService, MessageQueryRestResponse restResponse,final RingoResponseHandler<? extends Messages> responseHandler) {
            this.ringoService = ringoService;
            this.restResponse = restResponse;
            this.responseHandler = responseHandler;
            this.partIterator = restResponse.getMessageList().iterator();
        }

        public boolean hasNext() {
            boolean hasNext =  partIterator.hasNext();
            final Navigation navigation = restResponse.getNavigation();

            //are there no more messages in the current batch and no navigation links
            if (!hasNext && (navigation == null || navigation.getNext() == null)) {
                return false;
            } else if (!hasNext && navigation != null && navigation.getNext() != null) {
                //If there are no messages left in the current batch but there is a navigation link for next batch
                //fetches the next load of messages
                final Messages messages = ringoService.next(navigation, responseHandler);
                if (messages != null) {
                    //reassign the local messages and Iterator with the messages in the rest response.
                    restResponse = messages.messagesResponse;
                    partIterator = messages.messagesResponse.getMessageList().iterator();
                    hasNext = partIterator.hasNext();
                }
            }
            return hasNext;
        }

        public Message next() {
            return new Message(ringoService, partIterator.next());
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported by this iterator.");
        }
    }
}

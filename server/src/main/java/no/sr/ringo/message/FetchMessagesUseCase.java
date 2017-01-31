package no.sr.ringo.message;

import com.google.inject.Inject;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountId;
import no.sr.ringo.resource.UriLocationAware;
import no.sr.ringo.response.InboxQueryResponse;
import no.sr.ringo.response.MessagesQueryResponse;
import no.sr.ringo.response.Navigation;
import no.sr.ringo.response.OutboxQueryResponse;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Stateful usecase which can be used to fetch messages for a given account.
 *
 * Useage..
 *
 *  MessagesQueryResponse messagesQueryResponse = fetchMessagesForClientUseCase.init(this,uriInfo)
 * .messagesFor(account.getAccountId())
 * .getMessages(new SearchParams(direction, receiver, sender, sent, index));
 *
 * @author andy
 *
 */
public class FetchMessagesUseCase {

    private final PeppolMessageRepository peppolMessageRepository;

    MessagesDataProvider messagesDataProvider;

    UriInfo uriInfo;
    UriLocationAware locationAware;

    @Inject
    FetchMessagesUseCase(PeppolMessageRepository peppolMessageRepository) {
        this.peppolMessageRepository = peppolMessageRepository;
    }

    /**
     * decorates the messages with the self and download document uris
     * @param uriInfo
     * @param locationAware
     * @return
     */
    public FetchMessagesUseCase init(UriLocationAware locationAware, UriInfo uriInfo) {
        this.uriInfo = uriInfo;
        this.locationAware = locationAware;
        return this;
    }

    /**
     * Executes the usecase fetching the messages.
     * @param searchParams The search params are used for navigation and filtering of the result set.
     * @return
     */
    public MessagesQueryResponse getMessages(SearchParams searchParams) {

        if (messagesDataProvider == null) {
            throw new IllegalStateException("Unable to get messages, no dataProvider set.");
        }

        List<MessageWithLocations> messageWithLocationsList = fetchMessagesWithLocation(searchParams);
        final MessagesQueryResponse messagesQueryResponse = new MessagesQueryResponse(messageWithLocationsList);

        // add navigation links if possible
        if (searchParams != null) {
            messagesQueryResponse.setNavigation(getNavigation(searchParams));
        }

        return messagesQueryResponse;
    }

    /**
     * Executes the usecase fetching the messages, without any search parameters.
     * This means the navigation links will not be created.
     * @return
     */
    public MessagesQueryResponse getMessages() {
        //performs the usecase without any search params.
        return getMessages((SearchParams) null);
    }

    /**
     * Executes the usecase fetching the messages, with given direction
     * This means the navigation links will not be created.
     * @return
     */
    public OutboxQueryResponse getOutbox() {
        if (messagesDataProvider == null) {
            throw new IllegalStateException("Unable to get messages, no dataProvider set.");
        }

        List<MessageWithLocations> messageWithLocationsList = fetchMessagesWithLocation(TransferDirection.OUT);
        final OutboxQueryResponse messagesQueryResponse = new OutboxQueryResponse(messageWithLocationsList);

        return messagesQueryResponse;

    }

    /**
     * Executes the usecase fetching the messages, with given direction
     * This means the navigation links will not be created.
     * @return
     */
    public InboxQueryResponse getInbox() {
        if (messagesDataProvider == null) {
            throw new IllegalStateException("Unable to get messages, no dataProvider set.");
        }

        List<MessageWithLocations> messageWithLocationsList = fetchMessagesWithLocation(TransferDirection.IN);
        final InboxQueryResponse messagesQueryResponse = new InboxQueryResponse(messageWithLocationsList);

        return messagesQueryResponse;

    }

    /**
     * Sets up the data provider to retrieve messages for the given ringo account.
     * @param accountId
     * @return
     */
    public FetchMessagesUseCase messagesFor(final AccountId accountId){

        messagesDataProvider = new MessagesDataProvider(){
            @Override
            public Integer getCount(SearchParams searchParams) {
                return peppolMessageRepository.getMessagesCount(accountId, searchParams);
            }

            @Override
            public List<MessageMetaData> getMessages(SearchParams searchParams) {
                return peppolMessageRepository.findMessages(accountId, searchParams);
            }

            @Override
            public List<MessageMetaData> getMessages(TransferDirection direction) {
                if (TransferDirection.IN.equals(direction)) {
                    return peppolMessageRepository.findUndeliveredInboundMessagesByAccount(accountId);
                } else {
                    return peppolMessageRepository.findUndeliveredOutboundMessagesByAccount(accountId);
                }
            }

        };

        return this;
    }

    /**
     * Sets up the data provider to retrieve all incoming messages which do not have an associated accountId
     * @return
     */
    public FetchMessagesUseCase messagesWithoutAccountId() {
        messagesDataProvider = new MessagesDataProvider(){
            @Override
            public Integer getCount(SearchParams searchParams) {
                //not supported
                throw new IllegalStateException("Unable to get the count for messages without account id");
            }

            @Override
            public List<MessageMetaData> getMessages(SearchParams searchParams) {
                //search params are not used.
                return peppolMessageRepository.findMessagesWithoutAccountId();
            }

            @Override
            public List<MessageMetaData> getMessages(TransferDirection direction) {
                //we don't want to get messages without account id without account
                return null;
            }
        };

        return this;
    }



    /**
     * Fetches a batch of messages and then adds the links to self and download
     * @param searchParams
     * @return
     */
    private List<MessageWithLocations> fetchMessagesWithLocation(SearchParams searchParams) {

        return wrapMessageMetaDataWithLocation(messagesDataProvider.getMessages(searchParams));
    }

    /**
     * Fetches a batch of messages and then adds the links to self and download
     * @param transferDirection
     * @return
     */
    private List<MessageWithLocations> fetchMessagesWithLocation(TransferDirection transferDirection) {
        return wrapMessageMetaDataWithLocation(messagesDataProvider.getMessages(transferDirection));
    }

    /**
     * Wraps retrieve messages with location
     * @param messages
     * @return
     */
    private List<MessageWithLocations> wrapMessageMetaDataWithLocation(List<MessageMetaData> messages) {
        List<MessageWithLocations> messageWithLocationsList = new ArrayList<MessageWithLocations>();
        for (MessageMetaData message : messages) {
            //add the uris for self and download
            messageWithLocationsList.add(locationAware.decorateWithLocators(message, uriInfo));
        }
        return messageWithLocationsList;

    }

    /**
     * Determins the maximum number of pages for the total number of messages in the result set,
     * and determins given the current page number whether or not a previous and or next location uri
     * needs to be generated.
     * @return
     */
    protected Navigation getNavigation(SearchParams searchParams) {
        final int messagesCount = messagesDataProvider.getCount(searchParams);
        final int currentPageIndex = searchParams.getPageIndex();
        //find the maximum number of pages for the result set of this size
        // e.g. ceil(51 - 1 / 25) + 1 ==> 3 pages
        // e.g. ceil(50 - 1 / 25) + 1 ==> 2 pages
        // e.g. ceil(25 - 1 / 25) + 1 ==> 1 pages
        // e.g. ceil(0 - 1 / 25) + 1 ==> 1 pages
        final int maxPageTotal = (int) Math.ceil((messagesCount-1) / PeppolMessageRepository.DEFAULT_PAGE_SIZE) + 1;

        URI previous = null;
        URI next = null;
        //if we are on page > 1 we can have a previous link
        if (currentPageIndex > 1) {
            //if we are on a page which actually doesnt exist the previous should point to the last page.
            //else we should show the current page - 1
            int previousPageIndex = currentPageIndex > maxPageTotal ? maxPageTotal : currentPageIndex -1;
            previous = locationAware.linkToResource(uriInfo, searchParams, previousPageIndex);
        }

        //if we are on page < MAX we can have a next link
        if (currentPageIndex < maxPageTotal) {
            //if the current page index is somehow negative set it to the first page otherwise increment the page index
            int nextPageIndex = currentPageIndex < 0 ? 1 : currentPageIndex + 1;
            next = locationAware.linkToResource(uriInfo, searchParams, nextPageIndex);
        }

        return new Navigation(previous, next);
    }

    public MessageMetaData findOutBoundMessageByMessageNo(Account account, Long msgNo) {
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, msgNo);
        if (outgoingMessage(messageByMessageNo)) {
            return messageByMessageNo;
        }
        throw new PeppolMessageNotFoundException(msgNo);
    }

    private boolean outgoingMessage(MessageMetaData messageByMessageNo) {
        return TransferDirection.OUT.equals(messageByMessageNo.getTransferDirection());
    }

}

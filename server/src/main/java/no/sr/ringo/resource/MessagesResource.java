package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import no.sr.ringo.account.Account;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.*;
import no.sr.ringo.response.MessagesQueryResponse;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Represents the "messages" resource, which allows clients to GET MessageMetaDataEntity from both inbox and outbox
 * PEPPOL network.
 *
 * @author adam
 */
@Path("/messages")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class MessagesResource extends AbstractResource {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessagesResource.class);

    final PeppolMessageRepository peppolMessageRepository;
    private final FetchDocumentUseCase fetchDocumentUseCase;
    final Account account;
    private final PayloadResponseHelper payloadResponseHelper;
    final ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase;
    final FetchMessagesUseCase fetchMessagesUseCase;

    @Inject
    public MessagesResource(ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase,
                            FetchMessagesUseCase fetchMessagesUseCase,
                            PeppolMessageRepository peppolMessageRepository,
                            FetchDocumentUseCase fetchDocumentUseCase,
                            Account account,
                            UriLocationTool uriLocationTool,
                            PayloadResponseHelper payloadResponseHelper) {
        super(uriLocationTool);
        this.receiveMessageFromClientUseCase = receiveMessageFromClientUseCase;
        this.fetchMessagesUseCase = fetchMessagesUseCase;
        this.peppolMessageRepository = peppolMessageRepository;
        this.fetchDocumentUseCase = fetchDocumentUseCase;
        this.account = account;
        this.payloadResponseHelper = payloadResponseHelper;
    }

    /**
     * Retrieves all messaged from /inbox and /outbox
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/")
    public Response getMessages(@Context UriInfo uriInfo, @QueryParam("sent") String sent, @QueryParam("sender") String sender, @QueryParam("receiver") String receiver, @QueryParam("direction") String direction, @QueryParam("index") String index) {

            MessagesQueryResponse messagesQueryResponse = fetchMessagesUseCase.init(MessagesResource.class, uriInfo)
                    .messagesFor(account.getAccountId())
                    .getMessages(new SearchParams(direction, sender, receiver, sent, index));
            String entity = messagesQueryResponse.asXml();
            return SrResponse.ok().entity(entity).build();

    }

    /**
     * Retreives the message header for the supplied message number.
     *
     * @param msgNoString the numeric message number to retrieve
     * @param uriInfo   URI information provided by the JAX-RS implementation
     * @return a HTTP Response with return code 200 and an XML entity representing the message header, if message found.
     *         Retruns  a 404 (Bad Request) if the message number is invalid. Returns 400 (Not found) if the message number was not found
     *         in the database.
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/{message_no}/")
    public Response getMessage(@PathParam("message_no") String msgNoString, @Context UriInfo uriInfo) {

        MessageNumber msgNo;
        if (msgNoString == null || msgNoString.trim().length() == 0) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        MessageMetaData messageMetaDataWithLocator = peppolMessageRepository.findMessageByMessageNo(account, msgNo);

        return createSingleMessageResponse(uriInfo, messageMetaDataWithLocator,this.getClass());

    }

    /**
     * Retrieves the PEPPOL XML Document in XML format, without the header stuff.
     *
     * @param msgNoString identifies the message to which the xml document is associated.
     * @return
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/{message_no}/xml-document")
    public Response getXmlDocument(@PathParam("message_no") String msgNoString) {

        MessageNumber msgNo;
        if (msgNoString == null || msgNoString.trim().length() == 0) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        LOGGER.debug("Retrieving document " + msgNo + " for account " + account);

        // The actual retrieval of the payload is delegated as the payload URI might need to be rewritten and
        // the caller redirected.
        return payloadResponseHelper.fetchPayloadAndProduceResponse(fetchDocumentUseCase, account, msgNo);
    }


    @GET
    @Produces(RingoMediaType.TEXT_PLAIN)
    @Path("/count")
    public Response getCount() {

        Integer count = peppolMessageRepository.getInboxCount(account.getAccountId());
        return SrResponse.ok().entity(count.toString()).build();
    }
}

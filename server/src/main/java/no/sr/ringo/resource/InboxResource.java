package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import no.sr.ringo.account.Account;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.*;
import no.sr.ringo.response.InboxQueryResponse;
import no.sr.ringo.response.SingleInboxResponse;
import no.sr.ringo.transport.TransferDirection;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Represents the "inbox" resource, which allows clients to GET messages
 *
 * @author $Author$ (of last change)
 */
@Path("/inbox")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class InboxResource extends AbstractResource {

    private final Account account;
    private final PeppolMessageRepository peppolMessageRepository;
    private final FetchMessagesUseCase fetchMessagesUseCase;
    private final FetchDocumentUseCase fetchDocumentUseCase;
    private final PayloadResponseHelper payloadResponseHelper;

    @Inject
    public InboxResource(PeppolMessageRepository peppolMessageRepository,
                         Account account,
                         FetchMessagesUseCase fetchMessagesUseCase,
                         FetchDocumentUseCase fetchDocumentUseCase,
                         UriLocationTool uriLocationTool,
                         PayloadResponseHelper payloadResponseHelper) {
        super(uriLocationTool);
        this.peppolMessageRepository = peppolMessageRepository;
        this.account = account;
        this.fetchMessagesUseCase = fetchMessagesUseCase;
        this.fetchDocumentUseCase = fetchDocumentUseCase;
        this.payloadResponseHelper = payloadResponseHelper;
    }

    /**
     * Retrieves the unread messages from the /inbox
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/")
    public Response getMessages(@Context UriInfo uriInfo) {

        InboxQueryResponse inboxQueryResponse = fetchMessagesUseCase.init(this.getClass(), uriInfo)
                .messagesFor(account.getAccountId())
                .getInbox();

        String entity = inboxQueryResponse.asXml();
        return SrResponse.ok().entity(entity).build();

    }

    /**
     * Retreives the message header for the supplied message number.
     *
     * @param msgNoString the numeric message number to retrieve
     * @param uriInfo     URI information provided by the JAX-RS implementation
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

        MessageMetaData messageMetaData = peppolMessageRepository.findMessageByMessageNo(account, msgNo);
        if (!messageMetaData.getTransferDirection().equals(TransferDirection.IN)) {
            return SrResponse.status(Response.Status.NOT_FOUND, "Inbound message number " + msgNoString + " not found");
        }

        return createSingleInboxResponse(uriInfo, messageMetaData);

    }

    @GET
    @Produces(RingoMediaType.TEXT_PLAIN)
    @Path("/count")
    public Response getCount() {

        Integer count = peppolMessageRepository.getInboxCount(account.getAccountId());
        return SrResponse.ok().entity(count.toString()).build();

    }

    /**
     * Retrieves the PEPPOL XML Document in XML format, without the header stuff.
     *
     * @param msgNoString identifies the message to which the xml document is associated.
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

        return payloadResponseHelper.fetchPayloadAndProduceResponse(fetchDocumentUseCase, account, msgNo);
    }

    
    @POST
    @Path("/{message_no}/read")
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response markAsRead(@PathParam("message_no") String msgNoString, @Context UriInfo uriInfo) {

        MessageNumber msgNo;
        if (msgNoString == null || msgNoString.trim().length() == 0) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        MessageMetaData messageMetaDataWithLocators;
        messageMetaDataWithLocators = peppolMessageRepository.findMessageByMessageNo(account, msgNo);
        if (!messageMetaDataWithLocators.getTransferDirection().equals(TransferDirection.IN)) {
            return SrResponse.status(Response.Status.NOT_FOUND, "Inbound message number " + msgNoString + " not found");
        }

        peppolMessageRepository.markMessageAsRead(msgNo.toLong());

        return createSingleMessageResponse(uriInfo, messageMetaDataWithLocators,this.getClass());

    }

    /**
     * Creates the XML response holding the data from the message, including a link to the attached xml message document and a link pointing back to "self".
     * The actual PEPPOL XML message, is not included due to it's size.
     *
     * @param uriInfo         the UriInfo object provided by the JAX-RS container upon invocation.
     * @param messageMetaData the message containing the header and the xml document message.
     * @return a JAX-RS response holding the status code, 200 OK, and the peppol message as a xml entity.
     */
    private Response createSingleInboxResponse(UriInfo uriInfo, MessageMetaData messageMetaData) {

        //Decorates the message with self and xml document uris
        MessageWithLocations messageWithLocations = uriLocationTool.decorateWithLocators(messageMetaData, uriInfo,this.getClass());

        //Creates the response
        SingleInboxResponse messageResponse = new SingleInboxResponse(messageWithLocations);

        //format the response as an XML String
        String entity = messageResponse.asXml();

        // Shoves the URI of this message into the HTTP header "Location" and supplies the XML response as the entity
        return SrResponse.ok().entity(entity).build();

    }
}

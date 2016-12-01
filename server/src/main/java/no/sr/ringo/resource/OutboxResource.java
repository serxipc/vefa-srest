package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.jersey.spi.container.ResourceFilters;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.document.PeppolDocument;
import no.sr.ringo.message.*;
import no.sr.ringo.response.OutboxPostResponse;
import no.sr.ringo.response.OutboxQueryResponse;
import no.sr.ringo.response.SingleOutboxResponse;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;

/**
 * Represents the "outbox" resource, which allows clients to POST outboundMesssageMetaData messages destined for a recipient in the
 * PEPPOL network.
 *
 * @author $Author$ (of last change)
 *         Created by
 *         User: steinar
 *         Date: 23.11.11
 *         Time: 22:35
 */
@Path("/outbox")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class OutboxResource extends AbstractMessageResource {

    private static Logger logger = LoggerFactory.getLogger(OutboxResource.class);

    private final Account account;
    private final ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase;
    private final FetchMessagesUseCase fetchMessagesUseCase;
    private final FetchDocumentUseCase fetchDocumentUseCase;

    @Inject
    OutboxResource(ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase, Account account, FetchMessagesUseCase fetchMessagesUseCase, FetchDocumentUseCase fetchDocumentUseCase) {
        super();
        this.receiveMessageFromClientUseCase = receiveMessageFromClientUseCase;
        this.account = account;
        this.fetchMessagesUseCase = fetchMessagesUseCase;
        this.fetchDocumentUseCase = fetchDocumentUseCase;
    }


    /**
     * Retrieves the unread messages from the /outbox
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/")
    public Response getMessages(@Context UriInfo uriInfo) {

        OutboxQueryResponse outboxQueryResponse = fetchMessagesUseCase.init(this, uriInfo)
                .messagesFor(account.getAccountId())
                .getOutbox();
        String entity = outboxQueryResponse.asXml();

        logger.debug("Returning:\n" + entity);
        return SrResponse.ok().entity(entity).build();

    }

    /**
     * Retrieves the message header for the supplied message number.
     *
     * @param msgNoString the numeric message number to retrieve
     * @param uriInfo   URI information provided by the JAX-RS implementation
     * @return a HTTP Response with return code 200 and an XML entity representing the message header, if message found.
     *         Retruns  a 404 (Bad Request) if the message number is invalid. Returns 204 (NO_CONTENT) if the message number was not found
     *         in the database.
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/{message_no}/")
    public Response getMessage(@PathParam("message_no") String msgNoString, @Context UriInfo uriInfo) {

        MessageNumber msgNo = null;

        if (isEmpty(msgNoString)) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        MessageMetaData messageMetaData = fetchMessagesUseCase.findOutBoundMessageByMessageNo(account, msgNo.toLong());
        return createSingleOutboxResponse(uriInfo, messageMetaData);
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
    public Response downloadPeppolDocument(@PathParam("message_no") String msgNoString) {

        MessageNumber msgNo = null;

        if (isEmpty(msgNoString)) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        PeppolDocument xmlDocument = fetchDocumentUseCase.execute(account, msgNo);

        return Response.ok().entity(xmlDocument.getXml()).build();
    }

    /**
     * Receives a new message to be POST'ed (sent) into the PEPPOL network. The header and the actual XML document is
     * persisted to the database along with a timestamp, specifying that the transfer direction is outboundMesssageMetaData TransferDirection#OUT.
     * <p/>
     * The sender of the message must be associated with the SR account specified during HTTP Basic Authentication.
     *
     * @param channelIdString              the textual representation of the channel to which message should be associated.
     * @param recipientIdString            the PEPPOL participant identification of the recipient of the message.
     * @param senderIdString               the PEPPOL participant identification of the sender of the message.
     * @param processIdString              the PEPPOL process identification acronym
     * @param documentIdString             the PEPPOL document type identification acronym
     * @param inputStream            input stream of the http entity (the contents), i.e. the XML document
     * @param dataContentDisposition content disposition for the XML document
     * @param uriInfo                information about this URI
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response post(
            @FormDataParam("ChannelID") final String channelIdString,
            @FormDataParam("RecipientID") final String recipientIdString,
            @FormDataParam("SenderID") final String senderIdString,
            @FormDataParam("ProcessID") final String processIdString,
            @FormDataParam("DocumentID") final String documentIdString,
            @FormDataParam("UploadMode") final String uploadMode,
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition dataContentDisposition,
            @Context UriInfo uriInfo,
            @Context HttpHeaders headers) {

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Handling outbox upload '%s' '%s' '%s' '%s' '%s' '%s'", recipientIdString, senderIdString, processIdString, documentIdString, channelIdString, uploadMode));
        }

        OutboundPostParams postParams = new OutboundPostParams.Builder()
                .senderId(senderIdString)
                .recipientId(recipientIdString)
                .processId(processIdString)
                .documentId(documentIdString)
                .channelId(channelIdString)
                .inputStream(inputStream)
                .uploadMode(uploadMode)
                .fileName(dataContentDisposition.getFileName())
                .build();

        //perform params validation as well as smp lookup; create message when validation successful
        MessageWithLocations messageWithLocations = null;
        try {
            messageWithLocations = receiveMessageFromClientUseCase.handleMessage(postParams);
        } catch (Exception e) {
            return SrResponse.status(Response.Status.BAD_REQUEST, e.getMessage());

        }

        // Provides a nice response
        return createOutboxPostMessageResponse(uriInfo, messageWithLocations);

    }

    /**
     * Creates the XML response to POST holding the data from the message, including a link to the attached xml message document and a link pointing back to "self".
     * The actual PEPPOL XML message, is not included due to it's size.
     *
     * @param uriInfo         the UriInfo object provided by the JAX-RS container upon invocation.
     * @param messageMetaData the message containing the header and the xml document message.
     * @return a JAX-RS response holding the status code, 201 Created, and the peppol message as a xml entity.
     */
    Response createOutboxPostMessageResponse(UriInfo uriInfo, MessageMetaData messageMetaData) {

        final MessageWithLocations messageMetaDataWithLocations = decorateWithLocators(messageMetaData, uriInfo);

        //Creates the response
        OutboxPostResponse messageResponse = new OutboxPostResponse(messageMetaDataWithLocations);

        // Shoves the URI of this message into the HTTP header "Location" and supplies the XML response as the entity
        return SrResponse.created(messageMetaDataWithLocations.getSelfURI()).entity(messageResponse.asEntity()).build();
    }

    /**
     * Creates the XML response holding the data from the message, including a link to the attached xml message document and a link pointing back to "self".
     * The actual PEPPOL XML message, is not included due to it's size.
     *
     * @param uriInfo         the UriInfo object provided by the JAX-RS container upon invocation.
     * @param messageMetaData the message containing the header and the xml document message.
     * @return a JAX-RS response holding the status code, 200 OK, and the peppol message as a xml entity.
     */
    Response createSingleOutboxResponse(UriInfo uriInfo, MessageMetaData messageMetaData) {

        MessageWithLocations messageWithLocations = decorateWithLocators(messageMetaData,uriInfo);

        //Creates the response
        SingleOutboxResponse messageResponse = new SingleOutboxResponse(messageWithLocations);

        //format the response as an XML String
        String entity = messageResponse.asXml();

        // Shoves the URI of this message into the HTTP header "Location" and supplies the XML response as the entity
        return SrResponse.ok().entity(entity).build();
    }
}

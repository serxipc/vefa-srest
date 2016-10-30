package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.document.PeppolDocument;
import no.sr.ringo.message.*;
import no.sr.ringo.response.MessagesQueryResponse;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Represents the "messages" resource, which allows clients to GET MessageMetaData from both inbox and outbox
 * PEPPOL network.
 *
 * @author adam
 */
@Path("/messages")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class MessagesResource extends AbstractMessageResource {

    final PeppolMessageRepository peppolMessageRepository;
    private final FetchDocumentUseCase fetchDocumentUseCase;
    final Account account;
    final ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase;
    final FetchMessagesUseCase fetchMessagesUseCase;

    @Inject
    public MessagesResource(ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase, FetchMessagesUseCase fetchMessagesUseCase, PeppolMessageRepository peppolMessageRepository,FetchDocumentUseCase fetchDocumentUseCase, Account account) {
        super();
        this.receiveMessageFromClientUseCase = receiveMessageFromClientUseCase;
        this.fetchMessagesUseCase = fetchMessagesUseCase;
        this.peppolMessageRepository = peppolMessageRepository;
        this.fetchDocumentUseCase = fetchDocumentUseCase;
        this.account = account;
    }

    /**
     * Retrieves all messaged from /inbox and /outbox
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/")
    public Response getMessages(@Context UriInfo uriInfo, @QueryParam("sent") String sent, @QueryParam("sender") String sender, @QueryParam("receiver") String receiver, @QueryParam("direction") String direction, @QueryParam("index") String index) {

            MessagesQueryResponse messagesQueryResponse = fetchMessagesUseCase.init(this, uriInfo)
                    .messagesFor(account.getId())
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

        MessageMetaData messageMetaDataWithLocator = peppolMessageRepository.findMessageByMessageNo(account, msgNo.toLong());
        return createSingleMessageResponse(uriInfo, messageMetaDataWithLocator);

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

        PeppolDocument xmlDocument = fetchDocumentUseCase.execute(account, msgNo);
        return SrResponse.ok().entity(xmlDocument.getXml()).build();

    }

    /**
     * Retrieves the PEPPOL XML Document in XML format, with added stylesheet (intended for web viewing on our site).
     *
     * @param msgNoString identifies the message to which the xml document is associated.
     * @return
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/{message_no}/xml-document-decorated")
    public Response getXmlDocumentDecorated(@PathParam("message_no") String msgNoString) {

        MessageNumber msgNo;
        if (msgNoString == null || msgNoString.trim().length() == 0) {
            return SrResponse.status(Response.Status.BAD_REQUEST, "No message number given");
        } else {
            msgNo = parseMsgNo(msgNoString);
        }

        PeppolDocument xmlDocument = fetchDocumentUseCase.executeWithDecoration(account, msgNo);
        return SrResponse.ok().entity(xmlDocument.getXml()).build();

    }

    @GET
    @Produces(RingoMediaType.TEXT_PLAIN)
    @Path("/count")
    public Response getCount() {

        Integer count = peppolMessageRepository.getInboxCount(account.getId());
        return SrResponse.ok().entity(count.toString()).build();

    }

}

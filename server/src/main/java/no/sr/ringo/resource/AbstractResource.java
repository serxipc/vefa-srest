/* Created by steinar on 08.01.12 at 20:41 */
package no.sr.ringo.resource;

import no.sr.ringo.account.Account;
import no.sr.ringo.document.FetchDocumentResult;
import no.sr.ringo.document.FetchDocumentResultVisitorImpl;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.SingleMessagesResponse;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Abstract REST resource.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public abstract class AbstractResource  {

    protected final UriLocationTool uriLocationTool;

    AbstractResource(UriLocationTool uriLocationTool) {
        this.uriLocationTool = uriLocationTool;
    }

    /**
     * Creates the XML response holding the data from the message, including a link to the attached xml message document and a link pointing back to "self".
     * The actual PEPPOL XML message, is not included due to it's size.
     *
     * This method is in Abstract class to make it available in both Inbox and Message resources
     *
     * @param uriInfo         the UriInfo object provided by the JAX-RS container upon invocation.
     * @param messageMetaData the message containing the header and the xml document message.
     * @return a JAX-RS response holding the status code, 200 OK, and the peppol message as a xml entity.
     */
    protected Response createSingleMessageResponse(UriInfo uriInfo, MessageMetaData messageMetaData,Class<? extends AbstractResource> resourceClass) {

        // Messages with locators.
        MessageWithLocations messageWithLocations = uriLocationTool.decorateWithLocators(messageMetaData,uriInfo, resourceClass);

        // Creates the response
        SingleMessagesResponse messageResponse = new SingleMessagesResponse(messageWithLocations);

        // format the response as an XML String
        String entity = messageResponse.asXml();

        // Shoves the URI of this message into the HTTP header "Location" and supplies the XML response as the entity
        return SrResponse.ok().entity(entity).build();
    }

    /**
     * Tries to parse pageIndex into integer
     * @param pageIndex
     * @return parsed index or 1 if parameter not specified
     */
    protected Integer parsePageIndex(String pageIndex) {
        Integer result = 1;
        if (pageIndex != null && pageIndex.trim().length() > 0) {
            try {
                result = Integer.parseInt(pageIndex);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Invalid page index '%s'", pageIndex));
            }
        }

        return result;

    }

    /**
     * Parses msgNo to Integer and throws new syntax exception if fails
     * @param msgNoString
     * @return
     */
    protected MessageNumber parseMsgNo(String msgNoString) {
        try {
            return MessageNumber.of(msgNoString);
        } catch (IllegalArgumentException nfe) {
            throw new InvalidUserInputWebException(String.format("Invalid message number '%s'", msgNoString));
        }
    }

    private UriBuilder getUriBuilderForResource(UriInfo uriInfo, Class<? extends AbstractResource> resource) {
        return uriInfo.getBaseUriBuilder()  // Gets the base URI of the application in the form of a UriBuilder
                .path(resource)   // Appends the path from @Path-annotated class to the existing path
                .scheme("https"); // Must use https (we are always behind a firewall)
    }

    protected boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }


    static Response fetchPayloadAndProduceResponse(FetchDocumentUseCase fetchDocumentUseCase, Account account, MessageNumber msgNo) {
        // Retrieves either the document itself or a reference to where it is located
        FetchDocumentResult fetchDocumentResult = fetchDocumentUseCase.findDocument(account, msgNo);

        // Uses the visitor pattern to produce a Response, which will either contain
        // the xml text or an http 303 (see other) response.
        return fetchDocumentResult.accept(new FetchDocumentResultVisitorImpl());
    }
}

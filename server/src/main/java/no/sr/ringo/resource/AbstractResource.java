/* Created by steinar on 08.01.12 at 20:41 */
package no.sr.ringo.resource;

import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.SingleMessagesResponse;

import javax.ws.rs.core.Response;
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
     * Parses msgNo to Integer and throws new syntax exception if fails
     *
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

}

/* Created by steinar on 08.01.12 at 20:41 */
package no.sr.ringo.resource;

import no.sr.ringo.message.*;
import no.sr.ringo.response.SingleMessagesResponse;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public abstract class AbstractMessageResource implements UriLocationAware {

    AbstractMessageResource() {
    }

    @Override
    public MessageWithLocations decorateWithLocators(MessageMetaData messageMetaData, UriInfo uriInfo) {

        // Creates the link for viewing this message again http....../inbox|messages|outbox/{msgno}
        URI self = computeUriForSelf(uriInfo, messageMetaData.getMsgNo());

        // Creates the link for downloading the peppol message http....../inbox|messages|outbox/{msgno}/xml-document
        URI documentUri = computeXmlDocumentUri(uriInfo, messageMetaData.getMsgNo());

        //Creates the object which contains the links to self and the peppol document
        return new MessageWithLocationsImpl(messageMetaData,self,documentUri);
    }

    @Override
    public URI linkToResource(UriInfo uriInfo, SearchParams searchParams, int pageIndex) {
        // https....../XXXbox
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, this.getClass());

        // https....../XXXbox?
        final UriBuilder resourceUriBuilder = uriBuilder.clone().queryParam("index", pageIndex);
        searchParams.appendTo(resourceUriBuilder);

        return resourceUriBuilder.build();
    }

    protected URI computeXmlDocumentUri(UriInfo uriInfo, Long msgNo) {
        // https....../outbox
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, MessagesResource.class);

        // https....../outbox/{msgno}/xml-document
        URI documentUri = uriBuilder.clone().path("/{msgno}/xml-document").build(msgNo.toString());


        return documentUri;
    }

    protected URI computeUriForSelf(UriInfo uriInfo, Long msgNo) {

        // https....../outbox
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, this.getClass());

        // https....../outbox/{msgno}
        String messageNumberAsString = msgNo.toString();
        URI self = uriBuilder.clone().path("/{msgno}").build(messageNumberAsString);

        return self;
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
    protected Response createSingleMessageResponse(UriInfo uriInfo, MessageMetaData messageMetaData) {

        //Messages with locators.
        MessageWithLocations messageWithLocations = decorateWithLocators(messageMetaData,uriInfo);

        //Creates the response
        SingleMessagesResponse messageResponse = new SingleMessagesResponse(messageWithLocations);

        //format the response as an XML String
        String entity = messageResponse.asXml();

        // Shoves the URI of this message into the HTTP header "Location" and supplies the XML response as the entity
        return SrResponse.ok().entity(entity).build();
    }

    /**
     * Tries to parse pageIndexinto integer
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
            return MessageNumber.valueOf(msgNoString);
        } catch (IllegalArgumentException nfe) {
            throw new InvalidUserInputWebException(String.format("Invalid message number '%s'", msgNoString));
        }
    }

    private UriBuilder getUriBuilderForResource(UriInfo uriInfo, Class<? extends AbstractMessageResource> resource) {
        return uriInfo.getBaseUriBuilder()
                .path(resource)
                .scheme("https"); // Must use https (we are always behind a firewall)
    }

    protected boolean isEmpty(String value) {
        return StringUtils.isBlank(value);
    }
}

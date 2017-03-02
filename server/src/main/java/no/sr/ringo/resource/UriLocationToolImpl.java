package no.sr.ringo.resource;

import no.sr.ringo.message.*;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * @author steinar
 *         Date: 01.03.2017
 *         Time: 21.51
 */
public class UriLocationToolImpl implements UriLocationTool {

    static protected URI computeXmlDocumentUri(UriInfo uriInfo, MessageNumber msgNo) {
        // https....../messages
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, MessagesResource.class);

        // https....../messages/{msgno}/xml-document
        URI documentUri = uriBuilder.clone().path("/{msgno}/xml-document").build(msgNo.toString());

        return documentUri;
    }

    static protected URI computeUriForSelf(UriInfo uriInfo, MessageNumber msgNo, Class<? extends AbstractResource> resourceClass) {

        // https....../outbox
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, resourceClass);

        // https....../outbox/{msgno}
        String messageNumberAsString = msgNo.toString();
        URI self = uriBuilder.clone().path("/{msgno}").build(messageNumberAsString);

        return self;
    }

    /**
     * Produces a UriBuilder referencing the root of the supplied resource class
     */
    static private UriBuilder getUriBuilderForResource(UriInfo uriInfo, Class<? extends AbstractResource> resource) {
        return uriInfo.getBaseUriBuilder()  // Gets the base URI of the application in the form of a UriBuilder
                .path(resource)   // Appends the path from @Path-annotated class to the existing path
                .scheme("https"); // Must use https (we are always behind a firewall)
    }

    @Override
    public MessageWithLocations decorateWithLocators(MessageMetaData messageMetaData, UriInfo uriInfo, Class<? extends AbstractResource> resourceClass) {

        // Creates the link for viewing this message again http....../inbox|messages|outbox/{msgno}
        URI self = computeUriForSelf(uriInfo, messageMetaData.getMsgNo(), resourceClass);

        // Creates the link for downloading the peppol message http....../inbox|messages|outbox/{msgno}/xml-document
        URI documentUri = computeXmlDocumentUri(uriInfo, messageMetaData.getMsgNo());

        //Creates the object which contains the links to self and the peppol document
        return new MessageWithLocationsImpl(messageMetaData, self, documentUri);
    }

    @Override
    public URI linkToResource(UriInfo uriInfo, SearchParams searchParams, int pageIndex, Class<? extends AbstractResource> resourceClass) {

        if (resourceClass == null) {
            throw new NullPointerException("Required argument resourceClass is null");
        }

        if (uriInfo == null) {
            throw new NullPointerException("Required argument UriInfo");
        }
        // https....../XXXbox
        UriBuilder uriBuilder = getUriBuilderForResource(uriInfo, resourceClass);

        // https....../XXXbox?
        final UriBuilder resourceUriBuilder = uriBuilder.clone().queryParam("index", pageIndex);
        searchParams.appendTo(resourceUriBuilder);

        return resourceUriBuilder.build();
    }

}

package no.sr.ringo.resource;

import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.SearchParams;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * User: andy
 * Date: 2/21/12
 * Time: 12:35 PM
 */
public interface UriLocationTool {

    /**
     * Adds URLs to the message meta data for self and downloading the message.
     *
     * @param messageMetaData
     * @param uriInfo
     * @return
     */
    MessageWithLocations decorateWithLocators(MessageMetaData messageMetaData, UriInfo uriInfo, Class<? extends AbstractResource> resourceClass);

    /**
     * Adds a URL for navigating to the provided page index.
     *
     * @param uriInfo Information about the current request
     * @param searchParams Search parameters which should be attached to the generated URI
     * @param pageIndex the page index to use.
     * @return
     */
    URI linkToResource(UriInfo uriInfo, SearchParams searchParams, int pageIndex,Class<? extends AbstractResource> resourceClass);
}

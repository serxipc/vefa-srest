package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.spi.container.*;
import no.sr.ringo.common.RingoConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jersey server side response filter implementation, which is used to inspect the version number being passed from
 * the client, comparing it to what the server thinks is the current version.
 *
 * The client reports it's version number and the server determines whether it needs to be updated or not.
 *
 * User: andy
 * Date: 2/24/12
 * Time: 3:52 PM
 */
@Singleton
public class ClientVersionNumberResponseFilter implements ResourceFilter, ContainerResponseFilter {

    static final Logger log = LoggerFactory.getLogger(ClientVersionNumberResponseFilter.class);

    private final ClientVersionHelper clientVersionHelper;

    @Inject
    public ClientVersionNumberResponseFilter(ClientVersionHelper clientVersionHelper) {
        this.clientVersionHelper = clientVersionHelper;
    }

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        final String userAgent = containerRequest.getHeaderValue("User-Agent");
        //checks the version if the client is out of date add a link to the download
        log.info("User-Agent {} requested {}", userAgent, containerRequest.getAbsolutePath().toString());
        if (clientVersionHelper.isOutOfDate(userAgent)) {
            containerResponse.getHttpHeaders().add(RingoConstants.CLIENT_DOWNLOAD_HEADER, RingoConstants.CLIENT_DOWNLOAD_URL);
        }
        return containerResponse;
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return null;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return this;
    }

}

package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.response.SmpLookupResponse;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.SmpLookupResult;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Represents the "directory" resource, which allows clients to check whether participant is registered in peppol network.
 *
 * @author adam
 * @author thore
 */
@Path("/directory")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class DirectoryResource {

    private final RingoSmpLookup ringoSmpLookup;
    private final Account account;

    @Inject
    public DirectoryResource(RingoSmpLookup ringoSmpLookup, Account account) {
        this.ringoSmpLookup = ringoSmpLookup;
        this.account = account;
    }

    /**
     * Checks whether participant is registered in SMP
     */
    @GET
    @Path("/{participantId}/")
    public Response isRegistered(@Context final UriInfo uriInfo, @PathParam("participantId") final String peppolParticipantId) {
        PeppolParticipantId participantId = PeppolParticipantId.valueOf(peppolParticipantId);
        if (participantId == null) {
            return SrResponse.status(Response.Status.BAD_REQUEST, String.format("Invalid peppol participant id '%s'", peppolParticipantId));
        }
        final boolean registered = ringoSmpLookup.isRegistered(participantId);
        return registered ? SrResponse.ok().build() : Response.noContent().build();
    }

    /**
     * Retrieves acceptable document types for given participant and localName
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/{participantId}/{localName}")
    public Response getDocumentTypes(@Context final UriInfo uriInfo, @PathParam("participantId") final String peppolParticipantId, @PathParam("localName") final String localNameString) {
        PeppolParticipantId participantId = PeppolParticipantId.valueOf(peppolParticipantId);
        if (participantId == null) {
            return SrResponse.status(Response.Status.BAD_REQUEST, String.format("Invalid peppol participant id '%s'", peppolParticipantId));
        }
        LocalName localName = LocalName.valueOf(localNameString);
        SmpLookupResult smpLookupResult = ringoSmpLookup.fetchSmpMetaData(participantId, localName);
        if (smpLookupResult.getAcceptedDocumentTypes().isEmpty()){
            return Response.noContent().build();
        } else {
            String entity = new SmpLookupResponse(smpLookupResult).asXml();
            return SrResponse.ok().entity(entity).build();
        }
    }

}

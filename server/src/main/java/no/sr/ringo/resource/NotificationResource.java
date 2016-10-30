package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.email.EmailService;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Exposing resources allowing to send notification emails when something goes wrong in ringo client
 *
 * @author adam
 */
@Path("/notify")
@RequestScoped
public class NotificationResource {

    private final Account account;
    private final EmailService emailService;

    @Inject
    public NotificationResource(Account account, EmailService emailService) {
        super();
        this.account = account;
        this.emailService = emailService;
    }

    @POST
    @Path("/batchUploadError")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response batchUploadError(@FormParam("commandLine") String commandLine, @FormParam("errorMessage") String errorMessage, @Context UriInfo uriInfo) {

        if (account != null && account.isSendNotification()) {
            emailService.sendClientBatchUploadErrorNotification(account, commandLine, errorMessage);
        }
        return SrResponse.ok().build();

    }

    @POST
    @Path("/downloadError")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response downloadError(@FormParam("commandLine") String commandLine, @FormParam("errorMessage") String errorMessage, @Context UriInfo uriInfo) {

        if (account != null && account.isSendNotification()) {
            emailService.sendClientDownloadErrorNotification(account, commandLine, errorMessage);
        }

        return SrResponse.ok().build();

    }

}

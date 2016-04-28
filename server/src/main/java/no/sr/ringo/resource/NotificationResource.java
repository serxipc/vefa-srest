package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.account.RingoAccount;

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

    private final RingoAccount ringoAccount;
    private final EmailService emailService;

    @Inject
    public NotificationResource(RingoAccount ringoAccount, EmailService emailService) {
        super();
        this.ringoAccount = ringoAccount;
        this.emailService = emailService;
    }

    @POST
    @Path("/batchUploadError")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response batchUploadError(@FormParam("commandLine") String commandLine, @FormParam("errorMessage") String errorMessage, @Context UriInfo uriInfo) {

        if (ringoAccount != null && ringoAccount.isSendNotification()) {
            emailService.sendClientBatchUploadErrorNotification(ringoAccount, commandLine, errorMessage);
        }
        return SrResponse.ok().build();

    }

    @POST
    @Path("/downloadError")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response downloadError(@FormParam("commandLine") String commandLine, @FormParam("errorMessage") String errorMessage, @Context UriInfo uriInfo) {

        if (ringoAccount != null && ringoAccount.isSendNotification()) {
            emailService.sendClientDownloadErrorNotification(ringoAccount, commandLine, errorMessage);
        }

        return SrResponse.ok().build();

    }

}

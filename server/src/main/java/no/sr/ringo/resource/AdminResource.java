package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.message.FetchMessagesUseCase;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.report.RingoReportUtils;
import no.sr.ringo.report.SendReportUseCase;
import no.sr.ringo.response.MessagesQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Represents the "admin" resource, which allows to look up various statuses
 * PEPPOL network.
 *
 * @author adam
 */
@Path("/admin")
@RequestScoped
public class AdminResource extends AbstractMessageResource {

    private static Logger logger = LoggerFactory.getLogger(AdminResource.class);

    private final Account account;
    private final FetchMessagesUseCase fetchMessagesUseCase;
    private final PeppolMessageRepository peppolMessageRepository;
    private final SendReportUseCase sendReportUseCase;

    @Inject
    public AdminResource(FetchMessagesUseCase fetchMessagesUseCase, Account account, PeppolMessageRepository peppolMessageRepository, SendReportUseCase sendReportUseCase) {
        super();
        this.fetchMessagesUseCase = fetchMessagesUseCase;
        this.account = account;
        this.peppolMessageRepository = peppolMessageRepository;
        this.sendReportUseCase = sendReportUseCase;
    }

    /**
     * Retrieves messages without account_id (messages we do not know who belongs to)
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/status")
    public Response getStatus(@Context UriInfo uriInfo, @Context ServletContext servletContext) {

        MessagesQueryResponse messagesQueryResponse = fetchMessagesUseCase.init(this, uriInfo).messagesWithoutAccountId().getMessages();

        String entity = String.format("<status><is-production-server>%s</is-production-server>%s</status>",
                servletContext.getInitParameter("isProductionServer"),
                messagesQueryResponse.asXml());
        return SrResponse.ok().entity(entity).build();
    }

    /**
     * Send monthly report (for previous month)
     *
     * curl -u username:password https://ringo.domain.com/admin/sendMonthlyReport?email=someone@company.com
     *
     * @param year    The year to report for. If omitted, current year is used. If the current month is January, we use the previous year.
     * @param month   The month to report for. If ommited, previous month is used. If the current month is January, we use December.
     * @param email   The receiver of the report
     * @return A status message
     */
    @GET
    @Path("/sendMonthlyReport")
    @Produces(RingoMediaType.TEXT_PLAIN)
    public Response sendMonthlyReport(@Context ServletContext servletContext, @QueryParam("year") Integer year, @QueryParam("month") Integer month, @QueryParam("email") String email) {


        if(year == null) {
            year = RingoReportUtils.getDefaultYearForReport();
        }

        if(month == null) {
            month = RingoReportUtils.getPreviousMonth();
        }

        if(email == null) {
            return SrResponse.status(Response.Status.BAD_REQUEST, String.format("You must specify parameter email. Year=%d, month=%d", year, month));
        }

        String result = sendReportUseCase.sendReport(year, month, email);

        return SrResponse.ok().entity(String.format("Sent report for year %d, month %d to %s\n\n%s", year, month, email, result)).build();
    }

    @GET
    @Path("/statistics")
    @Produces(RingoMediaType.APPLICATION_XML)
    public Response adminStatistics(){
        final RingoStatistics ringoStatistics = peppolMessageRepository.getAdminStatistics();
        return SrResponse.ok().entity(ringoStatistics.asXml()).build();
    }

}

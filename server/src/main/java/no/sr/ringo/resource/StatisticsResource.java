package no.sr.ringo.resource;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.statistics.RingoStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Start of a resource providing statistics over the inbox, outbox and messages
 * @author andy
 */
@Path("/statistics")
@RequestScoped
public class StatisticsResource extends AbstractMessageResource {

    private static Logger logger = LoggerFactory.getLogger(StatisticsResource.class);

    private final RingoAccount ringoAccount;
    private final PeppolMessageRepository peppolMessageRepository;

    @Inject
    public StatisticsResource(RingoAccount ringoAccount, PeppolMessageRepository peppolMessageRepository) {
        super();
        this.ringoAccount = ringoAccount;
        this.peppolMessageRepository = peppolMessageRepository;
    }

    /**
     * Retrieves messages without account_id
     */
    @GET
    @Produces(RingoMediaType.APPLICATION_XML)
    @Path("/")
    public Response getOverview() {

        final RingoStatistics ringoStatisticsForAccount = peppolMessageRepository.getAccountStatistics(ringoAccount.getId());
        return SrResponse.ok().entity(ringoStatisticsForAccount.asXml()).build();
    }
}

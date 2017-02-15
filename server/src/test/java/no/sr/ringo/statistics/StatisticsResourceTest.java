package no.sr.ringo.statistics;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.statistics.InboxStatistics;
import no.sr.ringo.message.statistics.OutboxStatistics;
import no.sr.ringo.message.statistics.RingoAccountStatistics;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.resource.StatisticsResource;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 9/4/12
 * Time: 2:48 PM
 */
public class StatisticsResourceTest {


    private final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private PeppolMessageRepository mockPeppolMessageRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        mockPeppolMessageRepository = EasyMock.createStrictMock(PeppolMessageRepository.class);
    }

    @Test
    public void testStatisticsResource() throws Exception {

        final Account testAccount = ObjectMother.getTestAccount();
        List<RingoAccountStatistics> accountStatistics = new ArrayList<RingoAccountStatistics>();
        Date lastDeliveredIN = parseDate("2010-01-01");
        Date oldestUndeliveredIN = parseDate("2011-01-01");
        Date lastReceivedIN = parseDate("2012-01-01");

        InboxStatistics inboxStatistics = new InboxStatistics(1, 1, lastDeliveredIN, lastReceivedIN, oldestUndeliveredIN);
        final Date date = new Date();
        OutboxStatistics outboxStatistics = new OutboxStatistics(9, 1, date, date);
        accountStatistics.add(new RingoAccountStatistics(10, inboxStatistics, outboxStatistics, testAccount.getAccountId(), "test", "a@b.com"));
        RingoStatistics statistics = new RingoStatistics(accountStatistics);

        expect(mockPeppolMessageRepository.getAccountStatistics(testAccount.getAccountId())).andStubReturn(statistics);

        replay(mockPeppolMessageRepository);

        StatisticsResource statisticsResource = new StatisticsResource(testAccount, mockPeppolMessageRepository);
        final Response overview = statisticsResource.getOverview();

        final int status = overview.getStatus();
        assertEquals(200, status);

        String xml = (String) overview.getEntity();
        assertEquals(statistics.asXml(), xml);
    }

    /**
     * Parses date in yyyy-MM-dd format
     */
    protected Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }

}

package no.sr.ringo.statistics;

import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.statistics.InboxStatistics;
import no.sr.ringo.message.statistics.OutboxStatistics;
import no.sr.ringo.message.statistics.RingoAccountStatistics;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.resource.AdminResource;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
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
public class AdminStatisticsResourceTest {

    private PeppolMessageRepository mockPeppolMessageRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        mockPeppolMessageRepository = EasyMock.createStrictMock(PeppolMessageRepository.class);
    }

    @Test
    public void testStatisticsResource() throws Exception {

        final Account testAccount = ObjectMother.getTestAccount();
        List<RingoAccountStatistics> accountStatistics = new ArrayList<RingoAccountStatistics>();
        final Date date = new Date();
        InboxStatistics inboxStatistics = new InboxStatistics(1, 1, date, date, date);
        final OutboxStatistics outboxStatistics = new OutboxStatistics(9, 1, date, date);
        accountStatistics.add(new RingoAccountStatistics(10, inboxStatistics, outboxStatistics, testAccount.getAccountId(), "test", "a@b.com"));
        RingoStatistics statistics = new RingoStatistics(accountStatistics);

        expect(mockPeppolMessageRepository.getAdminStatistics()).andStubReturn(statistics);

        replay(mockPeppolMessageRepository);

        AdminResource statisticsResource = new AdminResource(null, testAccount,mockPeppolMessageRepository,null,null);
        final Response overview = statisticsResource.adminStatistics();

        final int status = overview.getStatus();
        assertEquals(200, status);

        String xml = (String) overview.getEntity();
        assertEquals(statistics.asXml(), xml);
    }

}

package no.sr.ringo.message.statistics;

import eu.peppol.persistence.AccountId;
import no.sr.ringo.peppol.RingoUtils;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * Tests the RingoAccountStatisticsDto
 * User: andy
 * Date: 9/5/12
 * Time: 2:29 PM
 */
public class RingoAccountStatisticsTest {


    private final Date date = new Date();
    private final String dateAsString = RingoUtils.formatDateTimeAsISO8601String(date);

    @Test
    public void testToXml() throws Exception {
        final InboxStatistics inboxStatistics = new InboxStatistics(2, 3, date, date, date);
        final OutboxStatistics outboxStatistics = new OutboxStatistics(4, 5, date, date);
        RingoAccountStatistics statistics = new RingoAccountStatistics(1, inboxStatistics, outboxStatistics, new AccountId(1), "Andy", "a@b.com");
        final String xmlString = statistics.toXml();
        assertEquals(xmlString,
                "<account name=\"Andy\" contact_email=\"a@b.com\"><messages total=\"1\"><in total=\"2\" undelivered=\"3\" oldest_undelivered=\"" +
                        dateAsString + "\" last_downloaded=\"" +
                        dateAsString + "\" last_received=\"" +
                        dateAsString + "\"></in><out total=\"4\" undelivered=\"5\" last_sent=\"" +
                        dateAsString + "\" last_received=\"" +
                        dateAsString + "\"></out></messages></account>");
    }

    @Test
    public void testXmlEscaping() throws Exception {
        final InboxStatistics inboxStatistics = new InboxStatistics(2, 3, date, date, date);
        final OutboxStatistics outboxStatistics = new OutboxStatistics(4, 5, date, date);
        RingoAccountStatistics statistics = new RingoAccountStatistics(1, inboxStatistics, outboxStatistics, new AccountId(1), "Andy & \"sons\"", "da@b.com");
        final String xmlString = statistics.toXml();
        assertEquals(xmlString,"<account name=\"Andy &amp; &quot;sons&quot;\" contact_email=\"da@b.com\"><messages total=\"1\"><in total=\"2\" undelivered=\"3\" oldest_undelivered=\""+
                dateAsString + "\" last_downloaded=\"" +
                dateAsString + "\" last_received=\"" +
                dateAsString +"\"></in><out total=\"4\" undelivered=\"5\" last_sent=\""+
                dateAsString + "\" last_received=\"" +
                dateAsString +"\"></out></messages></account>");
    }


    @Test
    /**
     * Simply checks that getLastRecevied uses the received property from the inbox
     */
    public void testRecievedFromPeppol() throws Exception {
        final InboxStatistics inboxStatistics = new InboxStatistics(2, 3, null, date, date);
        RingoAccountStatistics statistics = new RingoAccountStatistics(10, inboxStatistics, null, null, null, null);
        assertEquals(statistics.getLastReceived(),date);
    }


    @Test
    /**
     * Simply checks that getLastUploaded uses the received property from the outbox
     */
    public void testRecievedFromCustomer() throws Exception {
        final OutboxStatistics outboxStatistics = new OutboxStatistics(2, 3, null, date);
        RingoAccountStatistics statistics = new RingoAccountStatistics(10, null, outboxStatistics, null, null, null);
        assertEquals(statistics.getLastUploaded(),date);
    }

}

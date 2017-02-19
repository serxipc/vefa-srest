/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.message.statistics.RingoAccountStatistics;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

@Guice(moduleFactory = ServerTestModuleFactory.class)
public class PeppolMessageRepositoryImplStatisticsIntegrationTest {

    private final PeppolMessageRepository peppolMessageRepository;
    private final DatabaseHelper databaseHelper;
    Logger logger = LoggerFactory.getLogger(PeppolMessageRepositoryImplStatisticsIntegrationTest.class);
    private Account account = ObjectMother.getTestAccount();
    private ParticipantId participantId = ObjectMother.getTestParticipantIdForSMPLookup();

    private Date sentDate;
    private Date downloadedDate;
    private Date receivedDate;
    private Date oldestUndeliveredDate;

    @Inject
    public PeppolMessageRepositoryImplStatisticsIntegrationTest(PeppolMessageRepository peppolMessageRepository, DatabaseHelper databaseHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.databaseHelper = databaseHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void setup() throws ParseException {
        databaseHelper.deleteAllMessagesForAccount(account);
        sentDate = parseDate("2012-01-01");
        downloadedDate = parseDate("2012-01-02");
        receivedDate = parseDate("2012-01-03");
        oldestUndeliveredDate = parseDate("2010-01-03");
        createTestMessages();
    }

    @AfterMethod(groups = {"persistence"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteAllMessagesForAccount(account);
    }

    @Test(groups = {"persistence"})
    public void testRingoStatistics() throws Exception {

        final RingoStatistics ringoStatistics = peppolMessageRepository.getAdminStatistics();
        assertNotNull(ringoStatistics);

        final List<RingoAccountStatistics> accountStatistics = ringoStatistics.getAccountStatistics();
        assertNotNull(accountStatistics);
        assertFalse(accountStatistics.isEmpty());

        RingoAccountStatistics testAccount = ringoStatistics.findByAccountId(account.getAccountId());
        assertNotNull(testAccount);

        validateMessages(testAccount);

    }

    private void validateMessages(RingoAccountStatistics testAccount) {
        verifyCounts(testAccount);
        veifyDates(testAccount);
    }

    private void veifyDates(RingoAccountStatistics testAccount) {
        assertEquals(testAccount.getLastDownloaded(), downloadedDate);
        assertEquals(testAccount.getLastReceived(), receivedDate);
        assertEquals(testAccount.getOldestUndelivered(), oldestUndeliveredDate);
        assertEquals(testAccount.getLastSent(), sentDate);
    }

    private void verifyCounts(RingoAccountStatistics testAccount) {
        assertEquals(testAccount.getIn(), 2);
        assertEquals(testAccount.getUndeliveredIn(), 1);
        assertEquals(testAccount.getOut(), 2);
        assertEquals(testAccount.getUndeliveredOut(), 1);
        assertEquals(testAccount.getTotal(), 4);
    }

    private void createTestMessages() {


        databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                "<test>\u00E5</test>",
                account.getAccountId().toInteger(),
                TransferDirection.OUT,
                participantId.stringValue(),
                participantId.stringValue(),
                new ReceptionId(), sentDate, receivedDate);
        databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                "<test>\u00E5</test>",
                account.getAccountId().toInteger(),
                TransferDirection.IN,
                participantId.stringValue(),
                participantId.stringValue(),
                new ReceptionId(), downloadedDate, receivedDate);

        databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                "<test>\u00E5</test>",
                account.getAccountId().toInteger(),
                TransferDirection.IN,
                participantId.stringValue(),
                participantId.stringValue(),
                new ReceptionId(), null, oldestUndeliveredDate);
        databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                "<test>\u00E5</test>",
                account.getAccountId().toInteger(),
                TransferDirection.OUT,
                participantId.stringValue(),
                participantId.stringValue(),
                new ReceptionId(), null, oldestUndeliveredDate);

    }

    /**
     * Parses date in yyyy-MM-dd format
     */
    protected Date parseDate(String dateString) throws ParseException {
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        return DATE_FORMAT.parse(dateString);
    }


}

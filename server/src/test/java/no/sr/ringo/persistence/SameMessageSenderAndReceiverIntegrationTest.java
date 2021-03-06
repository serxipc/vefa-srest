package no.sr.ringo.persistence;

import com.google.inject.Inject;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Date;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Guice(moduleFactory = ServerTestModuleFactory.class)
public class SameMessageSenderAndReceiverIntegrationTest {

    Logger logger = LoggerFactory.getLogger(SameMessageSenderAndReceiverIntegrationTest.class);

    private final PeppolMessageRepository peppolMessageRepository;
    private final DatabaseHelper databaseHelper;
    private final DbmsTestHelper dbmsTestHelper;

    private Account account = ObjectMother.getTestAccount();
    private ParticipantIdentifier participantId = ObjectMother.getTestParticipantId();

    private Long messageOut;
    private Integer accountReceiverId;


    @Inject
    public SameMessageSenderAndReceiverIntegrationTest(PeppolMessageRepository peppolMessageRepository, DatabaseHelper databaseHelper, DbmsTestHelper dbmsTestHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.databaseHelper = databaseHelper;
        this.dbmsTestHelper = dbmsTestHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void insertSample() throws SQLException {
        messageOut = dbmsTestHelper.createSampleMessage(1, TransferDirection.OUT, participantId.getIdentifier(), participantId.getIdentifier(), new ReceptionId(), (Date)null);
    }

    @AfterMethod(groups = {"persistence"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteMessage(messageOut);
        databaseHelper.deleteAccountReceiver(accountReceiverId);
    }

    @Test(groups = {"persistence"})
    public void testSenderAndReceiverAccountTheSame() {

        //sender and receiver belong to same account
        assertTrue(peppolMessageRepository.isSenderAndReceiverAccountTheSame(messageOut));

        // update the message's receiver to different participantId - there's no such participant in account_receiver table
        String receiver = "9908:123456789";
        databaseHelper.updateMessageReceiver(messageOut, receiver);
        assertFalse(peppolMessageRepository.isSenderAndReceiverAccountTheSame(messageOut));

        accountReceiverId =  databaseHelper.addAccountReceiver(account.getAccountId(), receiver);

        //add receiver to account_role table for the same account
        assertTrue(peppolMessageRepository.isSenderAndReceiverAccountTheSame(messageOut));

    }

}

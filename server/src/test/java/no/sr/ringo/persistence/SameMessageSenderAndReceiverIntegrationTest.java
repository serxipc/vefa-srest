package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.TransferDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.UUID;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Guice(moduleFactory = TestModuleFactory.class)
public class SameMessageSenderAndReceiverIntegrationTest {

    Logger logger = LoggerFactory.getLogger(SameMessageSenderAndReceiverIntegrationTest.class);

    private final PeppolMessageRepository peppolMessageRepository;
    private final DatabaseHelper databaseHelper;

    private RingoAccount account = ObjectMother.getTestAccount();
    private ParticipantId participantId = ObjectMother.getTestParticipantId();

    private Long messageOut;
    private Integer accountReceiverId;


    @Inject
    public SameMessageSenderAndReceiverIntegrationTest(PeppolMessageRepository peppolMessageRepository, DatabaseHelper databaseHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.databaseHelper = databaseHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void insertSample() throws SQLException {
        messageOut = databaseHelper.createMessage(1, TransferDirection.OUT, participantId.stringValue(), participantId.stringValue(), UUID.randomUUID().toString(), null);
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

        accountReceiverId =  databaseHelper.addAccountReceiver(account.getId(), receiver);

        //add receiver to account_role table for the same account
        assertTrue(peppolMessageRepository.isSenderAndReceiverAccountTheSame(messageOut));

    }

}

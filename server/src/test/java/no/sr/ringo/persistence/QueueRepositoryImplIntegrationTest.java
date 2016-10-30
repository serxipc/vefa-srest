/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.common.PeppolMessageTestdataGenerator;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.*;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.queue.OutboundMessageQueueId;
import no.sr.ringo.queue.QueueRepository;
import no.sr.ringo.queue.QueuedOutboundMessage;
import no.sr.ringo.queue.QueuedOutboundMessageError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

@Guice(moduleFactory = TestModuleFactory.class)
public class QueueRepositoryImplIntegrationTest {

    Logger logger = LoggerFactory.getLogger(QueueRepositoryImplIntegrationTest.class);

    private final PeppolMessageRepository peppolMessageRepository;
    private final QueueRepository queueRepository;
    private final DatabaseHelper databaseHelper;

    private Account account = ObjectMother.getTestAccount();
    private ParticipantId participantId = ObjectMother.getTestParticipantIdForSMPLookup();

    private Long messageId;
    private Long messageOut;

    private Long msgIdInvoice;


    @Inject
    public QueueRepositoryImplIntegrationTest(PeppolMessageRepository peppolMessageRepository, QueueRepository queueRepository, DatabaseHelper databaseHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.queueRepository = queueRepository;
        this.databaseHelper = databaseHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void insertSample() throws SQLException {
        databaseHelper.deleteAllMessagesForAccount(account);
        messageId = databaseHelper.createMessage(1, TransferDirection.IN, participantId.stringValue(), participantId.stringValue(), UUID.randomUUID().toString(), null);
        messageOut = databaseHelper.createMessage(1, TransferDirection.OUT, participantId.stringValue(), participantId.stringValue(), null, null);
    }

    @AfterMethod(groups = {"persistence"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteMessage(messageId);
        databaseHelper.deleteMessage(messageOut);
        // databaseHelper.deleteMessage((long) msgIdInvoice);
        databaseHelper.deleteAllMessagesForAccount(account);
    }

    @Test(groups = {"persistence"})
    public void testPutMessageOnQueue() {
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequest();
        peppolMessage.getPeppolHeader().setProfileId(ProfileId.valueOf("urn:www.cenbii.eu:profile:bii05:ver1.0"));

        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        OutboundMessageQueueId queueId = queueRepository.putMessageOnQueue(messageWithLocations.getMsgNo());
        assertNotNull(queueId);

        DatabaseHelper.QueuedMessage queuedMessage = databaseHelper.getQueuedMessageByQueueId(queueId);
        assertEquals(queueId, new OutboundMessageQueueId(queuedMessage.getQueueId()));
        assertEquals(messageWithLocations.getMsgNo(), queuedMessage.getMsgNo());
        assertEquals(OutboundMessageQueueState.QUEUED, queuedMessage.getState());

    }

    @Test(groups = {"persistence"})
    public void testGetQueuedMessageById(){
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequest();
        peppolMessage.getPeppolHeader().setProfileId(ProfileId.valueOf("urn:www.cenbii.eu:profile:bii05:ver1.0"));

        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        OutboundMessageQueueId queueId = queueRepository.putMessageOnQueue(messageWithLocations.getMsgNo());
        assertNotNull(queueId);

        QueuedOutboundMessage queuedMessage = queueRepository.getQueuedMessageById(queueId);
        assertNotNull(queuedMessage);
        assertEquals(MessageNumber.create(messageWithLocations.getMsgNo()), queuedMessage.getMessageNumber());
        assertEquals(queueId, queuedMessage.getOutboundQueueId());
        assertEquals(OutboundMessageQueueState.QUEUED, queuedMessage.getState());
    }

    @Test(groups = {"persistence"})
    public void getQueuedMessages() {

        // make sure we get one more
        int count = queueRepository.getQueuedMessages(0).size();
        createMessageAndPutOnQueue();
        int newCount = queueRepository.getQueuedMessages(0).size();
        assertEquals(newCount, count + 1);

        // make sure we have at least two, so that we can test the limiter
        createMessageAndPutOnQueue();
        assertEquals(queueRepository.getQueuedMessages(newCount + 9).size(), newCount + 1); // limit above
        assertEquals(queueRepository.getQueuedMessages(0).size(), newCount + 1);            // limit at
        assertEquals(queueRepository.getQueuedMessages(newCount).size(), newCount);         // limit below
        assertEquals(queueRepository.getQueuedMessages(1).size(), 1);                       // limit at min

    }

    @Test(groups = {"persistence"})
    public void testUpdateQueuedMessageState(){
        OutboundMessageQueueId id = createMessageAndPutOnQueue();
        queueRepository.changeQueuedMessageState(id, OutboundMessageQueueState.OK);
        DatabaseHelper.QueuedMessage fetched = databaseHelper.getQueuedMessageByQueueId(id);
        assertEquals(OutboundMessageQueueState.OK, fetched.getState());
    }

    @Test(groups = {"persistence"})
    public void testQueueError() {
        OutboundMessageQueueId queueId = createMessageAndPutOnQueue();
        QueuedOutboundMessageError error = new QueuedOutboundMessageError(queueId, "detail", "message", "stacktrace");
        queueRepository.logOutboundError(error);
        List<QueuedOutboundMessageError> errors = databaseHelper.getErrorMessages();

        QueuedOutboundMessageError fetched = errors.get(errors.size() - 1);
        assertNotNull(fetched.getErrorId());
        assertEquals(queueId, fetched.getOutboundQueueId());
        assertEquals("detail", fetched.getDetails());
        assertEquals("message", fetched.getMessage());
        assertEquals("stacktrace", fetched.getStacktrace());
        assertNotNull(fetched.getCreateDT());
    }

    private OutboundMessageQueueId createMessageAndPutOnQueue() {
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequest();
        peppolMessage.getPeppolHeader().setProfileId(ProfileId.valueOf("urn:www.cenbii.eu:profile:bii05:ver1.0"));

        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        return  queueRepository.putMessageOnQueue(messageWithLocations.getMsgNo());
    }

    @Test(groups = {"persistence"})
    public void testLockingMessage() throws Exception {

        String invoiceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"xxx.xslt\"?><Invoice>invoice</Invoice>";
        //creates an outbound message
        final int accountId = 1;

        msgIdInvoice = databaseHelper.createMessage(PeppolDocumentTypeId.EHF_INVOICE,
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                invoiceXml, accountId, TransferDirection.OUT, participantId.stringValue(), participantId.stringValue(), null, null, new Date());
        Integer queueId = databaseHelper.putMessageOnQueue(msgIdInvoice);
        final boolean lockOk = queueRepository.lockQueueItemForDelivery(new OutboundMessageQueueId(queueId));

        assertTrue(lockOk, "Should have obtained a lock");

    }

    @Test(groups = {"persistence"})
    public void testFailLockingMessage() throws Exception {

        String invoiceXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"xxx.xslt\"?><Invoice>invoice</Invoice>";
        //creates an outbound message
        final int accountId = 1;
        msgIdInvoice = databaseHelper.createMessage(PeppolDocumentTypeId.EHF_INVOICE,
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId(),
                invoiceXml, accountId, TransferDirection.OUT, participantId.stringValue(), participantId.stringValue(), null, new Date(), new Date());
        assertNotNull(msgIdInvoice);
        Integer queueId = databaseHelper.putMessageOnQueue(msgIdInvoice);

        // try locking twice
        boolean lockOk = queueRepository.lockQueueItemForDelivery(new OutboundMessageQueueId(queueId));
        assertTrue(lockOk, "Should have obtained a lock");

        lockOk = queueRepository.lockQueueItemForDelivery(new OutboundMessageQueueId(queueId));
        assertFalse(lockOk, "Should not have obtained a lock");

    }

}

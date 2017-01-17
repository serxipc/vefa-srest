package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.MessageId;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.MessageNumber;
import eu.peppol.persistence.MessageRepository;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.PeppolMessageTestdataGenerator;
import no.sr.ringo.document.DocumentRepository;
import no.sr.ringo.document.PeppolDocument;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.*;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.resource.InvalidUserInputWebException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.testng.Assert.*;

/**
 * @author steinar
 * @author thore
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class PeppolMessageRepositoryImplIntegrationTest {

    private final PeppolMessageRepository peppolMessageRepository;
    private final DocumentRepository documentRepository;
    private final DatabaseHelper databaseHelper;
    private final MessageRepository oxalisMessageRepository;
    private final DbmsTestHelper dbmsTestHelper;
    Logger logger = LoggerFactory.getLogger(PeppolMessageRepositoryImplIntegrationTest.class);
    private Account account = ObjectMother.getTestAccount();
    private ParticipantId participantId = ObjectMother.getTestParticipantIdForSMPLookup();

    private Long messageId;
    private Long messageOut;
    private Long messageIn;
    private String messageInUuid;
    private MessageId messageOutUuid;

    @Inject
    public PeppolMessageRepositoryImplIntegrationTest(PeppolMessageRepository peppolMessageRepository, DocumentRepository documentRepository, DatabaseHelper databaseHelper, MessageRepository oxalisMessageRepository, DbmsTestHelper dbmsTestHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.documentRepository = documentRepository;
        this.databaseHelper = databaseHelper;
        this.oxalisMessageRepository = oxalisMessageRepository;
        this.dbmsTestHelper = dbmsTestHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void insertSample() throws SQLException {
        databaseHelper.deleteAllMessagesForAccount(account);
        messageInUuid = UUID.randomUUID().toString();
        messageId = dbmsTestHelper.createMessage(1, TransferDirection.IN, participantId.stringValue(), participantId.stringValue(), messageInUuid, null);
        messageOutUuid = new MessageId();
        messageOut = dbmsTestHelper.createMessage(1, TransferDirection.OUT, participantId.stringValue(), participantId.stringValue(), messageOutUuid.stringValue(), null);
    }

    @AfterMethod(groups = {"persistence"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteMessage(messageId);
        databaseHelper.deleteMessage(messageIn);
        databaseHelper.deleteMessage(messageOut);
        databaseHelper.deleteAllMessagesForAccount(account);
    }

    @Test(groups = {"persistence"})
    public void checkPeppolHeaderForPersistedOutboundMessage() {
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequest();

        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        MessageMetaData messageMetaData = peppolMessageRepository.findMessageByMessageNo(account, messageWithLocations.getMsgNo().longValue());

        PeppolHeader peppolHeader = peppolMessage.getPeppolHeader();
        PeppolHeader persistedPeppolHeader = messageMetaData.getPeppolHeader();

        assertEquals(peppolHeader, persistedPeppolHeader);

    }

    /**
     * We had a bug where uploaded XML messages were persisted to the database with null-namespace defined (xmlns="") for every element.
     * Those who relied on using the default xmlns="...." as one of the PEPPOL/BIS/UBL namespaces would get their file corrupted.
     * Most EHF customers explicitly used named namespaces for PEPPOL/BIS/UBL and will not have their files corrupted (only slightly
     * changed as the xmlns="" will be added).
     * <p>
     * The customer that had their files corruped was Dakantus, and the issue was resolved in 2015-03-05.
     */
    @Test(groups = {"persistence"})
    public void checkXmlNamespaceForPersistedOutboundMessage() throws TransformerException {
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequestOfXmlUsingDefaultNamespaces();

        DOMSource domSource = new DOMSource(peppolMessage.getXmlMessage());
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        System.out.println(writer.toString());

        Document doc = peppolMessage.getXmlMessage();
        System.out.println("Getting children of : " + doc.getDocumentElement().getTagName());
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (Node.ELEMENT_NODE == n.getNodeType() && n.getChildNodes().getLength() == 1) {
                System.out.println("Element : " + n.getNodeName() + " = " + n.getTextContent());
            }
        }
        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, messageWithLocations.getMsgNo().longValue());
        PeppolDocument docFromDatabase = documentRepository.getPeppolDocument(account, MessageNumber.create(messageByMessageNo.getMsgNo()));
        assertTrue(!docFromDatabase.getXml().contains("xmlns=\"\""), "We should not have added xmlns=\"\" anywhere in the message");

        assertNotNull(messageWithLocations.getUuid(), "Missing UUID in MessageWithLocations");
    }

    @Test(groups = {"persistence"})
    public void testHandlingOfInvalidProcessId() {
        PeppolMessage peppolMessage = PeppolMessageTestdataGenerator.outboxPostRequest();
        peppolMessage.getPeppolHeader().setProfileId(ProfileId.valueOf("urn:www.cenbii.eu:profile:bii05:ver1.0"));

        MessageWithLocations messageWithLocations = peppolMessageRepository.persistOutboundMessage(account, peppolMessage);
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, Long.valueOf(messageWithLocations.getMsgNo()));

        PeppolHeader peppolHeader = peppolMessage.getPeppolHeader();
        PeppolHeader persistedPeppolHeader = messageByMessageNo.getPeppolHeader();

        assertEquals(peppolHeader, persistedPeppolHeader);

    }

    @Test(groups = {"persistence"})
    public void testFindUndeliveredOutboundMessagesByAccount() throws Exception {

        List<MessageMetaData> messages = peppolMessageRepository.findUndeliveredOutboundMessagesByAccount(account.getAccountId());
        assertNotNull(messages, "Null object returned when querying DBMS for undelivered outbound messages");

        logger.debug("Found " + messages.size() + " messages");
        assertTrue(messages.size() > 0, "Expected more than zero messages!");
        assertTrue(messages.size() <= 25, "Expected not more than 25 messages!");
    }

    @Test(groups = {"persistence"})
    public void testFindMessageByMessageNoAndAccountId() throws Exception {
        final MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, messageId);
        assertNotNull(messageByMessageNo);
    }

    @Test(groups = {"persistence"})
    public void testFindMessageByMessageNo() throws Exception {
        final MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(MessageNumber.create(messageId));
        assertNotNull(messageByMessageNo);
    }

    @Test(groups = {"persistence"}, expectedExceptions = PeppolMessageNotFoundException.class)
    public void testDoesNotFindMessageByMessageNo() {
        //This should throw an exception as adam is not owner of message 1
        final MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(ObjectMother.getAdamsAccount(), messageId);
        fail("This should never have happened, didn't expect to get here (messageByMessageNo=" + messageByMessageNo + ")");
    }

    @Test(groups = {"persistence"})
    public void testInboxCount() throws Exception {
        final Integer inboxCount = peppolMessageRepository.getInboxCount(account.getAccountId());
        assertTrue(inboxCount > 0);
    }

    @Test(groups = {"persistence"})
    public void testUndeliveredIn() throws Exception {
        List<MessageMetaData> messages = peppolMessageRepository.findUndeliveredInboundMessagesByAccount(account.getAccountId());
        assertNotNull(messages, "Null object returned when querying DBMS for undelivered inbound messages");

        logger.debug("Found " + messages.size() + " messages");

        assertTrue(messages.size() > 0, "Expected more than zero messages!");
        assertTrue(messages.size() <= 25, "Expected not more than 25 messages!");
    }

    @Test(groups = {"persistence"})
    public void testUndeliveredInWithPageIndex() {

        //no page index, expect not more than 25 messages
        SearchParams params = new SearchParams(null, null, null, null, null);
        List<MessageMetaData> messages = peppolMessageRepository.findMessages(account.getAccountId(), params);
        assertTrue(messages.size() <= 25, "Expected not more than 25 messages!");

        //test assumes we don't have 100 000 pages of messages (25 each)
        params = new SearchParams(null, null, null, null, "100000");
        messages = peppolMessageRepository.findMessages(account.getAccountId(), params);
        assertEquals(0, messages.size());

    }

    @Test(groups = {"persistence"}, expectedExceptions = InvalidUserInputWebException.class)
    public void testUndeliveredInWithNotParsablePageIndex() {
        // test assumes we don't have 100 000 pages of messages (25 each)
        SearchParams params = new SearchParams(null, null, null, null, "xxx");
        List<MessageMetaData> messages = peppolMessageRepository.findMessages(account.getAccountId(), params);
        assertEquals(0, messages.size());
    }

    @Test(groups = {"persistence"})
    public void testUpdateAndCopyMessage() throws PeppolMessageNotFoundException, IOException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2020);
        cal.set(Calendar.MONTH, 5);
        cal.set(Calendar.DAY_OF_MONTH, 10);

        //update the message
        Date date = cal.getTime();
        byte[] nativeEvidenceBytes = "Native evidence bytes".getBytes();
        byte[] remEvidenceBytes = "REM evidence bytes".getBytes();
        peppolMessageRepository.updateOutBoundMessageDeliveryDateAndUuid(MessageNumber.create(messageOut), null, messageOutUuid, date, nativeEvidenceBytes);

        MessageMetaData messageOutbound = peppolMessageRepository.findMessageByMessageNo(account, messageOut);

        assertEquals(messageOutUuid.stringValue(), messageOutbound.getUuid());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(messageOutbound.getDelivered());

        assertEquals(cal.get(Calendar.YEAR), c2.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.MONTH), c2.get(Calendar.MONTH));
        assertEquals(cal.get(Calendar.DAY_OF_MONTH), c2.get(Calendar.DAY_OF_MONTH));

        String newUuid = UUID.randomUUID().toString();

        //copy from out to in while assigning a new UUID
        messageIn = peppolMessageRepository.copyOutboundMessageToInbound(this.messageOut, newUuid);

        MessageMetaData messageInbound = peppolMessageRepository.findMessageByMessageNo(account, messageIn);
        assertNull(messageInbound.getDelivered());
        assertEquals(TransferDirection.IN, messageInbound.getTransferDirection());
        assertEquals(messageOutbound.getPeppolHeader().getSender(), messageInbound.getPeppolHeader().getSender());
        assertEquals(messageOutbound.getPeppolHeader().getReceiver(), messageInbound.getPeppolHeader().getReceiver());
        assertEquals(messageOutbound.getPeppolHeader().getPeppolChannelId(), messageInbound.getPeppolHeader().getPeppolChannelId());
        assertEquals(messageOutbound.getPeppolHeader().getPeppolDocumentTypeId(), messageInbound.getPeppolHeader().getPeppolDocumentTypeId());
        assertEquals(messageOutbound.getPeppolHeader().getProfileId(), messageInbound.getPeppolHeader().getProfileId());
        assertEquals(newUuid, messageInbound.getUuid());
        assertEquals(messageOutbound.getReceived(), messageInbound.getReceived());

        // Verifies the contents of the evidence
        {
            List<eu.peppol.persistence.MessageMetaData> messages = oxalisMessageRepository.findByMessageId(messageOutUuid);
            assertEquals(messages.size(), 1);
            eu.peppol.persistence.MessageMetaData m = messages.get(0);

            verifyEvidence(m::getNativeEvidenceUri, nativeEvidenceBytes);
        }

        String xmlOut = peppolMessageRepository.findDocumentByMessageNoWithoutAccountCheck(messageOut);
        String xmlIn = peppolMessageRepository.findDocumentByMessageNoWithoutAccountCheck(messageIn);

        assertEquals(xmlIn, xmlOut);
    }

    void verifyEvidence(Supplier<URI> pathFunction, byte[] bytes) throws IOException {

        URI nativeEvidenceUri = pathFunction.get();

        byte[] evidenceBytes = Files.readAllBytes(Paths.get(nativeEvidenceUri));
        assertEquals(bytes, evidenceBytes);
    }

    @Test(groups = {"persistence"})
    public void testMessagesCount() throws Exception {
        final Integer inboxCount = peppolMessageRepository.getMessagesCount(account.getAccountId());
        assertTrue(inboxCount > 0);
    }

    @Test(groups = {"persistence"})
    public void testMessagesCountWithEmptySearchParams() throws Exception {
        SearchParams searchParams = new SearchParams(null, null, null, null, null);
        final Integer messagesCount = peppolMessageRepository.getMessagesCount(account.getAccountId(), searchParams);
        assertTrue(messagesCount > 0);
    }

    @Test(groups = {"persistence"})
    public void testMessagesCountWithDirection() throws Exception {
        SearchParams searchParams = new SearchParams("IN", null, null, null, null);
        Integer messagesCount = peppolMessageRepository.getMessagesCount(account.getAccountId(), searchParams);
        assertTrue(messagesCount > 0);

        searchParams = new SearchParams("OUT", null, null, null, null);
        messagesCount = peppolMessageRepository.getMessagesCount(account.getAccountId(), searchParams);
        assertTrue(messagesCount > 0);
    }

}

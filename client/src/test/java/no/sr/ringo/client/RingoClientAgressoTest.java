package no.sr.ringo.client;

import no.sr.ringo.common.UploadMode;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import no.sr.ringo.standalone.DefaultRingoConfig;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Tests that the AP is working against the local servers in Unit4 / SR.
 * These tests are diabled and should be run manually from IDEA for debugging and smoketesting.
 */
public class RingoClientAgressoTest {

    private static final PeppolParticipantId UNIT4_PARTICIPANT_ID = PeppolParticipantId.valueFor("9908:961329310");
    private static final PeppolParticipantId SEND_REGNING_TEST_ID = PeppolParticipantId.valueFor("9908:810017902");

    private RingoClientImpl agressoRingoClient;

    @BeforeMethod
    public void setUp() throws Exception {
        agressoRingoClient = new RingoClientImpl(new RingoServiceRestImpl(new DefaultRingoConfig("http://127.0.0.1", null), "username", "password"));
    }

    @Test
    public void testNothing() throws Exception {
        // just what is says
    }


    @Test(enabled = false)
    public void inboxExists() throws Exception {
        Inbox inbox = agressoRingoClient.getInbox();
        assertNotNull(inbox);
    }

    @Test(enabled = false)
    public void readMessages() throws Exception {
        Inbox inbox = agressoRingoClient.getInbox();
        Messages messages = inbox.getMessages();
        for (Message message : messages) {
            boolean read = message.markAsRead();
            assertEquals(true,read);
        }
    }

    @Test(enabled = false)
    public void thereAreAvailableDocumentTransfersForInvoice() throws Exception {
        List<AcceptedDocumentTransfer> acceptedDocumentTransfers = agressoRingoClient.fetchAcceptedDocumentTransfers(UNIT4_PARTICIPANT_ID, LocalName.Invoice);
        assertNotNull(acceptedDocumentTransfers);
        assertTrue(acceptedDocumentTransfers.size() > 0);
    }

    @Test(enabled = false)
    public void thereAreAvailableDocumentTransfersForCreditNote() throws Exception {
        List<AcceptedDocumentTransfer> acceptedDocumentTransfers = agressoRingoClient.fetchAcceptedDocumentTransfers(PeppolParticipantId.valueFor("9908:961329310"), LocalName.Invoice);
        assertNotNull(acceptedDocumentTransfers);
        assertTrue(acceptedDocumentTransfers.size() > 0);
    }

    @Test(enabled = false)
    public void itIsPossibleToSendAEHFDocument() throws Exception {
        URL ehfDocument = RingoClientAgressoTest.class.getResource("/documents/invoice/somedocument.xml");
        Message message = agressoRingoClient.send(new File(ehfDocument.toURI()), new PeppolChannelId("test"), null, SEND_REGNING_TEST_ID, UploadMode.SINGLE);
        assertNotNull(message);
        assertNotNull(message.getMessageSelfUri());
    }

}

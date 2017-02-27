/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import no.difi.oxalis.test.identifier.PeppolDocumentTypeIdAcronym;
import no.difi.oxalis.test.identifier.PeppolProcessTypeIdAcronym;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.document.DocumentRepository;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.document.PeppolDocument;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertNotNull;

@Guice(moduleFactory = ServerTestModuleFactory.class)
public class PeppolDocumentIntegrationTest {

    private final DatabaseHelper databaseHelper;
    private FetchDocumentUseCase fetchDocumentUseCase;
    private final DocumentRepository documentRepository;

    private Account account;
    private ParticipantIdentifier participantId;
    private List<MessageNumber> messagesToDelete;

    @Inject
    public PeppolDocumentIntegrationTest(FetchDocumentUseCase fetchDocumentUseCase, DocumentRepository documentRepository, DatabaseHelper databaseHelper) {
        this.fetchDocumentUseCase = fetchDocumentUseCase;
        this.documentRepository = documentRepository;
        this.databaseHelper = databaseHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void setUp() throws SQLException {
        account = ObjectMother.getTestAccount();
        participantId = ObjectMother.getTestParticipantIdForSMPLookup();
        messagesToDelete = new ArrayList<MessageNumber>();
    }

    @Test(groups = {"persistence"})
    public void testfindDocumentByMessageNumber() throws PeppolMessageNotFoundException {
        MessageNumber messageNumber = createMessageWithInvoiceDocument();
        final PeppolDocument xmlDoc = documentRepository.getPeppolDocument(account, messageNumber);
        assertNotNull(xmlDoc);
    }

    @Test(groups = {"persistence"}, expectedExceptions = PeppolMessageNotFoundException.class)
    public void testDoesNotFindDocumentByMessageNumber() throws Exception {
        //This should throw an exception as adam is not owner of message 1
        MessageNumber messageNumber = createMessageWithInvoiceDocument();
        documentRepository.getPeppolDocument(ObjectMother.getAdamsAccount(), messageNumber);
    }

    private MessageNumber createMessageWithInvoiceDocument() {
        String invoiceXml = invoiceAsXml();

        Long message = databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.EHF_INVOICE.toVefa(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.toVefa(),
                invoiceXml, 1, TransferDirection.IN, participantId.getIdentifier(), participantId.getIdentifier(), new ReceptionId(), new Date(), new Date(),new PeppolChannelId("test"));
        return MessageNumber.create(message);
    }

    private MessageNumber createMessageWithCreditNoteDocument() {
        String creditXml = creditNoteAsXml();

        Long messageNumber = databaseHelper.createSampleMessage(PeppolDocumentTypeIdAcronym.CREDIT_NOTE.toVefa(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.toVefa(),
                creditXml, 1, TransferDirection.IN, participantId.getIdentifier(), participantId.getIdentifier(), new ReceptionId(), new Date(), new Date(), new PeppolChannelId("test"));
        return MessageNumber.create(messageNumber);
    }

    private String invoiceAsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"xxx.xslt\"?><Invoice>invoice</Invoice>";
    }

    private String creditNoteAsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><?xml-stylesheet type=\"text/xsl\" href=\"xxx.xslt\"?><CreditInvoice>invoice</CreditInvoice>";
    }

}

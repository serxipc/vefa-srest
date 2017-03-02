package no.sr.ringo.document;

import no.difi.ringo.tools.PersistenceObjectMother;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.message.MessageMetaDataImpl;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.peppol.PeppolDocumentTest;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 3:26 PM
 */
public class FetchDocumentUseCaseTest extends PeppolDocumentTest {

    private DocumentRepository mockDocumentRepository;
    private FetchDocumentUseCase fetchDocumentUseCase;

    private Account account;
    private PeppolDocument ehfInvoice;
    private PeppolMessageRepository mockPeppolMessageRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        mockDocumentRepository = EasyMock.createStrictMock(DocumentRepository.class);
        mockPeppolMessageRepository = EasyMock.createMock(PeppolMessageRepository.class);

        fetchDocumentUseCase = new FetchDocumentUseCase(mockDocumentRepository, mockPeppolMessageRepository);

        account = ObjectMother.getTestAccount();
        ehfInvoice = createEhfInvoice();
    }


    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testIfAMessageIsNotFoundTheExceptionIsLeftToPropagate() throws Exception {
        MessageNumber msgNo = MessageNumber.of("10");

        expect(mockPeppolMessageRepository.findMessageByMessageNo(account, msgNo)).andThrow(new PeppolMessageNotFoundException(msgNo));
        replayAllMocks();

        fetchDocumentUseCase.findDocument(account, msgNo);

        verifyAllMocks();
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testIfAMessageIsNotFoundTheExceptionIsLeftToPropagate2() throws Exception {
        MessageNumber msgNo = MessageNumber.of("10");

        expect(mockPeppolMessageRepository.findMessageByMessageNo(account, msgNo)).andThrow(new PeppolMessageNotFoundException(msgNo));
        replayAllMocks();

        fetchDocumentUseCase.findDocument(account, msgNo);

        verifyAllMocks();
    }


    @Test
    public void returnPeppolDocumentIfFileUri() throws Exception {

        // Creates the sample data to be returned etc.
        MessageNumber msgNo = MessageNumber.of("10");
        final MessageMetaDataImpl messageMetaData = PersistenceObjectMother.sampleInboundTransmissionMetaData(msgNo, account.getAccountId());

        // First we expect an attempt to findDocument the meta data
        final PeppolDocument peppolDocument = EasyMock.createMock(PeppolDocument.class);
        expect(mockPeppolMessageRepository.findMessageByMessageNo(account, msgNo)).andReturn(messageMetaData);
        expect(mockDocumentRepository.getPeppolDocument(account, msgNo)).andReturn(peppolDocument);
        expectLastCall();
        
        replayAllMocks();

        final FetchDocumentResult fetchDocumentResult = fetchDocumentUseCase.findDocument(account, msgNo);

        assertTrue(fetchDocumentResult instanceof PeppolDocument);
    }

    @Test
    public void returnDocumentReferenceIfNotFileScheme() throws Exception {

        // Creates the sample data to be returned etc.
        MessageNumber msgNo = MessageNumber.of("10");
        final MessageMetaDataImpl messageMetaData = PersistenceObjectMother.sampleInboundTransmissionMetaData(msgNo, account.getAccountId());
        // Ensures that we should expect a PeppolDocumentReference when invoking the use case
        messageMetaData.setPayloadUri(URI.create("https://cloudservice/container/blob/document.xml"));

        // First we expect an attempt to findDocument the meta data
        expect(mockPeppolMessageRepository.findMessageByMessageNo(account, msgNo)).andReturn(messageMetaData);

        replayAllMocks();

        final FetchDocumentResult fetchDocumentResult = fetchDocumentUseCase.findDocument(account, msgNo);

        assertTrue(fetchDocumentResult instanceof PeppolDocumentReference);
    }


    private PeppolDocument createEhfInvoice() {
        String invoice = invoice();
        return new EhfInvoice(invoice);
    }

    private void verifyAllMocks() {
        verify(mockDocumentRepository, mockPeppolMessageRepository);
    }

    private void replayAllMocks() {
        replay(mockDocumentRepository, mockPeppolMessageRepository);
    }

}

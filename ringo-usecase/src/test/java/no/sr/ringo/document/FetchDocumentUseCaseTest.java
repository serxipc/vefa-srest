package no.sr.ringo.document;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.peppol.PeppolDocumentTest;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

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
    private MyPeppolDocumentDecorator peppolDocumentDecorator;

    @BeforeMethod
    public void setUp() throws Exception {
        mockDocumentRepository = EasyMock.createStrictMock(DocumentRepository.class);

        fetchDocumentUseCase = new FetchDocumentUseCase(mockDocumentRepository);

        account = ObjectMother.getTestAccount();
        ehfInvoice = createEhfInvoice();
        peppolDocumentDecorator = new MyPeppolDocumentDecorator(ehfInvoice);
    }


    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testIfAMessageIsNotFoundTheExceptionIsLeftToPropagate() throws Exception {
        MessageNumber msgNo = MessageNumber.valueOf("10");

        expect(mockDocumentRepository.getPeppolDocument(account, msgNo)).andThrow(new PeppolMessageNotFoundException(msgNo.toLong()));
        replayAllMocks();

        fetchDocumentUseCase.execute(account, msgNo);
    }

    private PeppolDocument createEhfInvoice() {
        String invoice = invoice();
        return new EhfInvoice(invoice);
    }

    private void verifyAllMocks() {
        verify(mockDocumentRepository);
    }

    private void replayAllMocks() {
        replay(mockDocumentRepository);
    }


    /**
     * PeppolDocumentDecorator stub useful for running tests.
     */
    private static class MyPeppolDocumentDecorator extends PeppolDocumentDecorator {

        private MyPeppolDocumentDecorator(PeppolDocument peppolDocument) {
            super(peppolDocument);
        }

    }
}

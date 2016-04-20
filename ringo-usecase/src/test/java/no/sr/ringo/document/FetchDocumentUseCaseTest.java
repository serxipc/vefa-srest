package no.sr.ringo.document;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.peppol.PeppolDocumentTest;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 3:26 PM
 */
public class FetchDocumentUseCaseTest extends PeppolDocumentTest {

    private PeppolDocumentDecoratorFactory mockPeppolDocumentDecoratorFactory;
    private DocumentRepository mockDocumentRepository;
    private FetchDocumentUseCase fetchDocumentUseCase;

    private RingoAccount ringoAccount;
    private PeppolDocument ehfInvoice;
    private MyPeppolDocumentDecorator peppolDocumentDecorator;

    @BeforeMethod
    public void setUp() throws Exception {
        mockDocumentRepository = EasyMock.createStrictMock(DocumentRepository.class);
        mockPeppolDocumentDecoratorFactory = EasyMock.createStrictMock(PeppolDocumentDecoratorFactory.class);

        fetchDocumentUseCase = new FetchDocumentUseCase(mockDocumentRepository, mockPeppolDocumentDecoratorFactory);

        ringoAccount = ObjectMother.getTestAccount();
        ehfInvoice = createEhfInvoice();
        peppolDocumentDecorator = new MyPeppolDocumentDecorator(ehfInvoice);
    }

    @Test
    public void testsDocumentIsFetchedAndDecoratedByTheUseCase() throws Exception {
        MessageNumber msgNo = MessageNumber.valueOf("10");

        expect(mockDocumentRepository.getPeppolDocument(ringoAccount, msgNo)).andStubReturn(ehfInvoice);

        expect(mockPeppolDocumentDecoratorFactory.decorateWithStyleSheet(ehfInvoice)).andStubReturn(peppolDocumentDecorator);

        replayAllMocks();

        PeppolDocument result = fetchDocumentUseCase.executeWithDecoration(ringoAccount, msgNo);

        assertEquals(result, peppolDocumentDecorator);
        verifyAllMocks();
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testIfAMessageIsNotFoundTheExceptionIsLeftToPropagate() throws Exception {
        MessageNumber msgNo = MessageNumber.valueOf("10");

        expect(mockDocumentRepository.getPeppolDocument(ringoAccount, msgNo)).andThrow(new PeppolMessageNotFoundException(msgNo.toInt()));
        replayAllMocks();

        fetchDocumentUseCase.execute(ringoAccount, msgNo);
    }

    private PeppolDocument createEhfInvoice() {
        String invoice = invoice();
        return new EhfInvoice(invoice);
    }

    private void verifyAllMocks() {
        verify(mockPeppolDocumentDecoratorFactory,mockDocumentRepository);
    }

    private void replayAllMocks() {
        replay(mockPeppolDocumentDecoratorFactory,mockDocumentRepository);
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

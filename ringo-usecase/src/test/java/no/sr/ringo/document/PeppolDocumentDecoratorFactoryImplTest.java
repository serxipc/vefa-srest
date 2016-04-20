package no.sr.ringo.document;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 10/29/12
 * Time: 10:50 AM
 */
public class PeppolDocumentDecoratorFactoryImplTest {

    private PeppolDocumentDecoratorFactory peppolDocumentDecoratorFactory;

    @BeforeMethod
    public void setUp() throws Exception {
        peppolDocumentDecoratorFactory = new PeppolDocumentDecoratorFactoryImpl();
    }

    @Test
    public void testXslStylesheetForInvoice() throws Exception {
        EhfInvoice invoice = new EhfInvoice("rubbish");
        PeppolDocument xmlStylesheetDecorator = peppolDocumentDecoratorFactory.decorateWithStyleSheet(invoice);
        assertNotNull(xmlStylesheetDecorator);
    }

    @Test
    public void testXslStylesheetForCreditInvoice() throws Exception {
        EhfCreditInvoice ehfCreditInvoice = new EhfCreditInvoice("rubbish");
        PeppolDocument xmlStylesheetDecorator = peppolDocumentDecoratorFactory.decorateWithStyleSheet(ehfCreditInvoice);
        assertNotNull(xmlStylesheetDecorator);
    }

    @Test
    public void testXslStylesheetForUnknownDocument() throws Exception {
        PeppolDocument defaultPeppolDocument = new DefaultPeppolDocument("stuff");
        PeppolDocument xmlStylesheetDecorator = peppolDocumentDecoratorFactory.decorateWithStyleSheet(defaultPeppolDocument);
        assertNotNull(xmlStylesheetDecorator);
    }


}

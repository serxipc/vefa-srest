package no.sr.ringo.document;

import no.sr.ringo.peppol.PeppolDocumentTest;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import org.testng.annotations.Test;

import static no.sr.ringo.document.PeppolDocumentXmlStyleSheetDecorator.StyleSheetFileNameVisitor.EHF_CREDIT_INVOICE_STYLESHEET;
import static no.sr.ringo.document.PeppolDocumentXmlStyleSheetDecorator.StyleSheetFileNameVisitor.EHF_INVOICE_STYLESHEET;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 3:03 PM
 */
public class PeppolDocumentXmlStyleSheetDecoratorTest extends PeppolDocumentTest {


    @Test
    public void testStyleSheetAddedToInvoice() throws Exception {

        String xml = validXmlDocumentFor(PeppolDocumentTypeId.EHF_INVOICE);
        EhfInvoice invoice = new EhfInvoice(xml);
        PeppolDocument peppolDocumentXmlStyleSheetDecorator = new PeppolDocumentXmlStyleSheetDecorator(invoice);

        String result = peppolDocumentXmlStyleSheetDecorator.getXml();

        assertTrue(result.contains(EHF_INVOICE_STYLESHEET));
    }

    @Test
    public void testStyleSheetAddedToCreditInvoice() throws Exception {

        String xml = validXmlDocumentFor(PeppolDocumentTypeId.EHF_CREDIT_NOTE);
        EhfCreditInvoice invoice = new EhfCreditInvoice(xml);
        PeppolDocument peppolDocumentXmlStyleSheetDecorator = new PeppolDocumentXmlStyleSheetDecorator(invoice);

        String result = peppolDocumentXmlStyleSheetDecorator.getXml();

        assertTrue(result.contains(EHF_CREDIT_INVOICE_STYLESHEET));
    }


    @Test
    public void testDefaultDocumentNoStyleSheetApplied() throws Exception {

        String xml = validXmlDocumentFor(new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId("urn:unknown"));
        DefaultPeppolDocument document = new DefaultPeppolDocument(xml);
        PeppolDocument peppolDocumentXmlStyleSheetDecorator = new PeppolDocumentXmlStyleSheetDecorator(document);

        String result = peppolDocumentXmlStyleSheetDecorator.getXml();

        //xml not changed.
        assertEquals(result, xml);
    }
}

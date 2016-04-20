package no.sr.ringo.document;

import no.sr.ringo.peppol.PeppolDocumentTest;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import static no.sr.ringo.document.PeppolDocumentXmlStyleSheetDecorator.DecorateDocumentVisitor;

public class DecorateDocumentVisitorTest extends PeppolDocumentTest {

    @Test
    public void testDecorate() throws Exception {
        String xslt = "myStyleSheet.xslt";
        DecorateDocumentVisitor decorator = createDecoratorUsingStyleSheet(xslt);
        final String result = decorator.decorate(xmlHeader());
        assertTrue(containsStyleSheet(result,xslt));
    }

    @Test
    public void testDecoratorInvalidXml() throws Exception {
        DecorateDocumentVisitor decorator = createDecoratorUsingStyleSheet("test.xslt");
        String result = decorator.decorate("not decorated as not valid xml");
        assertEquals(result,"not decorated as not valid xml");
    }

    @Test
    public void testDecoratorValidXmlWithExistingStyleSheet() throws Exception {
        DecorateDocumentVisitor decorator = createDecoratorUsingStyleSheet("test.xslt");
        String xml = xmlHeader() + "<?xml-stylesheet href=\"single-col.css\" media=\"all and (max-width: 30em)\"?>";
        String result = decorator.decorate(xml);
        assertEquals(result, xmlHeader() + decorator.appendStyleSheet("test.xslt"));
    }

    @Test
    public void testDecoratorInvalidXmlWithStyleSheet() throws Exception {
        DecorateDocumentVisitor decorator = createDecoratorUsingStyleSheet("test.xslt");
        String xml = "<?xml-stylesheet href=\"single-col.css\" media=\"all and (max-width: 30em)\"?>";
        String result = decorator.decorate(xml);
        assertEquals(result, xml);
    }

    @Test
    public void testStyleSheetAddedForInvoice() throws Exception {
        final String xmlDocument = validXmlDocumentFor(PeppolDocumentTypeId.EHF_INVOICE);
        EhfInvoice invoice = new EhfInvoice(xmlDocument);
        String xslt = "EHF-faktura_smaa.xslt";
        final DecorateDocumentVisitor peppolDocumentDecorator = createDecoratorUsingStyleSheet(xslt);
        String xml = invoice.acceptVisitor(peppolDocumentDecorator);
        assertDocumentModified(xmlDocument, xml);
        assertTrue(containsStyleSheet(xml, xslt));
    }

    @Test
    public void testStyleSheetAddedForCreditNote() throws Exception {
        final String xmlDocument = validXmlDocumentFor(PeppolDocumentTypeId.EHF_CREDIT_NOTE);
        EhfCreditInvoice creditInvoice = new EhfCreditInvoice(xmlDocument);
        String xslt = "EHF-kreditnota_smaa.xslt";
        final DecorateDocumentVisitor peppolDocumentDecorator = createDecoratorUsingStyleSheet(xslt);
        String xml = creditInvoice.acceptVisitor(peppolDocumentDecorator);
        assertDocumentModified(xmlDocument, xml);
        assertTrue(containsStyleSheet(xml, xslt));
    }

    private PeppolDocumentXmlStyleSheetDecorator.DecorateDocumentVisitor createDecoratorUsingStyleSheet(String xslt) {
        return new PeppolDocumentXmlStyleSheetDecorator.DecorateDocumentVisitor(xslt);
    }

    private void assertDocumentModified(String xmlDocument, String xml) {
        assertFalse(xmlDocument.equals(xml), "They were the same!");
    }

    private boolean containsStyleSheet(String result, String styleSheet) {
        return result.contains(styleSheet);
    }

}

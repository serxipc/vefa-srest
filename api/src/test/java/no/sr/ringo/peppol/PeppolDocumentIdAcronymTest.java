package no.sr.ringo.peppol;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 7/12/12
 * Time: 10:36 AM
 */
public class PeppolDocumentIdAcronymTest {


    @Test
    public void testSafeValueOf() throws Exception {
        assertEquals(PeppolDocumentIdAcronym.UNKNOWN, PeppolDocumentIdAcronym.fromAcronym("rubbish"));
        assertEquals(PeppolDocumentIdAcronym.INVOICE, PeppolDocumentIdAcronym.fromAcronym("INVOICE"));
        assertEquals(PeppolDocumentIdAcronym.CREDIT_NOTE, PeppolDocumentIdAcronym.fromAcronym("CREDIT_NOTE"));
        assertEquals(PeppolDocumentIdAcronym.ORDER, PeppolDocumentIdAcronym.fromAcronym("ORDER"));
    }

    @Test
    public void testInvoiceToLocalName() throws Exception {
        LocalName invoiceLocalName = PeppolDocumentIdAcronym.INVOICE.toLocalName();

        assertEquals(invoiceLocalName,LocalName.Invoice);

    }

    @Test
    public void testCreditNoteToLocalName() throws Exception {
        LocalName creditNoteLocalName = PeppolDocumentIdAcronym.CREDIT_NOTE.toLocalName();

        assertEquals(creditNoteLocalName,LocalName.CreditNote);
    }

    @Test
    public void testInvoiceFromLocalName() throws Exception {
        LocalName invoiceLocalName = LocalName.valueOf("Invoice");
        PeppolDocumentIdAcronym invoice = PeppolDocumentIdAcronym.fromLocalName(invoiceLocalName);

        assertEquals(invoice, PeppolDocumentIdAcronym.INVOICE);
    }

    @Test
    public void testCreditInvoiceFromLocalName() throws Exception {
        LocalName creditNoteLocalName = LocalName.valueOf("CreditNote");
        PeppolDocumentIdAcronym creditNote = PeppolDocumentIdAcronym.fromLocalName(creditNoteLocalName);
        assertEquals(creditNote, PeppolDocumentIdAcronym.CREDIT_NOTE);
    }

}

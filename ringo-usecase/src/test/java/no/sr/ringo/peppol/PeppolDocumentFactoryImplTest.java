package no.sr.ringo.peppol;

import no.sr.ringo.document.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 10/3/12
 * Time: 10:32 AM
 */
public class PeppolDocumentFactoryImplTest extends PeppolDocumentTest {

    private PeppolDocumentFactoryImpl documentFactory;

    @BeforeMethod
    protected void setUp() throws Exception {
        documentFactory = new PeppolDocumentFactoryImpl();
    }

    @Test
    public void testEhfInvoiceDocumentShouldCreateEhfInvoiceObject() throws Exception {
        PeppolDocumentFactoryImpl documentFactory = new PeppolDocumentFactoryImpl();
        final PeppolDocument document = documentFactory.makePeppolDocument(PeppolDocumentTypeId.EHF_INVOICE, "");
        assertEquals(document.getClass(), EhfInvoice.class);
    }

    @Test
    public void testEhfCreditNoteDocumentShouldCreateEhfCreditInvoiceObject() throws Exception {
        PeppolDocumentFactoryImpl documentFactory = new PeppolDocumentFactoryImpl();
        final PeppolDocument document = documentFactory.makePeppolDocument(PeppolDocumentTypeId.EHF_CREDIT_NOTE, "");
        assertEquals(document.getClass(), EhfCreditInvoice.class);
    }

    @Test
    public void testUnknownPeppolDocumentTypeId() throws Exception {
        final PeppolDocument document = documentFactory.makePeppolDocument(PeppolDocumentTypeId.valueOf("rubbish"), "");
        assertEquals(document.getClass(), DefaultPeppolDocument.class);
    }

}

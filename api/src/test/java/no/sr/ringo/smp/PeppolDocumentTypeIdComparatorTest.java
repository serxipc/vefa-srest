package no.sr.ringo.smp;

import no.sr.ringo.peppol.CustomizationIdentifier;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Makes sure well known documenttypeid / customizationid variants will sort as expected.
 * Below are a simple chart for some UBL 2.0 and UBL 2.1 based document types and their profile usage
 *
 * PROFILES
 * ========
 * EHF_INVOICE                     urn:www.cenbii.eu:profile:bii04:ver1.0
 * EHF_INVOICE 2.0                 urn:www.cenbii.eu:profile:bii04:ver2.0
 * EHF_CREDITNOTE                  urn:www.cenbii.eu:profile:biixx:ver1.0
 * EHF_INVOICE_CREDITNOTE          urn:www.cenbii.eu:profile:bii05:ver1.0
 * EHF_INVOICE_CREDITNOTE 2.0      urn:www.cenbii.eu:profile:bii05:ver2.0
 * EHF_INVOICE_CREDITNOTE_REMINDER urn:www.cenbii.eu:profile:biixy:ver1.0
 *
 * DOCUMENTID USED IN PROFILES
 * ===========================
 * EHF_INVOICE_CREDITNOTE          urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0
 * EHF_CREDITNOTE                  urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0
 * EHF_INVOICE_CREDITNOTE_REMINDER urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0
 * EHF_INVOICE_CREDITNOTE 2.0      urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1
 *
 * DOCUMENTID USED IN PROFILES
 * ===========================
 * EHF_INVOICE_CREDITNOTE          urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
 * EHF_INVOICE                     urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
 * EHF_INVOICE_CREDITNOTE_REMINDER urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
 * EHF_INVOICE 2.0                 urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1
 *
 * @author steinar
 * @author thore
 */
public class PeppolDocumentTypeIdComparatorTest {

    private PeppolDocumentTypeIdComparator comparator;

    @BeforeTest
    public void setUp() {
        comparator = new PeppolDocumentTypeIdComparator();
    }

    //
    // Below are UBL 2.1 / EHF 2.0 based tests
    //

    @Test
    void testUbl21Faktura() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 1);
    }

    @Test
    void testUbl21Kreditnota() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 2);
    }

    @Test
    void testUbl21Ordre() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biitrns001:ver2.0:extended:urn:www.peppol.eu:bis:peppol28a:ver2.0:extended:urn:www.difi.no:ehf:ordre:ver1.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 3);
    }

    @Test
    void testUbl21Ordrebekreftelse() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:OrderResponse-2::OrderResponse##urn:www.cenbii.eu:transaction:biitrns076:ver2.0:extended:urn:www.peppol.eu::bis:peppol28a:ver1.0:extended:urn:www.difi.no:ehf:ordrebekreftelse:ver1.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 4);
    }

    @Test
    void testUbl21Katalog() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:www.cenbii.eu:transaction:biitrns019:ver2.0:extended:urn:www.peppol.eu:bis:peppol1a:ver2.0:extended:urn:www.difi.no:ehf:katalog:ver1.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 5);
    }

    @Test
    void testUbl21Invoice() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 21);
    }

    @Test
    void testUbl21CreditNote() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 22);
    }

    @Test
    void testUbl21InvoiceBis05() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 21);
    }

    @Test
    void testUbl21Pakkseddel() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf( "urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2::DespatchAdvice##urn:www.cenbii.eu:transaction:biitrns016:ver1.0:extended:urn:www.peppol.eu:bis:peppol30a:ver1.0:extended:urn:www.difi.no:ehf:pakkseddel:ver1.0::2.1"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 50);
    }

    //
    // Below are UBL 2.0 / EHF 1.x based tests
    //

    @Test
    public void testInvoice() {
        PeppolDocumentTypeIdComparator.CustomizationIdWithOrdinalNumber customizationIdWithOrdinalNumber = comparator.new CustomizationIdWithOrdinalNumber(CustomizationIdentifier.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1"));
        assertTrue(customizationIdWithOrdinalNumber.isPeppolInvoiceOnly());
        assertEquals(customizationIdWithOrdinalNumber.getOrdinalNumber().intValue(), 51);
    }
    
    @Test
    public void testCreditNote() {
        PeppolDocumentTypeIdComparator.CustomizationIdWithOrdinalNumber customizationIdWithOrdinalNumber = comparator.new CustomizationIdWithOrdinalNumber(CustomizationIdentifier.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1"));
        assertTrue(customizationIdWithOrdinalNumber.isEHFCreditNoteOnly());
        assertEquals(customizationIdWithOrdinalNumber.getOrdinalNumber().intValue(), 52);
    }

    @Test
    public void testInvoiceAndBiixyOrdinalNumber() {
        PeppolDocumentTypeIdComparator.CustomizationIdWithOrdinalNumber customizationIdWithOrdinalNumber = comparator.new CustomizationIdWithOrdinalNumber(CustomizationIdentifier.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:faktura:ver1"));
        assertTrue(customizationIdWithOrdinalNumber.isEhfInvoiceCreditNoteAndReminder());
        assertTrue(customizationIdWithOrdinalNumber.isEHFInvoice());
        assertEquals(customizationIdWithOrdinalNumber.getOrdinalNumber().intValue(), 53);
    }

    @Test
    public void testCreditNoteAndBiixyOrdinalNumber() {
        PeppolDocumentTypeIdComparator.CustomizationIdWithOrdinalNumber customizationIdWithOrdinalNumber = comparator.new CustomizationIdWithOrdinalNumber(CustomizationIdentifier.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1"));
        assertTrue(customizationIdWithOrdinalNumber.isEhfInvoiceCreditNoteAndReminder());
        assertTrue(customizationIdWithOrdinalNumber.isEHFCreditNote());
        assertEquals(customizationIdWithOrdinalNumber.getOrdinalNumber().intValue(), 54);
    }

    @Test
    public void testPeppolInvoiceOrdinalNumber() {
        PeppolDocumentTypeIdComparator.CustomizationIdWithOrdinalNumber customizationIdWithOrdinalNumber = comparator.new CustomizationIdWithOrdinalNumber(CustomizationIdentifier.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0"));
        assertTrue(customizationIdWithOrdinalNumber.isPeppolInvoiceOnly());
        assertEquals(customizationIdWithOrdinalNumber.getOrdinalNumber().intValue(), 56);
    }

    @Test void testPeppolApplicationResponse() {
        PeppolDocumentTypeIdComparator.OrdinalNumberProvider ordinalNumber = comparator.getOrdinalNumberProvider(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0"));
        assertEquals(ordinalNumber.getOrdinalNumber().intValue(), 100);
    }

}

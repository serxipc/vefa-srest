package no.sr.ringo.peppol;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 10/4/12
 * Time: 3:15 PM
 */
public class PeppolDocumentTypeIdTest {

    private String documentIdAsText;

    @BeforeMethod
    protected void setUp() throws Exception {
        documentIdAsText = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    }

    @Test
    public void testValueOf() throws Exception {
        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(documentIdAsText);

        assertNotNull(peppolDocumentTypeId);
    }

    @Test
    public void testValueOfWrongValue() throws Exception {
        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1");

        assertNotNull(peppolDocumentTypeId);
        assertTrue(peppolDocumentTypeId instanceof PeppolDocumentTypeId.UnknownPeppolDocumentTypeId);
    }

    @Test
    public void testGetLocalName() throws Exception {
        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(documentIdAsText);

        assertEquals(LocalName.Invoice, peppolDocumentTypeId.getLocalName());
    }

    @Test
    public void testGetCreditInvoiceLocalName() throws Exception {
        PeppolDocumentTypeId peppolDocumentIdString = PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0");
        assertEquals(LocalName.CreditNote, peppolDocumentIdString.getLocalName());
    }


    @Test
    public void testCreditNoteFromIntegrationTestIsValid() throws Exception {
        PeppolDocumentTypeId peppolDocumentIdString = PeppolDocumentTypeId.valueOf("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1");
        assertEquals(peppolDocumentIdString.getLocalName(),LocalName.valueOf("UNKNOWN"));
    }

    @Test
    public void testParsingInvalidPeppolDocumentTypeIdReturnsUnknownDocumentTypeId() throws Exception {
        PeppolDocumentTypeId documentTypeId = PeppolDocumentTypeId.valueOf("");
        assertNotNull(documentTypeId);
        assertEquals(documentTypeId.getClass(), PeppolDocumentTypeId.UnknownPeppolDocumentTypeId.class);

        documentTypeId = PeppolDocumentTypeId.valueOf(null);
        assertNotNull(documentTypeId);
        assertEquals(documentTypeId.getClass(), PeppolDocumentTypeId.UnknownPeppolDocumentTypeId.class);
    }

    @Test
    public void testToString() throws Exception {
        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(documentIdAsText);

        assertEquals(peppolDocumentTypeId.toString(),documentIdAsText);
    }

    @Test
    public void testToStringOnUnknown() throws Exception {
        final String idAsText = "invalid";
        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(idAsText);

        assertEquals(peppolDocumentTypeId.toString(),idAsText);
    }

    @Test
    public void testEqualsForTwoIdenticalPeppolDocumentTypeId() {

        final PeppolDocumentTypeId id1 = PeppolDocumentTypeId.valueOf(documentIdAsText);

        final PeppolDocumentTypeId id2 = PeppolDocumentTypeId.valueOf(documentIdAsText);

        assertTrue(id1.equals(id2));
    }

    @Test
    public void testInvoiceNotEqualToUnknown() throws Exception {

        final PeppolDocumentTypeId invoice = PeppolDocumentTypeId.valueOf(documentIdAsText);

        PeppolDocumentTypeId.UnknownPeppolDocumentTypeId unknown = new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId(documentIdAsText);

        assertFalse(invoice.equals(unknown));
    }

    @Test
    public void testUnknownNotEqualsToDifferentUnknown() throws Exception {
        PeppolDocumentTypeId.UnknownPeppolDocumentTypeId unknown = new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId("rubbish");
        assertFalse(unknown.equals(new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId(documentIdAsText)));
    }

    @Test
    public void testUnknownEqualsToUnknow() throws Exception {
        PeppolDocumentTypeId.UnknownPeppolDocumentTypeId unknown = new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId(documentIdAsText);
        assertTrue(unknown.equals(new PeppolDocumentTypeId.UnknownPeppolDocumentTypeId(documentIdAsText)));
    }

    @Test
    public void testInvoiceNotEqualsToCreditNote() throws Exception {
        final PeppolDocumentTypeId invoice = PeppolDocumentTypeId.valueOf(documentIdAsText);
        PeppolDocumentTypeId creditNote = PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0");
        assertFalse(invoice.equals(creditNote));
    }

    @Test
    public void testHashCode() {
        PeppolDocumentTypeId invalidDocumentTypeId = PeppolDocumentTypeId.valueOf(null);
        try {
            invalidDocumentTypeId.hashCode();
        } catch (NullPointerException e) {
            fail("Should not produce a NPE");
        }
    }

    @Test
    public void testHashCodeSame() {
        PeppolDocumentTypeId invalidDocumentTypeId = PeppolDocumentTypeId.valueOf(null);
        PeppolDocumentTypeId invalidDocumentTypeId2 = PeppolDocumentTypeId.valueOf(null);
        assertEquals(invalidDocumentTypeId.hashCode(),invalidDocumentTypeId2.hashCode());
    }

    @Test
    public void testIllegalValueOf() throws Exception {
        PeppolDocumentTypeId typeId = PeppolDocumentTypeId.valueOf("urn::##urn::");
        assertEquals(typeId.getLocalName(), LocalName.valueOf("UNKNOWN"));
    }

    @Test
    public void testStringValue() throws Exception {
        PeppolDocumentTypeId id = PeppolDocumentTypeId.valueOf(documentIdAsText);
        assertEquals(id.stringValue(),documentIdAsText);
    }

    @Test
    public void testStringValue2() {
        String documentIdAsText1 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0";
        PeppolDocumentTypeId id = PeppolDocumentTypeId.valueOf(documentIdAsText1);
        assertEquals(id.toString(), documentIdAsText1);
    }

    @Test
    public void testServiceProviderContainer() {
        String spc = "urn:ap:unit4.com::ServiceProviderContainer##urn:spc:extended:dmk::1.0";
        PeppolDocumentTypeId id = PeppolDocumentTypeId.valueOf(spc);
        assertNotNull(id);
        assertEquals(id.toString(), spc);
    }

}

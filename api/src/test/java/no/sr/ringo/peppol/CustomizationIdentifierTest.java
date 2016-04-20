package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: steinar
 * Date: 10.10.12
 * Time: 16:42
 */
public class CustomizationIdentifierTest {

    // Customization identifier with 2 extensions
    public static final String EHF_FAKTURA = "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1";
    public static final CustomizationIdentifier FAKTURA = CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T010_INVOICE_V1 + ":#" + ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY + "#" + ProfileId.Predefined.EHF_INVOICE);

    public static final String EHF_FAKTURA_2 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol04a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1";
    public static final String EHF_KREDITNOTA_2 = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1";

    // Customization identifier with 0 extensions for tendering
    public static final String TENDER_DOCID = "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1";
    public static final String TENDER_CUSTID = "urn:www.cenbii.eu:transaction:biitrdm090:ver3.0";
    public static final String TENDER_PROCID = "urn:www.cenbii.eu:profile:bii54:ver3.0"; // ProcessIdentifier

    public static final String TENDER_RECEIPT_DODID = "urn:oasis:names:specification:ubl:schema:xsd:TenderReceipt-2::TenderReceipt##urn:www.cenbii.eu:transaction:biitrdm045:ver3.0::2.1";
    public static final String TENDER_RECEIPT_CUSTID = "urn:www.cenbii.eu:transaction:biitrdm045:ver3.0";
    public static final String TENDER_RECEIPT_PROCID = "urn:www.cenbii.eu:profile:bii54:ver3.0"; // ProcessIdentifier

    public static final String CALL_FOR_TENDER_DODID = "urn:oasis:names:specification:ubl:schema:xsd:CallForTenders::CallForTenders##urn:www.cenbii.eu:transaction:biitrdm083:ver3.0::2.1";
    public static final String CALL_FOR_TENDER_CUSTID = "urn:www.cenbii.eu:transaction:biitrdm083:ver3.0";
    public static final String CALL_FOR_TENDER_PROCID = "urn:www.cenbii.eu:profile:bii47:ver3.0"; // ProcessIdentifier

    private CustomizationIdentifier c1;
    private CustomizationIdentifier c2;
    private CustomizationIdentifier c3;

    @BeforeTest
    public void setUp() {
        c1 = FAKTURA;
        c2 = CustomizationIdentifier.valueOf(FAKTURA.toString());
        c3 = CustomizationIdentifier.valueOf(FAKTURA.toString().replace("ver1", "ver2"));
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(c1, c2);
        assertNotEquals(c1, 3);
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1.hashCode(), c3.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(c1.toString(), EHF_FAKTURA);
    }

    @Test
    public void testNoExtentions() {

        CustomizationIdentifier tenderCustomizationIdentifier = CustomizationIdentifier.valueOf(TENDER_CUSTID);
        assertEquals(tenderCustomizationIdentifier.getFullExtensionIdentifier(), TENDER_CUSTID);

        CustomizationIdentifier tenderReceiptCustomizationIdentifier = CustomizationIdentifier.valueOf(TENDER_RECEIPT_CUSTID);
        assertEquals(tenderReceiptCustomizationIdentifier.getFullExtensionIdentifier(), TENDER_RECEIPT_CUSTID);

        CustomizationIdentifier callForTenderCustomizationIdentifier = CustomizationIdentifier.valueOf(CALL_FOR_TENDER_CUSTID);
        assertEquals(callForTenderCustomizationIdentifier.getFullExtensionIdentifier(), CALL_FOR_TENDER_CUSTID);

    }

    @Test
    public void testMultipleExtensionsIdentities() {
        // CustomizationIdentifier with 3 extensions
        String thirdExtension = "#urn:www.sendregning.no:ehf:regning:ver1";
        CustomizationIdentifier srCustomizationIdentifier = CustomizationIdentifier.valueOf(EHF_FAKTURA + thirdExtension);
        assertTrue(srCustomizationIdentifier.toString().contains(thirdExtension));
        String extensionIdentifierList = srCustomizationIdentifier.getFullExtensionIdentifier();
        assertNotNull(extensionIdentifierList);
        assertTrue(extensionIdentifierList.contains(thirdExtension));
    }

    @Test
    public void testValueOfAndToString() {
        // Creates a rather complicated customization identifier
        CustomizationIdentifier srCustomizationIdentifier = CustomizationIdentifier.valueOf(EHF_FAKTURA + "#urn:www.sendregning.no:ehf:regning:ver1");
        // converts it into a string
        String s = srCustomizationIdentifier.toString();
        // parses the string into a customization identifier
        CustomizationIdentifier actual = CustomizationIdentifier.valueOf(s);
        // verifies that they are still equal.
        assertEquals(actual, srCustomizationIdentifier);
    }

    @Test
    public void testContains() {
        // Creates a rather complicated customization identifier
        CustomizationIdentifier c = CustomizationIdentifier.valueOf(EHF_FAKTURA + "#urn:www.sendregning.no:ehf:regning:ver1");
        assertTrue(c.containsExtension(new ProfileId("urn:www.sendregning.no:ehf:regning:ver1")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class,expectedExceptionsMessageRegExp = ".*not recognized as customization identifier")
    public void testUnexpectedIdentifier() {
        CustomizationIdentifier.valueOf("www.cenbii.eu:transaction:biitrns019:ver2.0");
    }

    @Test
    public void testEHF_v2() {
        try {
            CustomizationIdentifier.valueOf(EHF_FAKTURA_2);
            CustomizationIdentifier.valueOf(EHF_KREDITNOTA_2);
        } catch (Exception ex) {
            fail("Exception when testing EHF 2.0 customization");
        }
    }

}

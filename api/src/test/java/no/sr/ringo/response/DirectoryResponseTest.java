package no.sr.ringo.response;

import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.smp.SmpLookupResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

/**
 * User: Adam
 * Date: 10/12/12
 * Time: 1:23 PM
 */
public class DirectoryResponseTest {

    PeppolDocumentTypeId docType1;
    PeppolDocumentTypeId docType2;
    PeppolDocumentTypeId docType3;

    SmpLookupResult smpLookupResult;

    @BeforeMethod
    public void setUp() throws Exception {
        createSmpLookupResult();
    }

    @Test
    public void testAsXml() throws Exception {

        SmpLookupResponse response = new SmpLookupResponse(smpLookupResult);
        String expected =
                "<directory-response version=\"1.0\">\n" +
                        "  <accepted-document-transfer>\n" +
                        "     <DocumentID>urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0</DocumentID>\n" +
                        "     <ProcessID>urn:www.cenbii.eu:profile:bii04:ver1.0</ProcessID>\n" +
                        "  </accepted-document-transfer>\n" +
                        "  <accepted-document-transfer>\n" +
                        "     <DocumentID>urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0</DocumentID>\n" +
                        "     <ProcessID>urn:www.cenbii.eu:profile:biixx:ver1.0</ProcessID>\n" +
                        "  </accepted-document-transfer>\n" +
                        "  <accepted-document-transfer>\n" +
                        "     <DocumentID>urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0</DocumentID>\n" +
                        "     <ProcessID>urn:www.cenbii.eu:profile:biixy:ver1.0</ProcessID>\n" +
                        "  </accepted-document-transfer>\n" +
                "</directory-response>";
        assertEquals(expected, response.asXml());

    }

    private void createSmpLookupResult() {
        List<PeppolDocumentTypeId> documentTypes = prepareDocumentTypes();
        smpLookupResult = new SmpLookupResult(documentTypes);
    }

    private List<PeppolDocumentTypeId> prepareDocumentTypes() {

        docType1 = PeppolDocumentTypeId.valueFor("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0");
        docType2 = PeppolDocumentTypeId.valueFor("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");
        docType3 = PeppolDocumentTypeId.valueFor("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");

        List<PeppolDocumentTypeId> result = new ArrayList<PeppolDocumentTypeId>();
        result.add(docType1);
        result.add(docType2);
        result.add(docType3);

        return result;
    }

}

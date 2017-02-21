package no.sr.ringo.document;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Testing the different EHF 2.0 variants, making sure we understand them correctly.
 * @author Thore Johnsen
 */
public class FileClientPeppolEHF2Test {

    private String RESOURCE_EHF2_INVOICE = "/ehf2/EHF_2_0_Faktura.xml";
    private String RESOURCE_EHF2_CREDITNOTE = "/ehf2/EHF_2_0_Kreditnota.xml";

    public static final String EHF_FAKTURA_2 = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol04a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1";
    public static final String EHF_KREDITNOTA_2 = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1";

    @Test
    public void testFindDocumentTypeForEhf2Invoice() throws Exception {
        FileClientPeppolDocument document = getFileClientPeppolDocumentFromResource(RESOURCE_EHF2_INVOICE);
        DocumentTypeIdentifier result = document.findDocumentType();
        assertNotNull(result);
        assertEquals(result.getIdentifier(), EHF_FAKTURA_2);
    }

    @Test
    public void testFindDocumentTypeForEhf2CreditNote() throws Exception {
        FileClientPeppolDocument document = getFileClientPeppolDocumentFromResource(RESOURCE_EHF2_CREDITNOTE);
        DocumentTypeIdentifier result = document.findDocumentType();
        assertNotNull(result);
        assertEquals(result.getIdentifier(), EHF_KREDITNOTA_2);
    }

    @Test
    public void testDecodingOfEhf2Invoice() throws Exception {

        FileClientPeppolDocument document = getFileClientPeppolDocumentFromResource(RESOURCE_EHF2_INVOICE);

        // make sure we have the correct document type
        DocumentTypeIdentifier documentType = document.findDocumentType();
        assertNotNull(documentType);
        System.out.println(documentType);

        // make sure we have the correct profile
        ProcessIdentifier profileId = document.findProfileId();
        assertNotNull(profileId);
        System.out.println(profileId.toString());

        // makes sure we have the correct receiver
        ParticipantIdentifier receiver = document.findReceiver();
        assertNotNull(receiver);
        System.out.println(receiver.toString());

        // make sure we have the correct sender
        ParticipantIdentifier sender = document.findSender();
        assertNotNull(sender);
        System.out.println(sender.toString());

    }


    @Test
    public void testDecodingOfEhf2CreditNote() throws Exception {

        FileClientPeppolDocument document = getFileClientPeppolDocumentFromResource(RESOURCE_EHF2_CREDITNOTE);

        // make sure we have the correct document type
        DocumentTypeIdentifier documentType = document.findDocumentType();
        assertNotNull(documentType);
        System.out.println(documentType);

        // make sure we have the correct profile
        ProcessIdentifier profileId = document.findProfileId();
        assertNotNull(profileId);
        System.out.println(profileId.toString());

        // makes sure we have the correct receiver
        ParticipantIdentifier receiver = document.findReceiver();
        assertNotNull(receiver);
        System.out.println(receiver.toString());

        // make sure we have the correct sender
        ParticipantIdentifier sender = document.findSender();
        assertNotNull(sender);
        System.out.println(sender.toString());

    }

    private FileClientPeppolDocument getFileClientPeppolDocumentFromResource(String resource) throws Exception {
        URL url = FileClientPeppolEHF2Test.class.getResource(resource);
        File file = new File(url.toURI());
        return new FileClientPeppolDocument(file);
    }

}

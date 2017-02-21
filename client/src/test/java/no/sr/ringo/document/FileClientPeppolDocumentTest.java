package no.sr.ringo.document;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.sr.ringo.cenbiimeta.ProfileId;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Making sure we decode the EHF 1.x documents correctly.
 * User: andy
 * Date: 10/29/12
 * Time: 1:21 PM
 */
public class FileClientPeppolDocumentTest {

    @Test
    public void testFindRecipient() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/participantWithoutMVA.xml");
        File testFile = new File(validInvoiceUrl.toURI());

        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findReceiver();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:976098897", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindRecipientWithMva() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/participantWithMva.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findReceiver();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:976098897", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindRecipient2() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/validInvoice.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findReceiver();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9999:983974724", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindRecipient3() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/BII04 T10 gyldig faktura med alle elementer.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findReceiver();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9999:976098897", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindRecipient4() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/BII05 T10 0 gyldig faktura med vanlige dataelementer.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findReceiver();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:976098897", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindSender() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/senderWithoutMva.xml");
        File testFile = new File(validInvoiceUrl.toURI());

        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ParticipantIdentifier result = document.findSender();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:540269750", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindSenderWithMVA() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/senderWithMva.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);

        ParticipantIdentifier result = document.findSender();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:540269750", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindSender2() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/validInvoice.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);

        ParticipantIdentifier result = document.findSender();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9902:540269750", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindSender3() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/BII04 T10 gyldig faktura med alle elementer.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);

        ParticipantIdentifier result = document.findSender();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9902:999999999", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindSender4() throws Exception {

        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/BII05 T10 0 gyldig faktura med vanlige dataelementer.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);

        ParticipantIdentifier result = document.findSender();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"9908:976098897", String.format("Unexpected participant id '%s' parsing test file : %s", result.getIdentifier(),validInvoiceUrl));
    }

    @Test
    public void testFindDocumentTypeForInvoice() throws Exception {
        final URL validInvoiceUrl = FileClientPeppolDocumentTest.class.getResource("/BII05 T10 0 gyldig faktura med vanlige dataelementer.xml");
        File testFile = new File(validInvoiceUrl.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);

        DocumentTypeIdentifier result = document.findDocumentType();

        assertNotNull(result);
        assertEquals(result.getIdentifier(), "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0");

    }

    @Test
    public void testFindDocumentTypeForCreditNote() throws Exception {
        final URL validCreditNoteUrl = FileClientPeppolDocumentTest.class.getResource("/BII05 T14 0 gyldig kreditnota med alle elementer.xml");
        File testFile = new File(validCreditNoteUrl.toURI());

        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        DocumentTypeIdentifier result = document.findDocumentType();

        assertNotNull(result);
        assertEquals(result.getIdentifier(),"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");
    }

    @Test
    public void testFindProcessId() throws Exception {
        final URL validCreditNoteUrl = FileClientPeppolDocumentTest.class.getResource("/BII05 T14 0 gyldig kreditnota med alle elementer.xml");
        File testFile = new File(validCreditNoteUrl.toURI());
        String processIdString = "urn:www.cenbii.eu:profile:bii05:ver1.0";
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ProcessIdentifier result = document.findProfileId();

        assertNotNull(result);
        assertEquals(processIdString, result.getIdentifier());
    }

    @Test
    public void testIsoEncodedFile() throws Exception {
        final URL invoice = FileClientPeppolDocumentTest.class.getResource("/iso-encoded-invoice.xml");
        File testFile = new File(invoice.toURI());
        FileClientPeppolDocument document = new FileClientPeppolDocument(testFile);
        ProcessIdentifier result = document.findProfileId();

        assertNotNull(result);
        assertEquals(result,ProfileId.Predefined.BII05_BILLING.toVefa());
    }

}

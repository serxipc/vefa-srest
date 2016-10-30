package no.sr.ringo.client;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.http.AbstractHttpClientServerTest;
import no.sr.ringo.peppol.*;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.testng.Assert;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.*;
import static org.testng.Assert.*;

/**
 * @author Steinar Overbeck Cook
 * @author Thore Johnsen
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class OutboxIntegrationTest extends AbstractHttpClientServerTest {

    PeppolDocumentTypeId ehfInvoicePeppolDocumentTypeId = PeppolDocumentTypeId.valueFor("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0");
    PeppolDocumentTypeId creditNotePeppolDocumentTypeId = PeppolDocumentTypeId.valueFor("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");

    @Inject
    DatabaseHelper databaseHelper;

    UploadMode uploadMode = UploadMode.SINGLE;

    /**
     * Uploads an XML document and attempts to download the invoice by using the supplied URL.
     * <p/>
     * <ol>
     * <li>Upload to /outbox</li>
     * <li>Download the XML Invoice Document from /outbox/{message_no}/xml-document</li>
     * </ol>
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = {"integration"})
    public void uploadSampleInvoice() throws Exception {

        final Account account = ObjectMother.getTestAccount();
        final ParticipantId sender = ObjectMother.getTestParticipantIdForSMPLookup();

        File file = ClientObjectMother.getTestInvoice();

        final PeppolChannelId channel = new PeppolChannelId("Test");
        final Message message = ringoRestClientImpl.send(file, channel, PeppolParticipantId.valueFor(sender.stringValue()), PeppolParticipantId.valueFor(sender.stringValue()), uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNull(message.getContents().getUuid());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().stringValue(), sender.stringValue());
        assertEquals(peppolHeader.getSender().stringValue(), sender.stringValue());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProfileId(), BII04_INVOICE_ONLY);
    }

    @Test(groups = {"integration"})
    public void uploadSampleCreditInvoice() throws Exception {

        final ParticipantId sender = ObjectMother.getTestParticipantIdForSMPLookup();

        File file = ClientObjectMother.getTestCreditNote();

        final PeppolChannelId channel = new PeppolChannelId("Test");

        final Message message = ringoRestClientImpl.send(file, channel, PeppolParticipantId.valueFor(sender.stringValue()), PeppolParticipantId.valueFor(sender.stringValue()), uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNull(message.getContents().getUuid());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().stringValue(), sender.stringValue());
        assertEquals(peppolHeader.getSender().stringValue(), sender.stringValue());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), creditNotePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProfileId(), PROPOSED_BII_XX);
    }


    /**
     * Tests that sender and recipient will be extracted from file
     */
    @Test(groups = {"integration"})
    public void uploadSampleInvoiceWithoutRecipientAndSenderSpecified() throws Exception {

        File file = ClientObjectMother.getTestInvoice();

        final PeppolChannelId channel = new PeppolChannelId("Test");
        final Message message = ringoRestClientImpl.send(file, channel, null, null, uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNull(message.getContents().getUuid());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().stringValue(), "9908:985420289");
        assertEquals(peppolHeader.getSender().stringValue(), "9908:891382529");
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProfileId(), BII04_INVOICE_ONLY);

    }

    /**
     * Verifies that we can perform HTTP GET /upload/upload.do and receive the upload.jsp page as the response.
     *
     * This test no longer works when running with Jetty embedded via Maven :-(
     * Works fine in IntelliJ
     *  -- Steinar Oct. 30, 2016
     *
     * @throws IOException
     */
    @Test(groups = {"integration","external"})
    public void verifyUploadHtmlPage() throws IOException {

        HttpGet httpGet = new HttpGet(PEPPOL_BASE_URL + "/upload/upload.do");
        HttpResponse httpResponse = httpClient.execute(httpGet);

        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }


    /**
     * Uploads an XML document as a Stream and attempts to download the invoice by using the supplied URL.
     * <p/>
     * <ol>
     * <li>Upload to /outbox</li>
     * <li>Download the XML Invoice Document from /outbox/{message_no}/xml-document</li>
     * </ol>
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = {"integration"})
    public void uploadSampleInvoiceAsStream() throws Exception {

        File file = ClientObjectMother.getTestInvoice();
        final ReaderInputStream readerInputStream = new ReaderInputStream(new InputStreamReader(new FileInputStream(file), "UTF-8"));

        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.EHF_INVOICE;
        PeppolParticipantId participantId = PeppolParticipantId.valueFor("0002:1234");
        final Message message = ringoRestClientImpl.send(readerInputStream, PeppolHeader.forDocumentType(peppolDocumentTypeId, participantId, participantId));

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNull(message.getContents().getUuid());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().stringValue(), participantId.stringValue());
        assertEquals(peppolHeader.getSender().stringValue(), participantId.stringValue());
        assertEquals(peppolHeader.getPeppolChannelId(), new PeppolChannelId("SendRegning"));
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProfileId(), BII04_INVOICE_ONLY);

    }

    @Test(groups = {"integration"})
    public void uploadSampleCreditInvoiceAsStream() throws Exception {

        File file = ClientObjectMother.getTestCreditNote();
        final ReaderInputStream readerInputStream = new ReaderInputStream(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        final PeppolChannelId channel = new PeppolChannelId("SendRegning");

        PeppolDocumentTypeId peppolDocumentTypeId = creditNotePeppolDocumentTypeId;
        PeppolParticipantId participantId = PeppolParticipantId.valueFor("0002:1234");
        final Message message = ringoRestClientImpl.send(readerInputStream, PeppolHeader.forDocumentType(peppolDocumentTypeId, participantId, participantId));

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNull(message.getContents().getUuid());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().stringValue(), participantId.stringValue());
        assertEquals(peppolHeader.getSender().stringValue(), participantId.stringValue());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), creditNotePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProfileId(), PROPOSED_BII_XX);

    }


    @Test(groups = {"integration"})
    public void uploadSampleInvoiceInvalidDocumentId() throws URISyntaxException, IOException {

        final ParticipantId sender = ObjectMother.getTestParticipantIdForSMPLookup();
        File file = ClientObjectMother.getTestInvoice();

            HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            multipartEntity.addPart("file", new FileBody(file, "application/xml"));
            multipartEntity.addPart("RecipientID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("SenderID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ProcessID", new StringBody(ProfileId.Predefined.BII04_INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("DocumentID", new StringBody("WrongDocumentId", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

            httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        System.out.println(response.toString());
        assertEquals(400, response.getStatusLine().getStatusCode());


    }


    @Test(groups = {"integration"})
    public void uploadSampleInvoiceInvalidProcessId() throws URISyntaxException, IOException {

        final ParticipantId sender = ObjectMother.getTestParticipantIdForSMPLookup();
        File file = ClientObjectMother.getTestInvoice();

            HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            multipartEntity.addPart("file", new FileBody(file, "application/xml"));
            multipartEntity.addPart("RecipientID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("SenderID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ProcessID", new StringBody("WrongProcessId", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("DocumentID", new StringBody(LocalName.Invoice.toString().toUpperCase(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

            httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        System.out.println(response.toString());
        assertEquals(response.getStatusLine().getStatusCode(),400);

    }

    @Test(groups = {"integration"})
    public void uploadSampleInvoiceInvalidXml() throws URISyntaxException, IOException {

        databaseHelper.updateValidateFlagOnAccount(new AccountId(1), true);
        try {
            final ParticipantId sender = ObjectMother.getTestParticipantIdForSMPLookup();
            File file = ClientObjectMother.getTestInvoiceInvalid();

                HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                multipartEntity.addPart("file", new FileBody(file, "application/xml"));
                multipartEntity.addPart("RecipientID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("SenderID", new StringBody(sender.stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("ProcessID", new StringBody(ProfileId.Predefined.BII04_INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("DocumentID", new StringBody(LocalName.Invoice.toString().toUpperCase(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

                httpPost.setEntity(multipartEntity);

            HttpResponse response = httpClient.execute(httpPost);
            assertEquals(response.getStatusLine().getStatusCode(), 400);
            String message = convertEntityToString(response);
            assertTrue(message.startsWith("XmlDocument was unknown or corrupt") || message.startsWith("Unable to validate the XML document"));

        } finally {
            databaseHelper.updateValidateFlagOnAccount(new AccountId(1), false);
        }
    }

    // TODO : implement integration test that validates MLR and Norwegian EHF 2.0 formats

}

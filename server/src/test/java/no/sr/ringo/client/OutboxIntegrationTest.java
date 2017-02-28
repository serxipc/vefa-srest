package no.sr.ringo.client;

import com.google.inject.Inject;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.http.AbstractHttpClientServerTest;
import no.sr.ringo.peppol.*;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
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

import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.BII04_INVOICE_ONLY;
import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.PROPOSED_BII_XX;
import static org.testng.Assert.*;

/**
 * @author Steinar Overbeck Cook
 * @author Thore Johnsen
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class OutboxIntegrationTest extends AbstractHttpClientServerTest {

    //PeppolDocumentTypeId.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0");
    DocumentTypeIdentifier ehfInvoicePeppolDocumentTypeId = PeppolDocumentTypeId.EHF_INVOICE.toVefa();
    // of("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0");
    DocumentTypeIdentifier creditNotePeppolDocumentTypeId = PeppolDocumentTypeId.EHF_CREDIT_NOTE.toVefa();

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
        final ParticipantIdentifier sender = ObjectMother.getTestParticipantIdForSMPLookup();

        File file = ClientObjectMother.getTestInvoice();

        final PeppolChannelId channel = new PeppolChannelId(ChannelProtocol.SREST.name());
        final Message message = ringoRestClientImpl.send(file, channel, sender, sender, uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);

        assertNotNull(message.getContents().getReceptionId());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().getIdentifier(), sender.getIdentifier());
        assertEquals(peppolHeader.getSender().getIdentifier(), sender.getIdentifier());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProcessIdentifier(), BII04_INVOICE_ONLY.toVefa());
    }

    @Test(groups = {"integration"})
    public void uploadSampleCreditInvoice() throws Exception {

        final ParticipantIdentifier sender = ObjectMother.getTestParticipantIdForSMPLookup();

        File file = ClientObjectMother.getTestCreditNote();

        final PeppolChannelId channel = new PeppolChannelId(ChannelProtocol.SREST.name());

        final Message message = ringoRestClientImpl.send(file, channel, sender, sender, uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNotNull(message.getContents().getReceptionId());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().getIdentifier(), sender.getIdentifier());
        assertEquals(peppolHeader.getSender().getIdentifier(), sender.getIdentifier());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), creditNotePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProcessIdentifier(), PROPOSED_BII_XX.toVefa());
    }


    /**
     * Tests that sender and recipient will be extracted from file
     */
    @Test(groups = {"integration"})
    public void uploadSampleInvoiceWithoutRecipientAndSenderSpecified() throws Exception {

        File file = ClientObjectMother.getTestInvoice();

        final PeppolChannelId channel = new PeppolChannelId(ChannelProtocol.SREST.name());
        final Message message = ringoRestClientImpl.send(file, channel, null, null, uploadMode);

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNotNull(message.getContents().getReceptionId());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().getIdentifier(), "9908:985420289");
        assertEquals(peppolHeader.getSender().getIdentifier(), "9908:891382529");
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProcessIdentifier(), BII04_INVOICE_ONLY.toVefa());

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
    @Test(groups = {"external"})
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
        ParticipantIdentifier participantId = ParticipantIdentifier.of("0002:1234");
        final Message message = ringoRestClientImpl.send(readerInputStream, PeppolHeader.forDocumentType(peppolDocumentTypeId.toVefa(), PeppolProcessIdAcronym.INVOICE_ONLY.toVefa(),participantId, participantId));

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNotNull(message.getContents().getReceptionId());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().getIdentifier(), participantId.getIdentifier());
        assertEquals(peppolHeader.getSender().getIdentifier(), participantId.getIdentifier());
        assertEquals(peppolHeader.getPeppolChannelId(), new PeppolChannelId(ChannelProtocol.SREST.name()));
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), ehfInvoicePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProcessIdentifier(), BII04_INVOICE_ONLY.toVefa());

    }

    @Test(groups = {"integration"})
    public void uploadSampleCreditInvoiceAsStream() throws Exception {

        File file = ClientObjectMother.getTestCreditNote();
        final ReaderInputStream readerInputStream = new ReaderInputStream(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        final PeppolChannelId channel = new PeppolChannelId(ChannelProtocol.SREST.name());

        ParticipantIdentifier participantId = ParticipantIdentifier.of("0002:1234");
        final Message message = ringoRestClientImpl.send(readerInputStream, PeppolHeader.forDocumentType(creditNotePeppolDocumentTypeId, PeppolProcessIdAcronym.INVOICE_ONLY.toVefa(),participantId, participantId));

        assertNotNull(message);
        assertNull(message.getContents().getDelivered());
        assertNotNull(message.getContents().getReceived());
        Assert.assertEquals(message.getContents().getTransferDirection(), TransferDirection.OUT);
        assertNotNull(message.getContents().getReceptionId());
        assertNotNull(message.getContents().getMsgNo());

        final PeppolHeader peppolHeader = message.getContents().getPeppolHeader();
        assertEquals(peppolHeader.getReceiver().getIdentifier(), participantId.getIdentifier());
        assertEquals(peppolHeader.getSender().getIdentifier(), participantId.getIdentifier());
        assertEquals(peppolHeader.getPeppolChannelId(), channel);
        assertEquals(peppolHeader.getPeppolDocumentTypeId(), creditNotePeppolDocumentTypeId);
        assertEquals(peppolHeader.getProcessIdentifier(), PeppolProcessIdAcronym.INVOICE_ONLY.toVefa());

    }



    @Test(groups = {"integration"})
    public void uploadSampleInvoiceInvalidXml() throws URISyntaxException, IOException {

        databaseHelper.updateValidateFlagOnAccount(new AccountId(1), true);
        try {
            final ParticipantIdentifier sender = ObjectMother.getTestParticipantIdForSMPLookup();
            File file = ClientObjectMother.getTestInvoiceInvalid();

                HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                multipartEntity.addPart("file", new FileBody(file, "application/xml"));
                multipartEntity.addPart("RecipientID", new StringBody(sender.getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("SenderID", new StringBody(sender.getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("ProcessID", new StringBody(ProfileId.Predefined.BII04_INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
                multipartEntity.addPart("DocumentID", new StringBody(LocalName.Invoice.toString().toUpperCase(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

                httpPost.setEntity(multipartEntity);

            HttpResponse response = httpClient.execute(httpPost);
            assertEquals(response.getStatusLine().getStatusCode(), 400);

        } finally {
            databaseHelper.updateValidateFlagOnAccount(new AccountId(1), false);
        }
    }

    // TODO : implement integration test that validates MLR and Norwegian EHF 2.0 formats

}

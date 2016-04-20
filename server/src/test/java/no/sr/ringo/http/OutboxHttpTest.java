package no.sr.ringo.http;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.client.ClientObjectMother;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author andy
 * @author thore
 */
public class OutboxHttpTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(OutboxHttpTest.class);

    @Test(groups = {"integration"})
    public void uploadSampleInvoice() throws Exception {

        //try uploading :)
        File file = ClientObjectMother.getTestInvoice();
        String uploadUrlString = PEPPOL_BASE_REST_URL + "/outbox";
        HttpPost httpPost = new HttpPost(new URI(uploadUrlString));

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET));
        ContentBody contentBody = new FileBody(file);
        multipartEntity.addPart("file", contentBody);
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("Test", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(ProfileId.Predefined.BII04_INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeId.EHF_INVOICE.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        httpPost.setEntity(multipartEntity);

        //uploads the invoice
        final HttpResponse httpResponse = httpClient.execute(httpPost);
        log.info(EntityUtils.toString(httpResponse.getEntity()));
        assertEquals(httpResponse.getStatusLine().getStatusCode(), 201);

        //checks that the outbox now contains the invoice uploaded.
        HttpGet httpGet = new HttpGet(new URI(uploadUrlString));
        final HttpResponse outBoxResponse = httpClient.execute(httpGet);

        assertEquals(outBoxResponse.getStatusLine().getStatusCode(), 200);
        final String outBoxAsXml = EntityUtils.toString(outBoxResponse.getEntity());

        assertTrue(outBoxAsXml.contains("<message>"),outBoxAsXml);

    }

    @Test(groups = {"integration"})
    public void testPostOutboxEhf2DocumentId() throws URISyntaxException, IOException, SQLException {

        String uploadUrlString = PEPPOL_BASE_REST_URL + "/outbox";

        HttpPost httpPost = new HttpPost(uploadUrlString);

        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        String processId = "urn:www.cenbii.eu:profile:bii05:ver2.0";
        String documentId = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1";
        String participantId = "9908:910667831";

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(participantId, "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(participantId,  "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("EHF2TEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(processId, "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(documentId, "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        httpPost.setEntity(multipartEntity);

        //CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        //credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        //httpClient.setCredentialsProvider(credentialsProvider);

        HttpResponse response = httpClient.execute(httpPost);

        //assertEquals(ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET), "Wrong senderId value: WrongSenderId");
        assertEquals(response.getStatusLine().getStatusCode(), 201); // http created

    }

}

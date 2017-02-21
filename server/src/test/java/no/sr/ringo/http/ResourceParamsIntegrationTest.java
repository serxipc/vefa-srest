package no.sr.ringo.http;

import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.client.ClientObjectMother;
import no.sr.ringo.common.ResponseUtils;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.guice.ServerTestModuleFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

/**
 * @author Adam Mscisz adam@sendregning.no
 *         <p/>
 *         Tests that wrong parameters passed to resources will cause 400
 */
//@Guice(modules = {ServerTestDataSourceModule.class, RingoServiceModule.class})
@Guice(moduleFactory = ServerTestModuleFactory.class)

public class ResourceParamsIntegrationTest extends AbstractHttpClientServerTest {

    /**
     * inbox/msgNo/read returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testInboxReadWrongMessageNo() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/inbox/notANumber/read";
        URI directoryLookupUri = new URI(s);
        HttpPost httpPost = new HttpPost(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpPost);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * inbox/msgNo returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testInboxWrongMessageNo() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/inbox/notANumber";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * inbox/msgNo/xml-message returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testInboxWrongMessageNoForXmlDocument() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/inbox/notANumber/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * inbox/msgNo returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testOutboxWrongMessageNo() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/outbox/notANumber";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * inbox/msgNo/xml-message returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testOutboxWrongMessageNoForXmlDocument() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/outbox/notANumber/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * messages/msgNo returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testMessagesWrongMessageNo() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/messages/notANumber";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * messages/msgNo/xml-message returns 400 BAD REQUEST if found
     */
    @Test(groups = {"integration"})
    public void testMessagesWrongMessageNoForXmlDocument() throws URISyntaxException, IOException, SQLException {

        String s = PEPPOL_BASE_URL.toString() + "/messages/notANumber/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals("Invalid message number 'notANumber'", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 400);
    }

    /**
     * post outbox - proper params
     */
    @Test(groups = {"integration"})
    public void testPostOutboxProperParams() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(PeppolProcessTypeIdAcronym.INVOICE_ONLY.toVefa().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeIdAcronym.INVOICE.toVefa().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        assertEquals(response.getStatusLine().getStatusCode(), 201);
    }

    /**
     * post outbox/returns 400
     */
    @Test(groups = {"integration"})
    public void testPostOutboxWrongProcessId() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("ProcessID", new StringBody("WrongProcessId", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeIdAcronym.INVOICE.toVefa().toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        // We no longer perform any checks, anything goes
        // assertEquals("Wrong processId value: WrongProcessId", ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
        assertEquals(response.getStatusLine().getStatusCode(), 201);
    }

    /**
     * post outbox/returns 400
     */
    @Test(groups = {"integration"})
    public void testPostOutboxWrongRecipientId() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody("WrongRecipientId", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(PeppolProcessTypeIdAcronym.INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeIdAcronym.INVOICE.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        assertEquals(response.getStatusLine().getStatusCode(), 201);
    }

    /**
     * post outbox/returns 400
     */
    @Test(groups = {"integration"})
    public void testPostOutboxWrongSenderId() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody("WrongSenderId",  "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(PeppolProcessTypeIdAcronym.INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeIdAcronym.INVOICE.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        assertEquals(response.getStatusLine().getStatusCode(), 201); // Perfectly ok.
    }

    /**
     * post outbox/returns 400
     */
    @Test(groups = {"integration"})
    public void testPostOutboxWrongDocumentId() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody(PeppolProcessTypeIdAcronym.INVOICE_ONLY.toString(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody("WrongDocumentId", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);

        HttpResponse response = httpClient.execute(httpPost);

        assertEquals(response.getStatusLine().getStatusCode(), 201);    // We allow invalid document type identifiers
    }

    /**
     * post outbox/returns 400
     */
    @Test(groups = {"integration"})
    public void testPostOutboxServiceProviderContainer() throws URISyntaxException, IOException, SQLException {

        HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        final File file = ClientObjectMother.getTestInvoice();

        multipartEntity.addPart("file", new FileBody(file, "application/xml"));
        multipartEntity.addPart("RecipientID", new StringBody(ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("SenderID", new StringBody(ObjectMother.getTestParticipantIdForConsumerReceiver().getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ChannelID", new StringBody("TEST_ONLY", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("ProcessID", new StringBody("urn:dmk", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
        multipartEntity.addPart("DocumentID", new StringBody("urn:ap:unit4.com::ServiceProviderContainer##urn:spc:extended:dmk::1.0", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

        httpPost.setEntity(multipartEntity);
        HttpResponse response = httpClient.execute(httpPost);
        assertEquals(response.getStatusLine().getStatusCode(), 201);

    }

}


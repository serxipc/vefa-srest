package no.sr.ringo.http;

import com.google.inject.Inject;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.guice.TestModuleFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

/**
 * @author Adam Mscisz adam@sendregning.no
 * Tests HTTP status code returned by resource
 * insertSample and deleteSample methods used only in those tests that require it
 */
//@Guice(modules = {TestDataSourceModule.class,RingoServiceModule.class})
@Guice(moduleFactory = TestModuleFactory.class)

public class HttpStatusCodeIntegrationTest extends AbstractHttpClientServerTest {

    @Inject
    DatabaseHelper databaseHelper;

    private Long messageId;

    @Test(groups = {"integration"})
    /**
     * inbox/msgNo returns 200 if found
     */
    public void testHttpStatusCodeInboxMessage() throws URISyntaxException, IOException, SQLException {

        insertSample();
        try{
            String s = PEPPOL_BASE_URL.toString() + "/inbox/" + messageId;
            URI directoryLookupUri = new URI(s);
            HttpGet httpGet = new HttpGet(directoryLookupUri);

            HttpResponse response = httpClient.execute(httpGet);

            assertEquals(response.getStatusLine().getStatusCode(), 200);
        } finally {
            deleteSample();
        }

    }

    @Test(groups = {"integration"})
    /**
     * messages/msgNo returns 200 if found
     */
    public void testHttpStatusCodeMessagesMessage() throws URISyntaxException, IOException, SQLException {

        insertSample();

        try{
            String s = PEPPOL_BASE_URL.toString() + "/messages/" + messageId;
            URI directoryLookupUri = new URI(s);
            HttpGet httpGet = new HttpGet(directoryLookupUri);

            HttpResponse response = httpClient.execute(httpGet);

            assertEquals(response.getStatusLine().getStatusCode(),200);
        } finally {
            deleteSample();
        }

    }

    @Test(groups = {"integration"})
    /**
     * messages/msgNo returns 204 if not found
     */
    public void testHttpStatusCodeMessagesNotFound() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/messages/" + 999999999;
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    @Test(groups = {"integration"})
    /**
     * outbox/msgNo returns 204 if not found
     */
    public void testHttpStatusCodeOutboxNotFound() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/outbox/" + 999999999;
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    @Test(groups = {"integration"})
    /**
     * inbox/msgNo returns 204 if not found
     */
    public void testHttpStatusCodeInboxNotFound() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/inbox/" + 999999999;
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    @Test(groups = {"integration"})
    /**
     * messages/msgNo/xml-document returns 204 if not found
     */
    public void testHttpStatusCodeMessagesNotFoundXml() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/messages/" + 999999999 + "/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    @Test(groups = {"integration"})
    /**
     * outbox/msgNo/xml-document returns 204 if not found
     */
    public void testHttpStatusCodeOutboxNotFoundXml() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/outbox/" + 999999999 + "/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    @Test(groups = {"integration"})
    /**
     * inbox/msgNo/xml-document returns 204 if not found
     */
    public void testHttpStatusCodeInboxNotFoundXml() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/inbox/" + 999999999 + "/xml-document";
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),204);

    }

    /**
     * Helper methods
     */
    public void insertSample() throws SQLException {
        final Account account = ObjectMother.getTestAccount();
        messageId = databaseHelper.createMessage(account.getAccountId().toInteger(), TransferDirection.IN, ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), UUID.randomUUID().toString(), null);
    }

    public void deleteSample() throws SQLException {
        if(messageId != null){
            databaseHelper.deleteMessage(messageId);
        }
    }

}


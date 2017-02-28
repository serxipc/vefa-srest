package no.sr.ringo.http;

import no.difi.ringo.tools.PersistenceObjectMother;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Message;
import no.sr.ringo.client.Messages;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.*;
import no.sr.ringo.persistence.DbmsTestHelper;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.testng.Assert.*;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class InboxIntegrationTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(InboxIntegrationTest.class);
    @Inject
    DatabaseHelper databaseHelper;
    @com.google.inject.Inject
    DbmsTestHelper dbmsTestHelper;
    @Inject
    PeppolMessageRepository peppolMessageRepository;
    private Long msgNo;
    private ReceptionId receptionId;

    /**
     * Retrieves messages from inbox
     *
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    @Test(groups = {"integration"})
    public void count() throws IOException, URISyntaxException {

        Inbox inbox = ringoRestClientImpl.getInbox();

        Integer count = inbox.getCount();
        assertNotNull(count);

        assertTrue(count >= 0);
    }

    @Test(groups = {"integration"})
    public void testGetContentsOfInbox() throws Exception {

        Inbox inbox = ringoRestClientImpl.getInbox();

        final Messages messages = inbox.getMessages();

        int count = 0;
        boolean foundOneOfTheInserted = false;

        for (Message message : messages) {
            count++;
            // Assumes there is only a single message, of by insertSample(), in the table

            if (message.getReceptionId().equals(receptionId))
                foundOneOfTheInserted = true;
        }
        assertTrue(count > 0, "Expected more than 0 messages");
        assertTrue(foundOneOfTheInserted);
    }


    /**
     * Inserts a sample entry into the database and verifies that
     * the payload is handled correctly
     *
     * @throws Exception
     */
    @Test(groups = {"integration"})
    public void testGetPayload() throws Exception {

        try {
            String s = PEPPOL_BASE_URL.toString() + "/messages/" + msgNo + "/xml-document";
            URI directoryLookupUri = new URI(s);
            HttpGet httpGet = new HttpGet(directoryLookupUri);

            HttpResponse response = httpClient.execute(httpGet);

            final String txt = EntityUtils.toString(response.getEntity());

            assertEquals(txt, DbmsTestHelper.PAYLOAD_DATA);
            assertEquals(response.getStatusLine().getStatusCode(),200);
        } finally {
            deleteSample();
        }

        final MessageMetaDataImpl messageMetaData = PersistenceObjectMother.sampleInboundTransmissionMetaData();
        messageMetaData.setPayloadUri(URI.create("http://www.peppol.eu"));   // This is where we will be redirected
        final MessageNumber messageNumber = databaseHelper.createSampleEntry(messageMetaData);

        try {
            String s = PEPPOL_BASE_REST_URL.toString() + "/messages/" + messageNumber.toString() + "/xml-document";
            final URI uri = URI.create(s);
            final HttpGet httpGet = new HttpGet(uri);

            httpClient.setRedirectStrategy(new RedirectStrategy() {
                @Override
                public boolean isRedirected(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                    return false;
                }

                @Override
                public HttpUriRequest getRedirect(HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
                    return null;
                }
            });

            final HttpResponse response = httpClient.execute(httpGet);
            assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_SEE_OTHER);

        }   finally {
            databaseHelper.deleteMessage(messageNumber.toLong());
        }

    }

    @BeforeMethod(groups = {"integration"})
    public void insertSample() throws SQLException {
        final Account account = ObjectMother.getTestAccount();
        receptionId = new ReceptionId();

        msgNo = dbmsTestHelper.createSampleMessage(1, TransferDirection.IN, ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), ObjectMother.getTestParticipantIdForSMPLookup().getIdentifier(), receptionId, null);
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(MessageNumber.of(msgNo));


    }

    @AfterMethod(groups = {"integration"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteMessage(msgNo);
    }
}


package no.sr.ringo.http;

import com.google.inject.Inject;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.TransferDirection;
import no.sr.ringo.message.statistics.RingoStatistics;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests the admin resource...
 *
 *
 * User: Adam
 * Date: 2/20/12
 * Time: 3:14 PM
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class AdminResourceTest extends AbstractHttpClientServerTest {

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    PeppolMessageRepository peppolMessageRepository;

    private List<Integer> messageIds = new ArrayList<Integer>();
    private RingoAccount testAccount = ObjectMother.getTestAccount();

    /**
     * By default there's no admin constraint, so this will return forbidden
     * @throws IOException
     */
    @Test(groups = {"integration"})
    public void testAdminStatusWithoutAdminRole() throws IOException {

        HttpGet httpGet = new HttpGet(PEPPOL_BASE_URL + "/admin/status");
        HttpResponse httpResponse = httpClient.execute(httpGet);

        // it's forbidden without 'admin' role and our testuser only have "client" and "superadmin" roles
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_FORBIDDEN);

    }

    /**
     * Add role superadmin to be allowed to see the /admin resource
     */
    @Test(groups = {"integration"}, dependsOnMethods = {"testAdminStatusWithoutAdminRole"})
    public void testAdminStatusWithAdminRole() throws IOException {

        // allow "superadmin" role to see the /admin path (our test user have that role)
        allowSuperAdminRoleAccessToAdminConstraint();

        HttpGet httpGet = new HttpGet(PEPPOL_BASE_URL + "/admin/status");
        HttpResponse httpResponse = httpClient.execute(httpGet);

        //it's forbidden without 'admin' role
        System.out.println(httpResponse.getStatusLine().getReasonPhrase());
        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

    }

    @Test(groups = {"integration"}, dependsOnMethods = {"testAdminStatusWithAdminRole"})
    public void testAdminStatistics() throws Exception {

        //lets add the role so test account has access to sendQueuedMessages
        allowSuperAdminRoleAccessToAdminConstraint();

        RingoStatistics statistics = peppolMessageRepository.getAdminStatistics();

        //fetch the statistics
        HttpGet httpGet = new HttpGet(PEPPOL_BASE_URL + "/admin/statistics");
        HttpResponse httpResponse = httpClient.execute(httpGet);

        assertEquals(httpResponse.getStatusLine().getStatusCode(), HttpStatus.SC_OK);

        //tests that the response contains successfull delivery of all messages that were on the queue
        final String responseAsStr = EntityUtils.toString(httpResponse.getEntity());
        //we are just testing that we get a response really the contents of the xml is tested elsewhere
        assertTrue(responseAsStr.contains("last_sent"));
    }

    @BeforeMethod(groups = {"integration"})
    public void insertSample() throws SQLException {
        databaseHelper.deleteAllMessagesForAccount(testAccount);
        //creates 2 messages
        for (int i = 0; i <= 1; i++) {
            final int message = databaseHelper.createMessage(testAccount.getId().toInteger(), TransferDirection.OUT, ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), null, null);
            messageIds.add(message);
        }
    }

    @AfterMethod(groups = {"integration"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteAllMessagesForAccount(testAccount);
    }

}

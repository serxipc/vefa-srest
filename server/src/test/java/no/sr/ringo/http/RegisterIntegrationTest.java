package no.sr.ringo.http;

import com.google.inject.Inject;
import eu.peppol.identifier.WellKnownParticipant;
import no.sr.ringo.account.Account;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.account.SrAccountNotFoundException;
import no.sr.ringo.account.UserName;
import no.sr.ringo.guice.ServerTestModuleFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.testng.Assert.assertEquals;

/**
 * @author steinar
 *         Date: 13.10.2016
 *         Time: 09.28
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class RegisterIntegrationTest extends AbstractHttpClientServerTest {

    public static final String ACCOUNT_USERNAME = "Difi_test";
    // Skips past the "9908:" prefix
    public static final String DIFI_TEST_ORG_NO = WellKnownParticipant.DIFI_TEST.stringValue().substring(5);

    @Inject
    AccountRepository accountRepository;

    public static final Logger log = LoggerFactory.getLogger(RegisterIntegrationTest.class);

    @Test(groups = {"integration"})
    public void registerNewAccount() throws Exception {

        removeAccountIfExists();
        final HttpResponse httpResponse = doRegistration();

        assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
    }


    @Test(groups = {"integration"})
    public void preventDuplicateRegistration() throws IOException, URISyntaxException {

        removeAccountIfExists();

        // Register a new account
        {
            HttpResponse httpResponse = doRegistration();
            assertEquals(httpResponse.getStatusLine().getStatusCode(), 200);
        }

        // Attempts to register a duplicate
        {
            HttpResponse failureResponse = doRegistration();
            assertEquals(failureResponse.getStatusLine().getStatusCode(), 400);
        }

    }

    private HttpResponse doRegistration() throws URISyntaxException, IOException {
        // The registration data in JSON format
        String registrationData = "{\n" +
                "  \"name\" : \"" + ACCOUNT_USERNAME + "\",\n" +
                "  \"address1\" : \"Business Address 1\",\n" +
                "  \"address2\" : \"Business Address 2\",\n" +
                "  \"zip\" : \"0494\",\n" +
                "  \"city\" : \"Oslo\",\n" +
                "  \"country\" : \"Norway\",\n" +
                "  \"contactPerson\" : \"The Boss\",\n" +
                "  \"email\" : \"boss@business.com\",\n" +
                "  \"phone\" : \"111222333\",\n" +
                "  \"username\" : \""+ ACCOUNT_USERNAME +"\",\n" +
                "  \"password\" : \"topsecret123\",\n" +
                "  \"orgNo\" : \"" + "NO"+DIFI_TEST_ORG_NO + "\",\n" +
                "  \"registerSmp\" : false\n" +
                "}\n";

        // Creates a HttpClient entity
        StringEntity registrationDataEntity = new StringEntity(registrationData, Charset.forName("UTF-8"));

        String registerUrlString = PEPPOL_BASE_REST_URL + "/register";
        HttpPost httpPost = new HttpPost(new URI(registerUrlString));

        httpPost.setEntity(registrationDataEntity);

        // Performs the actual registration
        final HttpResponse httpResponse = httpClient.execute(httpPost);

        log.info(EntityUtils.toString(httpResponse.getEntity()));

        return httpResponse;
    }


    private void removeAccountIfExists() {
        UserName username = new UserName(ACCOUNT_USERNAME);
        boolean accountExists = accountRepository.accountExists(username);
        if (accountExists) {
            Account accountByUsername = null;
            try {
                accountByUsername = accountRepository.findAccountByUsername(username);
                accountRepository.deleteAccount(accountByUsername.getAccountId());
            } catch (SrAccountNotFoundException e) {
                throw new IllegalStateException("Existing account was not found for username " + username);
            }
        }
    }
}
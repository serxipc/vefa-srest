package no.sr.ringo.http;

import com.google.inject.Inject;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.common.ResponseUtils;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 *         IMPORTANT NOTE: We're mocking SMPLookup in tests (SMPLookupProvider) because do we don't want to
 *         make real call everyting we run tests. Therefore isParticipantRegistered method will always return true
 */
@Guice(moduleFactory = TestModuleFactory.class)

public class DirectoryHttpTest extends AbstractHttpClientServerTest {
    static final Logger log = LoggerFactory.getLogger(DirectoryHttpTest.class);

    @Inject
    DatabaseHelper databaseHelper;

    /**
     * Tests that sendregning is registered in the peppol network
     */
    @Test(groups = {"integration"})
    public void testsDirectoryLookup() {
        assertTrue(ringoRestClientImpl.isParticipantRegistered(PeppolParticipantId.valueFor(ObjectMother.getTestParticipantIdForSMPLookup().stringValue())));
    }


    /**
     * Tests that adam is not registered in the peppol network
     */
    @Test(groups = {"integration"})
    public void testsInvalidDirectoryLookupFailure() throws URISyntaxException, IOException {

        String s = PEPPOL_BASE_URL.toString() + "/directory/" + URLEncoder.encode("rubbish", "UTF-8");
        URI directoryLookupUri = new URI(s);
        HttpGet httpGet = new HttpGet(directoryLookupUri);

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(response.getStatusLine().getStatusCode(),400);
        assertEquals(ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET), "Invalid peppol participant id 'rubbish'");
    }

    private PeppolParticipantId peppolIdNotInElma() {
        return PeppolParticipantId.valueFor("987373822");
    }
}

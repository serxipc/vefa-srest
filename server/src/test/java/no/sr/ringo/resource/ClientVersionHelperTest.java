package no.sr.ringo.resource;

import com.google.inject.Inject;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * Tests that the client helper is able to determine if the client is out of date or not
 * User: andy
 * Date: 2/24/12
 * Time: 3:32 PM
 */

// Instructs TestNG to instantiate the ClientVersionModule Google Guice module for us.
// The ClientVersionModule will inject the current client version number inside ClientVersionHelper
@Guice(modules = {ClientVersionModule.class})
public class ClientVersionHelperTest {

    @Inject ClientVersionHelper clientVersionHelper;
    String currentVersion;

    @BeforeMethod
    public void setUp() throws Exception {
        // store the current version number to bring it back after the test
        currentVersion = clientVersionHelper.getCurrentClientVersion();
    }

    @Test
    public void testClientVersionSet() throws Exception {
        System.err.println("--------------> " + "testClientVersionSet();");
        assertEquals("1.1-SNAPSHOT", clientVersionHelper.getCurrentClientVersion());
    }

    @Test
    public void testIsOutOfDateNotRingo() throws Exception {
        //people not using ringo should not get the header so out of date
        //should be false
        assertFalse(clientVersionHelper.isOutOfDate("random"));
        assertFalse(clientVersionHelper.isOutOfDate(""));
        assertFalse(clientVersionHelper.isOutOfDate(null));
    }

    @Test
    public void testIsOutOfDateRingo() throws Exception {
        System.err.println("--------------> " + "testisOutOfDateRingo");

        //client is up to date
        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1-SNAPSHOT)"));

        assertTrue(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.0-SNAPSHOT)"));

        // newer versions are also out of date :)
        assertTrue(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.2-SNAPSHOT)"));

        clientVersionHelper = new ClientVersionHelper("2.0");

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 2.0.1)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 2.0.0)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 2.0.0-SNAPSHOT)"));

        assertTrue(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 2.1)"));

        clientVersionHelper = new ClientVersionHelper("1.1.5");

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.5)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.6)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.7)"));

        clientVersionHelper = new ClientVersionHelper("1.1.2-SNAPSHOT");

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.5)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.6)"));

        assertFalse(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.1.7)"));

        assertTrue(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 1.2.7-SNAPSHOT)"));

        assertTrue(clientVersionHelper.isOutOfDate("SendRegning ringo client (Version: 2.5)"));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        clientVersionHelper = new ClientVersionHelper(currentVersion);
    }

    @Test
    public void extractVersionNumberComponents() {


    }
}

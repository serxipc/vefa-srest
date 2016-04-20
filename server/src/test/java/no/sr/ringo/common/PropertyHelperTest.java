package no.sr.ringo.common;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertNotNull;

/**
 * User: andy
 * Date: 2/24/12
 * Time: 3:17 PM
 */
public class PropertyHelperTest {
    @Test
    public void testFetchProperties() throws Exception {
        final Properties properties = PropertyHelper.fetchProperties("/server.properties");
        assertNotNull(properties.getProperty("client.version"));
    }
}

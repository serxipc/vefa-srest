package no.sr.ringo.config;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertNull;

/**
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 13.34
 */
public class RingoConfigLoaderTest {
    @Test
    public void testLoadProperties() throws Exception {

        RingoConfigLoader ringoConfigLoader = new RingoConfigLoader();
        Properties properties = ringoConfigLoader.loadProperties();

        assertNull(properties.getProperty(RingoConfigProperty.PAYLOAD_BASE_PATH));
    }

}
package no.sr.ringo.config;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author steinar
 *         Date: 28.01.2017
 *         Time: 13.49
 */
public class RingoConfigPropertyTest {
    @Test
    public void testGetNames() throws Exception {

        List<String> propertyNames = RingoConfigProperty.getPropertyNames();
        assertNotNull(propertyNames);
        assertTrue(propertyNames.size() > 0);
    }

}
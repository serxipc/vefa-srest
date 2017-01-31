package no.difi.ringo.test_util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Unit test for simple App.
 */
public class TypeSafeConfigTest
{
    @Test
    public void testLoad() throws Exception {

        System.setProperty("foo", "foo from system props");
        Config config = ConfigFactory.load();
        String payloadBasedir = config.getString("typesafe-test.ringo.payload.basedir");
        assertNotNull(payloadBasedir);
        System.out.println(payloadBasedir);

        assertTrue(config.hasPath("typesafe-test"));
        assertTrue(config.hasPath("typesafe-test.ringo"));
        assertTrue(config.hasPath("typesafe-test.ringo.payload"));
        assertFalse(config.hasPath("typesafe-test.ringo.payl"));

        Object object = config.getAnyRef("typesafe-test.ringo.payload");
        System.out.println(object.getClass().getName());

        String string = config.getString("test-lib.johanne.mina");
        assertEquals(string, "14");

        System.out.println(config.getString("foo"));

        Set<Map.Entry<String, ConfigValue>> entries = config.entrySet();
        for (Map.Entry<String, ConfigValue> entry : entries) {

            System.out.printf("%-32s = %-32s\n", entry.getKey(), entry.getValue().render());
        }

    }
}

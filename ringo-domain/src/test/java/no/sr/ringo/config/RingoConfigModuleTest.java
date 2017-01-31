package no.sr.ringo.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

/**
 * @author steinar
 *         Date: 31.01.2017
 *         Time: 10.34
 */
public class RingoConfigModuleTest {
    
    @Test
    public void testConfigure() throws Exception {

        String s = System.setProperty("buster", "dog");
        Injector injector = Guice.createInjector(new RingoConfigModule());
        Config config = injector.getInstance(Config.class);
        assertNotNull(config);

        assertTrue(config.hasPath("buster"));
        assertEquals(config.getString("buster"), "dog");

        assertTrue(config.hasPath("ringo.payload.basedir"));

        assertTrue(System.getProperty("java.io.tmpdir").endsWith("/"));

        String payloadDir = config.getString("ringo.payload.basedir");

        String property = System.getProperty("java.io.tmpdir");
        Path p = Paths.get(property, "peppol","payload");

        assertEquals(payloadDir, p.toString());


    }

}
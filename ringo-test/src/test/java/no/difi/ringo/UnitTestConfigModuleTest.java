package no.difi.ringo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import no.sr.ringo.config.RingoConfigProperty;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 09.47
 */
public class UnitTestConfigModuleTest {
    @Test
    public void testConfigure() throws Exception {

        Injector injector = Guice.createInjector(new UnitTestConfigModule());
        Path path = injector.getInstance(Key.get(Path.class, Names.named(RingoConfigProperty.PAYLOAD_BASE_PATH)));
        assertNotNull(path);
        assertTrue(Files.isDirectory(path));

    }

}
package no.sr.ringo.config;

import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.*;

/**
 * @author steinar
 *         Date: 31.01.2017
 *         Time: 10.09
 */
public class RingoHomeDirectoryTest {

    @Test
    public void testLocateHomeDir() throws Exception {

        String homeDirName = System.getProperty("user.home");

        Path relativePath = Paths.get(homeDirName, RingoHomeDirectory.RELATIVE_DIR_NAME);
        if (Files.exists(relativePath)) {
            System.setProperty(RingoHomeDirectory.RINGO_HOME_PROPERTY_NAME, relativePath.toString());
            Path path = RingoHomeDirectory.locateRingoHomeDir();
            assertEquals(path, relativePath, "user.home:" + homeDirName + ", ringoHome: "+path);
        }

        if (!Files.exists(relativePath) && System.getProperty(RingoHomeDirectory.RINGO_HOME_PROPERTY_NAME) == null
                && System.getenv(RingoHomeDirectory.RINGO_HOME_ENV_NAME) == null) {
            try {
                RingoHomeDirectory.locateRingoHomeDir();
                fail("If RINGO_HOME directory does not exist, this test should have failed");
            } catch (IllegalStateException e) {
                // expected behaviour
            }
        }
    }

    @Test
    public void testLocateHomeDirRelativeToUserHome() throws Exception {

        Path path = RingoHomeDirectory.locateRingoDirRelativeToUserHome();
        if (!Files.exists(Paths.get(System.getProperty("user.home"), RingoHomeDirectory.RELATIVE_DIR_NAME))) {
            assertNull(path);
        } else
            assertNotNull(path);
    }

    @Test
    public void testLocateHomeDirFromEnvironmentVariable() throws Exception {
        String s = System.getenv(RingoHomeDirectory.RINGO_HOME_ENV_NAME);
        Path path = RingoHomeDirectory.locateRingoDirFromEnvironmentVariable();
        if (s == null) {
            assertNull(path);
        }
        if (s != null) {
            assertNotNull(path);
        }
    }

    @Test
    public void testLocateHomeDirFromJavaSystemProperty() throws Exception {
        Path tempDirectory = Files.createTempDirectory("TEST");
        System.setProperty(RingoHomeDirectory.RINGO_HOME_PROPERTY_NAME, tempDirectory.toString());

        Path path = RingoHomeDirectory.locateRingoDirFromJavaSystemProperty();
        assertEquals(path, tempDirectory);
        System.setProperty(RingoHomeDirectory.RINGO_HOME_PROPERTY_NAME, "");

    }

}
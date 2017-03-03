package no.sr.ringo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author steinar
 *         Date: 31.01.2017
 *         Time: 09.44
 */
public class RingoHomeDirectory {

    public static final Logger log = LoggerFactory.getLogger(RingoHomeDirectory.class);
    public static final String RINGO_HOME_PROPERTY_NAME = RingoConfigProperty.HOME_DIR_PATH;
    public static final String RINGO_HOME_ENV_NAME = "RINGO_HOME";
    public static final String RELATIVE_DIR_NAME = ".ringo";

    public static Path locateRingoHomeDir() {

        log.info("Attempting to locate home dir ....");

        Path homeDir = locateRingoDirFromJavaSystemProperty();
        if (homeDir == null) homeDir = locateRingoDirFromEnvironmentVariable();
        if (homeDir == null) homeDir = locateRingoDirRelativeToUserHome();

        validate(homeDir);

        return homeDir;

    }

    private static void validate(Path homeDir) {
        if (homeDir == null) {
            throw new IllegalStateException("No " + RINGO_HOME_ENV_NAME + " directory could be determined. Ringo will probably fail");
        }
        if (!Files.exists(homeDir)) {
            throw new IllegalStateException(homeDir + " does not exist");
        } else if (!Files.isDirectory(homeDir)) {
            throw new IllegalStateException(homeDir + " is not a directory");
        } else if (!Files.isReadable(homeDir)) {
            throw new IllegalStateException(homeDir + " exists and is a directory, but there is no read access");
        }
    }

    protected static Path locateRingoDirRelativeToUserHome() {

        String userHome = System.getProperty("user.home");

        Path userHomePath = Paths.get(userHome);
        if (!Files.isDirectory(userHomePath)) {
            return null;
        }
        Path ringoHomePath = userHomePath.resolve(RELATIVE_DIR_NAME);
        if (!Files.isDirectory(ringoHomePath)) {
            return null;
        }
        
        log.info("Using " + ringoHomePath + " for RINGO_HOME");
        return ringoHomePath;
    }

    protected static Path locateRingoDirFromEnvironmentVariable() {
        String ringoHome = System.getenv(RINGO_HOME_ENV_NAME);
        if (ringoHome != null && ringoHome.trim().length() > 0) {
            log.info("Using value of environment variable " + RINGO_HOME_ENV_NAME + " for RINGO_HOME : " + ringoHome);
            return Paths.get(ringoHome);
        }
        return null;
    }

    protected static Path locateRingoDirFromJavaSystemProperty() {
        String ringoHome = System.getProperty(RINGO_HOME_PROPERTY_NAME);
        if (ringoHome != null && ringoHome.trim().length() > 0) {
            log.info("Using RINGO_HOME specified with Java System property -D" + RINGO_HOME_PROPERTY_NAME + "=" + ringoHome);
            return Paths.get(ringoHome);
        } else
            return null;
    }
}

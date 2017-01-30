package no.sr.ringo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 13.12
 */
class RingoConfigLoader {

    public static final Logger log = LoggerFactory.getLogger(RingoConfigLoader.class);

    String propertiesFileName = "ringo.properties";

    Properties loadProperties() {

        // Computes the path to the default config properties
        Path configPropertiesPath = getPathInHomeDirFor(propertiesFileName);
        Properties applicationProperties = new Properties();

        // Loads the application properties only if file exists
        if (Files.exists(configPropertiesPath)
                && Files.isRegularFile(configPropertiesPath)
                && Files.isReadable(configPropertiesPath)) {

            log.info("Loading configuration parameters from " + configPropertiesPath);

            try (InputStream inputStream = Files.newInputStream(configPropertiesPath)) {
                applicationProperties.load(inputStream);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load config params from " + configPropertiesPath);
            }
        } else {
            log.info("Default configuration file " + configPropertiesPath + " not found, ignored.");
        }

        // Overrides with System properties
        Properties effectiveProperties = createEffectiveProperties(applicationProperties);

        return effectiveProperties;
    }


    Properties createEffectiveProperties(Properties applicationProperties) {
        Properties effectiveProperties = new Properties(applicationProperties);

        for (String propName : RingoConfigProperty.getPropertyNames()) {
            String value = System.getProperty(propName);
            if (value != null)
                effectiveProperties.setProperty(propName, value);
        }
        return effectiveProperties;
    }

    /**
     * Computes the {@link Path} of the supplied filename in the current users home directory.
     *
     * @param fileName name of file to locate in home directory.
     * @return
     */
    Path getPathInHomeDirFor(String fileName) {
        String homeDirName = System.getProperty("user.home");
        return Paths.get(homeDirName, fileName);
    }


}

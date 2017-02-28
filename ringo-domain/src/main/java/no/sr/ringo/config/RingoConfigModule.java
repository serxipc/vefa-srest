package no.sr.ringo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads the configuration and provides bindings for certain important stuff.
 *
 * @see <a href="https://github.com/typesafehub/config">https://github.com/typesafehub/config#how-to-handle-defaults</a>
 * 
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 10.13
 */
public class RingoConfigModule extends AbstractModule{


    private static final Logger LOGGER = LoggerFactory.getLogger(RingoConfigModule.class);

    @Override
    protected void configure() {
        // No action for now
    }


    /**
     * Provides the Path of the RINGO_HOME directory
     * @return
     */
    @Provides
    @Singleton
    @Named("ringo.home")
    protected Path provideRingoHomeDir() {
        return RingoHomeDirectory.locateRingoHomeDir();
    }


    /**
     * Laods and merges configurations params from:
     * <ol>
     *     <li>System.getProperties() (including properties set with -D)</li>
     *     <li>ringo.conf</li>
     *     <li>reference.conf</li>
     *     <li>defaults.conf</li>
     * </ol>
     * @param ringoConf
     * @return complete Configuration for Ringo
     */
    @Provides
    @Singleton
    protected Config loadConfiguration(@Named("file") Config ringoConf) {
        Config referenceConfig = ConfigFactory.defaultReference();

        ConfigFactory.invalidateCaches();   // Important for unit tests etc.
        return ConfigFactory.systemProperties()
                .withFallback(ringoConf)
                .withFallback(referenceConfig)
                .withFallback(referenceConfig.getConfig("defaults"));
    }

    /**
     * Intermediate Config used to of the final, sandwiched config.
     * Loads the default "ringo.conf" file from the RINGO_HOME directory.
     *
     * @param homePath the RINGO_HOME path
     * @return
     */
    @Provides
    @Singleton
    @Named("file")
    protected Config loadConfigurationFile(@Named("ringo.home") Path homePath) {
        Path configPath = homePath.resolve("ringo.conf");
        LOGGER.info("Configuration file: {}", configPath);

        return ConfigFactory.parseFile(configPath.toFile());
    }


    @Provides
    @Singleton
    @Named(RingoConfigProperty.PAYLOAD_BASE_PATH)
    protected Path provideBaseDirPath(Config config) {
        return Paths.get(config.getString(RingoConfigProperty.PAYLOAD_BASE_PATH));
    }
}

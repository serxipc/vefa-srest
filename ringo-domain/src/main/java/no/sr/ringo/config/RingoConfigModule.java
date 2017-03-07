package no.sr.ringo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static no.sr.ringo.config.RingoConfigProperty.HOME_DIR_PATH;

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
    private Config effectiveConfig;
    private final Path ringoHomeDir;


    public RingoConfigModule() {
        ringoHomeDir = RingoHomeDirectory.locateRingoHomeDir();
    }

    @Override
    protected void configure() {
        effectiveConfig = loadConfig();
        bindConfigNamesAndValues();
    }

    private void bindConfigNamesAndValues() {

        List<String> settingsNotBound = new ArrayList<>();
        for (String propName : RingoConfigProperty.getPropertyNames()) {
            if (effectiveConfig.hasPath(propName)) {
                String value = effectiveConfig.getString(propName);
                LOGGER.debug("Binding {} to value \"{}\"", propName, value);
                bind(String.class).annotatedWith(Names.named(propName)).toInstance(value);
            } else
                settingsNotBound.add(propName);
        }

        for (String propName : settingsNotBound) {
            LOGGER.warn("No configuration value found for setting {}", propName);
        }
    }

    private Config loadConfig() {
        // Loads the external configuration
        final Config externalConfigFile = loadExternalConfigurationFile(ringoHomeDir);
        // Merges external config with values from System properties.
        final Config mergedConfig = mergeConfiguration(externalConfigFile);

        return mergedConfig;
    }

    static protected Config mergeConfiguration(Config externalConfig) {

        ConfigFactory.invalidateCaches();
        final Config defaultReferenceConfig = ConfigFactory.load("ringo-reference.conf");
        //Config defaultReferenceConfig = ConfigFactory.defaultReference();   // Loads the reference.conf instances from class path

        // Loads and merges configuration in priority order
        final Config systemProperties = ConfigFactory.systemProperties();
        Config effectiveMergedconfig = systemProperties     // System properties overrides everything
                .withFallback(externalConfig)                               // The external configuration file
                .withFallback(defaultReferenceConfig)                       // The ringo-reference.conf files on class path
                .withFallback((defaultReferenceConfig.getConfig("defaults")));   // Finally, set default fall back values

        final Config resolved = effectiveMergedconfig.resolve();    // Resolves and substitutes any variables

        return resolved;
    }


    @Provides
    protected Config providesEffectiveConfig() {
        return effectiveConfig;
    }

    /**
     * Provides the Path of the RINGO_HOME directory
     * @return
     */
    @Provides
    @Singleton
    @Named(HOME_DIR_PATH)
    protected Path provideRingoHomeDir() {
        return ringoHomeDir;
    }



    /**
     * Intermediate Config used to load the final, sandwiched config.
     * Loads the default "ringo.conf" file from the RINGO_HOME directory.
     *
     * @param homePath the RINGO_HOME path
     * @return
     */
    static protected Config loadExternalConfigurationFile(Path homePath) {
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

package no.sr.ringo.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 10.13
 */
public class RingoConfigModule extends AbstractModule{

    public RingoConfigModule() {

    }

    @Override
    protected void configure() {

        // TODO: load global configuration file
        bind(Path.class).annotatedWith(Names.named(RingoConfigProperty.PAYLOAD_BASE_PATH))
                .toInstance(Paths.get("/var/peppol"));
    }
}

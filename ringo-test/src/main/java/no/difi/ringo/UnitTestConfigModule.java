package no.difi.ringo;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import no.sr.ringo.config.RingoConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author steinar
 *         Date: 29.01.2017
 *         Time: 09.36
 */
public class UnitTestConfigModule extends AbstractModule {

    @Override
    protected void configure() {

        String tmpDir = System.getProperty("java.io.tmpdir");
        Path baseDirPath = Paths.get(tmpDir, "/peppol");
        try {
            if (!Files.exists(baseDirPath) && !Files.isDirectory(baseDirPath)) {
                Path path = Files.createDirectories(baseDirPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create directory " + baseDirPath + "; " + e.getMessage(),e);
        }

        bind(Path.class).annotatedWith(Names.named(RingoConfigProperty.PAYLOAD_BASE_PATH))
                    .toInstance(baseDirPath);
    }
}

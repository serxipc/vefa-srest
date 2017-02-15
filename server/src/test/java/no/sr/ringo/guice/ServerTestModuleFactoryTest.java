package no.sr.ringo.guice;

import com.google.inject.Inject;
import no.sr.ringo.config.RingoConfigProperty;
import no.sr.ringo.persistence.file.ArtifactPathComputer;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Named;
import java.nio.file.Path;

import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 30.01.2017
 *         Time: 12.16
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class ServerTestModuleFactoryTest {

    @Inject
    @Named(RingoConfigProperty.PAYLOAD_BASE_PATH)
    Path path;

    @Inject
    ArtifactPathComputer artifactPathComputer;

    @Test
    public void testBindingOfPayloadBaseDir() throws Exception {
        assertNotNull(path, "Seems the binding of Path annotated with " + RingoConfigProperty.PAYLOAD_BASE_PATH + " did not work");

        assertNotNull(artifactPathComputer);
    }
}
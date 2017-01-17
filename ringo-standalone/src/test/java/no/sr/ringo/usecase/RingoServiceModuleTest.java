package no.sr.ringo.usecase;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import eu.peppol.persistence.RepositoryConfiguration;
import eu.peppol.persistence.guice.RepositoryModule;
import no.sr.ringo.guice.RingoDataSourceGuiceModule;
import no.sr.ringo.guice.RingoServiceModule;
import no.sr.ringo.oxalis.DummySender;
import no.sr.ringo.oxalis.OxalisDocumentSender;
import no.sr.ringo.oxalis.PeppolDocumentSender;
import org.testng.annotations.Test;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * User: Adam
 * Date: 5/29/13
 * Time: 12:59 PM
 */
public class RingoServiceModuleTest {

    @Test
    public void testDummySenderInjected() {

        Injector injector = Guice.createInjector(

                new RingoDataSourceGuiceModule("host", "user", "pass", "dbName"),
                new RingoServiceModule(false),   // Not production
                new RepositoryModule(),
                new DummyConfigModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof DummySender);
        assertFalse(documentSender instanceof OxalisDocumentSender);

    }


    @Test
    public void testOxalisSenderInjected() {

        Injector injector = Guice.createInjector(
                new RingoDataSourceGuiceModule("host", "user", "pass", "dbName"),
                new RingoServiceModule(true),    // Production
                new RepositoryModule(),
                new DummyConfigModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof OxalisDocumentSender);
        assertFalse(documentSender instanceof DummySender);

    }


    private static class DummyConfigModule extends AbstractModule {

        @Provides
        private RepositoryConfiguration getRepositoryConfiguration() {
            return new RepositoryConfiguration() {
                @Override
                public Path getBasePath() {
                    return Paths.get("/var/tmp");
                }

                @Override
                public URI getJdbcConnectionUri() {
                    return null;
                }

                @Override
                public String getJdbcDriverClassPath() {
                    return null;
                }

                @Override
                public String getJdbcDriverClassName() {
                    return null;
                }

                @Override
                public String getJdbcUsername() {
                    return null;
                }

                @Override
                public String getJdbcPassword() {
                    return null;
                }

                @Override
                public String getValidationQuery() {
                    return null;
                }
            };
        }

        @Override
        protected void configure() {

        }
    }

}

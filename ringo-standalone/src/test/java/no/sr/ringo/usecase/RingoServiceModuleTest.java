package no.sr.ringo.usecase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.peppol.persistence.RingoRepositoryModule;
import no.difi.ringo.UnitTestConfigModule;
import no.sr.ringo.guice.RingoDataSourceGuiceModule;
import no.sr.ringo.guice.RingoServiceModule;
import no.sr.ringo.oxalis.OxalisDocumentSender;
import no.sr.ringo.oxalis.PeppolDocumentSender;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * User: Adam
 * Date: 5/29/13
 * Time: 12:59 PM
 */
public class RingoServiceModuleTest {



    @Test
    public void testOxalisSenderInjected() {

        Injector injector = Guice.createInjector(
                new UnitTestConfigModule(),
                new RingoDataSourceGuiceModule("host", "user", "pass", "dbName"),
                new RingoServiceModule(),    // Production
                new RingoRepositoryModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof OxalisDocumentSender);

    }
}

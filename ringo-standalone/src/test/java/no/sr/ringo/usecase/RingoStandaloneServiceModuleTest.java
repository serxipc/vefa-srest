package no.sr.ringo.usecase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.peppol.persistence.jdbc.RingoDataSourceModule;
import eu.peppol.persistence.jdbc.RingoRepositoryModule;
import no.difi.ringo.UnitTestConfigModule;
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
public class RingoStandaloneServiceModuleTest {

    @Test
    public void testOxalisSenderInjected() {

        Injector injector = Guice.createInjector(
                new UnitTestConfigModule(),
                
                new RingoDataSourceModule(),
                new RingoServiceModule(),    // Production
                new RingoRepositoryModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof OxalisDocumentSender);

    }
}

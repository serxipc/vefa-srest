package no.sr.ringo.usecase;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.peppol.persistence.guice.RepositoryModule;
import no.sr.ringo.guice.AopJdbcTxManagerModule;
import no.sr.ringo.guice.RingoDataSourceGuiceModule;
import no.sr.ringo.guice.RingoServiceModule;
import no.sr.ringo.oxalis.DummySender;
import no.sr.ringo.oxalis.OxalisDocumentSender;
import no.sr.ringo.oxalis.PeppolDocumentSender;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 *
 * User: Adam
 * Date: 5/29/13
 * Time: 12:59 PM
 */
public class RingoServiceModuleTest {

    @Test
    public void testDummySenderInjected() {

        Injector injector = Guice.createInjector(
                new AopJdbcTxManagerModule(),
                new RingoDataSourceGuiceModule("host", "user", "pass", "dbName"),
                new RingoServiceModule(false),   // Not production
                new RepositoryModule()
        );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof DummySender);
        assertFalse(documentSender instanceof OxalisDocumentSender);

    }

    @Test
    public void testOxalisSenderInjected() {

        Injector injector = Guice.createInjector(
                new AopJdbcTxManagerModule(),
                new RingoDataSourceGuiceModule("host", "user", "pass", "dbName"),
                new RingoServiceModule(true),    // Production
                new RepositoryModule()
            );

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        PeppolDocumentSender documentSender = useCase.getDocumentSender();

        assertTrue(documentSender instanceof OxalisDocumentSender);
        assertFalse(documentSender instanceof DummySender);

    }

}

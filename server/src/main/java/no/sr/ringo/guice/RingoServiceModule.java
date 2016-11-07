package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import eu.peppol.persistence.api.account.AccountRepository;
import eu.peppol.persistence.jdbc.AccountRepositoryImpl;
import no.sr.ringo.email.*;
import eu.peppol.smp.*;
import no.sr.ringo.common.IsProductionServer;
import no.sr.ringo.document.*;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.message.PeppolMessageRepositoryImpl;
import no.sr.ringo.queue.QueueRepository;
import no.sr.ringo.queue.QueueRepositoryImpl;
import no.sr.ringo.report.ReportRepository;
import no.sr.ringo.report.ReportRepositoryImpl;

/**
 * Bindings for our service objects as used in RingoServer.
 *
 * @author andy
 * @author thore
 */
public class RingoServiceModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(IsProductionServer.class).toProvider(IsProductionServerProvider.class);
        bindSmpLookup();
        bindRepositories();
        bindPeppolDocumentFactories();
        bindEmailService();
    }

    private void bindSmpLookup() {
        bind(SmpContentRetriever.class).to(SmpContentRetrieverImpl.class);
        bind(BusDoxProtocolSelectionStrategy.class).to(DefaultBusDoxProtocolSelectionStrategyImpl.class);
        bind(SmpLookupManager.class).to(SmpLookupManagerImpl.class);
    }

    private void bindRepositories() {
        // The main workhorse
        bind(PeppolMessageRepository.class).to(PeppolMessageRepositoryImpl.class).in(Singleton.class);
        bind(QueueRepository.class).to(QueueRepositoryImpl.class).in(Singleton.class);
        bind(ReportRepository.class).to(ReportRepositoryImpl.class).in(Singleton.class);
        bind(DocumentRepository.class).to(DocumentRepositoryImpl.class).in(RequestScoped.class);
    }

    private void bindPeppolDocumentFactories() {
        bind(PeppolDocumentFactory.class).to(PeppolDocumentFactoryImpl.class).in(Singleton.class);
        bind(PeppolDocumentDecoratorFactory.class).to(PeppolDocumentDecoratorFactoryImpl.class).in(Singleton.class);
    }

    private void bindEmailService() {
        bind(EmailService.class).to(NoEmailServiceImpl.class).in(Singleton.class);
    }

}
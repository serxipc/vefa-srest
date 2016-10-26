package no.sr.ringo.guice;

import com.google.inject.Provides;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import eu.peppol.smp.*;
import eu.peppol.util.OperationalMode;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.FetchMessagesUseCase;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;
import no.sr.ringo.peppol.DummySender;
import no.sr.ringo.peppol.PeppolDocumentSender;
import no.sr.ringo.resource.*;
import no.sr.ringo.smp.RingoSmpLookup;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates the REST bindings for the Server at the given WebContexts.
 *
 * This module is only used in Production
 *
 * @author andy
 * @author thore
 */
public class RingoRestModule extends JerseyServletModule {

    private final String versionNumber;
    private final boolean enableTracingDebug;
    private final Map<String, String> initalisationParams = new HashMap<String, String>();

    public RingoRestModule(String versionNumber, boolean enableTracingDebug) {
        this.versionNumber = versionNumber;
        this.enableTracingDebug = enableTracingDebug;
    }

    @Override()
    protected void configureServlets() {

        configureLogging();

        bindClientVersion();
        bindJerseyResources();
        bindUseCases();
        bindSmpLookup();
        bindSmpDependencies();
        bindDocumentSendingDependencies();
        bindExceptionHandlers();

        // Serves everything under inbox, outbox, messages, events etc using (JAX-RS)
        serveRegex("(^\\/(?:register|directory|inbox|outbox|messages|admin|statistics|notify)(?!.*\\.ico.*).*$)").with(GuiceContainer.class, initalisationParams);

    }

    private void bindClientVersion() {
        bindConstant().annotatedWith(ClientVersion.class).to(versionNumber);
        bind(ClientVersionHelper.class);
        bind(ClientVersionNumberResponseFilter.class);
    }

    private void bindExceptionHandlers() {
        bind(UnexpectedExceptionMapper.class);
        bind(MessageNotFoundExceptionMapper.class);
    }

    private void configureLogging() {
        //sets up logging of requests and responses
        if (enableTracingDebug) {
            initalisationParams.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, "com.sun.jersey.api.container.filter.LoggingFilter");
            initalisationParams.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, "com.sun.jersey.api.container.filter.LoggingFilter");
            initalisationParams.put(ResourceConfig.FEATURE_TRACE, "true");
        }
    }

    private void bindJerseyResources() {
        bind(OutboxResource.class);
        bind(InboxResource.class);
        bind(MessagesResource.class);
        bind(DirectoryResource.class);
        bind(AdminResource.class);
        bind(RegisterResource.class);
        bind(StatisticsResource.class);
        bind(NotificationResource.class);
        // remember to add new paths to regexp in configureServlets()
    }

    private void bindDocumentSendingDependencies() {
        bind(PeppolDocumentSender.class).to(DummySender.class);
    }

    private void bindUseCases() {
        bind(FetchMessagesUseCase.class).in(RequestScoped.class);
        bind(ReceiveMessageFromClientUseCase.class).in(RequestScoped.class);
        bind(FetchDocumentUseCase.class).in(RequestScoped.class);
    }

    private void bindSmpLookup() {
        bind(SmpContentRetriever.class).to(SmpContentRetrieverImpl.class);
        bind(BusDoxProtocolSelectionStrategy.class).to(DefaultBusDoxProtocolSelectionStrategyImpl.class);
        bind(SmpLookupManager.class).to(SmpLookupManagerImpl.class);
    }

    private void bindSmpDependencies() {
        bind(RingoSmpLookup.class).toProvider(SmpLookupProvider.class);
    }

    @Provides
    OperationalMode getOperationalMode() {
        return OperationalMode.PRODUCTION;
    }

    @Provides
    SmlHost getSmlHost() {
        return SmlHost.PRODUCTION_SML;
    }


}

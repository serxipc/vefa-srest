package no.sr.ringo.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import no.sr.ringo.document.FetchDocumentResultVisitor;
import no.sr.ringo.document.FetchDocumentResultVisitorImpl;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.FetchMessagesUseCase;
import no.sr.ringo.peppol.DummySender;
import no.sr.ringo.peppol.PeppolDocumentSender;
import no.sr.ringo.resource.*;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates the REST bindings for the Server at the given WebContexts.
 * <p>
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
        bindDocumentSendingDependencies();
        bindExceptionHandlers();

        // Serves everything under inbox, outbox, messages, events etc using (JAX-RS)
        serveRegex("(^\\/(?:register|inbox|outbox|messages|admin|statistics|notify)(?!.*\\.ico.*).*$)").with(GuiceContainer.class, initalisationParams);

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
//        bind(PayloadUriRewriter.class).to(DefaultPayloadUriRewriter.class);     // Rewrites the payload URI
        bind(UriLocationTool.class).to(UriLocationToolImpl.class);

        bind(new TypeLiteral<FetchDocumentResultVisitor<Response>>(){}).to(FetchDocumentResultVisitorImpl.class);
        
        bind(PayloadResponseHelper.class);

        bind(OutboxResource.class);
        bind(InboxResource.class);
        bind(MessagesResource.class);
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
}

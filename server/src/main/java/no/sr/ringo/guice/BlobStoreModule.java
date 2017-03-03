package no.sr.ringo.guice;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import no.sr.ringo.config.RingoConfigProperty;
import no.sr.ringo.message.DefaultPayloadUriRewriter;
import no.sr.ringo.message.PayloadUriRewriter;
import no.sr.ringo.plugin.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This modules binds implementations of services pertaining to storage of payload, evidence etc. in an external
 * blob storage service like for instance Microsoft Azure og Amazone S3
 *
 *
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 18.45
 */
public class BlobStoreModule extends AbstractModule {

    public static final Logger LOGGER = LoggerFactory.getLogger(BlobStoreModule.class);

    @Override
    protected void configure() {

        // Default implementation of the PayloadUriRewriter, which may be overridden by a plugin
        bind(PayloadUriRewriter.class).annotatedWith(Names.named("default")).to(DefaultPayloadUriRewriter.class);
    }

    @Provides
    @Singleton
    @Named("plugin")
    protected PayloadUriRewriter providePayloadUriRewriterPlugin(PluginFactory pluginFactory) {
        return pluginFactory.newInstance(PayloadUriRewriter.class);
    }
    
    /**
     * Provides the concrete instance of the PayloadUriRewriter to be used, uses the configured plugin or the default.
     *
     * @param injector   current injector
     * @param config the config object
     * @return
     */
    @Provides
    @Singleton
    protected PayloadUriRewriter provideUriRewriter(Injector injector, Config config) {
        final String implementationToUse = config.getString(RingoConfigProperty.BLOB_SERVICE_URI_REWRITER);
        LOGGER.debug("Loading PayloadUriRewriter '{}'", implementationToUse);
        return injector.getInstance(Key.get(PayloadUriRewriter.class, Names.named(implementationToUse)));
    }

}

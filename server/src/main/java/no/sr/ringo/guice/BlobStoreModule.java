package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import no.sr.ringo.message.DefaultPayloadUriRewriter;
import no.sr.ringo.message.PayloadUriRewriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 18.45
 */
public class BlobStoreModule extends AbstractModule {

    public static final Logger LOGGER = LoggerFactory.getLogger(BlobStoreModule.class);
    public static final String RINGO_BLOB_CLASS_PATH = "ringo.blob.class.path";

    @Override
    protected void configure() {
        bind(PayloadUriRewriter.class).annotatedWith(Names.named("default")).to(DefaultPayloadUriRewriter.class);
    }

    @Provides
    PayloadUriRewriter provideUriRewriter(Injector injector, Config config) {
        if (config.hasPath("ringo.blob")) {
            String classPath = config.getString(RINGO_BLOB_CLASS_PATH);

            final URL url;
            try {
                url = new URL(classPath);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Unable to create URL from " + classPath);
            }

            ServiceLoader<PayloadUriRewriter> loader = ServiceLoader.load(PayloadUriRewriter.class, new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader()));

            final List<PayloadUriRewriter> payloadUriRewriters = StreamSupport.stream(loader.spliterator(), false).collect(Collectors.toList());
            if (payloadUriRewriters.size() > 1) {
                LOGGER.debug("Found {} implementations of {}, using the first one", payloadUriRewriters.size(),PayloadUriRewriter.class.getName());
            }
            if (payloadUriRewriters.isEmpty()) {
                LOGGER.error("No implementation of {} found on class path", PayloadUriRewriter.class.getName());
                throw new IllegalStateException("Unable to load " + PayloadUriRewriter.class.getName() + " from class path " + classPath);
            }
            return payloadUriRewriters.get(0);
        } else
            return injector.getInstance(Key.get(PayloadUriRewriter.class, Names.named("default")));
    }

}

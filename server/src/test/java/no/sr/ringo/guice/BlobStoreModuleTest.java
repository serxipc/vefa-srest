package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.sr.ringo.message.PayloadUriRewriter;
import org.testng.annotations.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 19.42
 */
public class BlobStoreModuleTest {


    @Test
    public void testProvideUriRewriter() throws Exception {

        // Causes our plugin to be loaded
        final URI uri1 = URI.create("file:///Users/steinar/src/spiralis/azureblob/target/azure-blob-1.0-SNAPSHOT.jar");

        if (Files.exists(Paths.get(uri1))) {

            // Signals to the BlobStoreModule that there is an implementation to load. All system
            // properties are picked up by the type safe Config.
            System.setProperty(BlobStoreModule.RINGO_BLOB_CLASS_PATH, uri1.toString());

            final Injector injector = Guice.createInjector(
                    new AbstractModule() {
                        @Override
                        protected void configure() {

                        }

                        // Picks up configuration properties.
                        @Provides
                        Config providesConfig() {
                            ConfigFactory.invalidateCaches();
                            final Config config = ConfigFactory.systemProperties().withFallback(ConfigFactory.defaultReference());
                            return config;

                        }
                    },

                    new BlobStoreModule()
            );

            final PayloadUriRewriter payloadUriRewriter = injector.getInstance(PayloadUriRewriter.class);
            System.out.println(payloadUriRewriter.getClass().getName());

            final URI uri = URI.create("http://hmaptestdata01.blob.core.windows.net/invoice-out/sample-invoice-doc.xml");
            final URI rewriten = payloadUriRewriter.rewrite(uri, null);
            System.out.println(rewriten);
        } else
            System.out.println("Experimental test not executed");
    }


}
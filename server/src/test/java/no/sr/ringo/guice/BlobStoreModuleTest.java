package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import no.sr.ringo.message.PayloadUriRewriter;
import org.testng.annotations.Test;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 19.42
 */
public class BlobStoreModuleTest {


    @Test(enabled = false)
    public void testProvideUriRewriter() throws Exception {


        System.setProperty(BlobStoreModule.RINGO_BLOB_CLASS_PATH, "file:///Users/steinar/src/spiralis/azureblob/target/classes/");
        
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {

                    }

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

        payloadUriRewriter.rewrite(null, null);
    }

    

}
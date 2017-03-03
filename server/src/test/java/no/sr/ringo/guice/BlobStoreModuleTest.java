package no.sr.ringo.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import no.sr.ringo.config.RingoConfigModule;
import no.sr.ringo.config.RingoConfigProperty;
import no.sr.ringo.message.PayloadUriRewriter;
import no.sr.ringo.plugin.PluginModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 19.42
 */
public class BlobStoreModuleTest {

    @BeforeMethod
    public void setUp() throws Exception {
        // Ensures that we do not load our own installation of ringo.conf
        System.setProperty(RingoConfigProperty.HOME_DIR_PATH, System.getProperty("java.io.tmpdir"));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        System.getProperties().remove(RingoConfigProperty.HOME_DIR_PATH);
        assertNull(System.getProperty(RingoConfigProperty.HOME_DIR_PATH));

        System.clearProperty(RingoConfigProperty.BLOB_SERVICE_URI_REWRITER);
    }

    @Test
    public void testProvidesDefaultUriRewriter() throws Exception {

        System.setProperty(RingoConfigProperty.BLOB_SERVICE_URI_REWRITER, "default");
        final Injector injector = Guice.createInjector(
                new RingoConfigModule(),
                new PluginModule(),
                new BlobStoreModule()
        );

        final PayloadUriRewriter payloadUriRewriter = injector.getInstance(PayloadUriRewriter.class);

    }

    /**
     * Verifies that we can specify "plugin" has the value for BLOB_SERVICE_URI_REWRITER and have our fake
     * URI rewriter loaded.
     *
     * @throws Exception
     */
    @Test
    public void testDummyRewriterIsLoadIfPropertySetToPlugin() throws Exception {

        System.setProperty(RingoConfigProperty.BLOB_SERVICE_URI_REWRITER, "plugin");

        final Injector injector = Guice.createInjector(new RingoConfigModule(), new BlobStoreModule(), new PluginModule());
        try {
            final PayloadUriRewriter payloadUriRewriter = injector.getInstance(PayloadUriRewriter.class);
            fail("Specifying 'plugin' should have failed, as we have no plugin in the test env");
        } catch (Exception e) {
            //
        }

    }
}
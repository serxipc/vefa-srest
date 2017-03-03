package no.sr.ringo.plugin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import no.sr.ringo.config.RingoConfigModule;
import no.sr.ringo.config.RingoConfigProperty;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author steinar
 *         Date: 03.03.2017
 *         Time: 13.34
 */
public class PluginModuleTest {

    @BeforeMethod
    public void setUp() throws Exception {
        System.setProperty(RingoConfigProperty.HOME_DIR_PATH, System.getProperty("java.io.tmpdir"));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        System.clearProperty(RingoConfigProperty.HOME_DIR_PATH);
    }


    @Test
    public void testProvidesDefaultClassLoader() throws Exception {

        final Injector injector = Guice.createInjector(new RingoConfigModule(), new PluginModule());
        ClassLoader loader = injector.getInstance(Key.get(ClassLoader.class, Names.named("plugin")));

        // As we have not specified a plugin.path, we should receive the default class loader.
        assertEquals(loader, Thread.currentThread().getContextClassLoader());
    }
}
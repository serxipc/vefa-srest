package no.difi.ringo.test_util;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author steinar
 *         Date: 28.01.2017
 *         Time: 10.48
 */
public class PropertiesTest {


    public static final String P = "p";

    @Test
    public void testPropertiesWithDefault() throws Exception {

        System.setProperty(P, "system");

        Properties module = new Properties();
        Properties app = new Properties(module);
        Properties system = new Properties(app);

        module.setProperty(P, "module");
        app.setProperty(P, "app");
        system.setProperty(P, System.getProperty("p"));

        String p = system.getProperty(P);

        assertEquals(p, "system");

        Injector injector = Guice.createInjector(new PropLoadModule(system));
        Sample instance = injector.getInstance(Sample.class);

        assertEquals(instance.getValue(), "system");

    }


    static class PropLoadModule extends AbstractModule {

        private final Properties properties;

        public PropLoadModule(Properties properties) {
            this.properties = properties;
        }

        @Override
        protected void configure() {

            Names.bindProperties(binder(), properties);

            bindConstant().annotatedWith(Names.named(x())).to("value");
        }

        public final static String x() { return "x";}
    }

    static class Sample {
        private final String value;

        @Inject
        public Sample(@Named(PropertiesTest.P) String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}


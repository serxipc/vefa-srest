package no.sr.ringo.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.servlet.RequestScoped;
import eu.peppol.persistence.guice.AopJdbcTxManagerModule;
import eu.peppol.persistence.guice.RepositoryModule;
import eu.peppol.util.OxalisProductionConfigurationModule;
import no.sr.ringo.security.CredentialHandler;
import no.sr.ringo.security.SecretKeyCredentialHandler;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.RingoSmpLookupImpl;
import no.sr.ringo.smp.TestModeSmpLookupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import javax.servlet.ServletContext;

/**
 * An IModuleFactory allows us to use modules depending upon the classes that TestNG is about to instantiate.
 *
 * Our factory will also receive a test context, which allows you to look up some values
 * on the environment you were invoked with.
 */
public class TestModuleFactory implements IModuleFactory {

    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        return new TestModule(false);
    }

    /**
     * Module that sets up
     * Repositories, test datasource, fake ElmaRegistration
     * optionally fake SMP lookup.
     */
    private class TestModule extends TestDataSourceModule {

        private final boolean mockSmp;
        public  final Logger log = LoggerFactory.getLogger(TestModule.class);

        public TestModule(boolean mockSmp) {
            this.mockSmp = mockSmp;
        }

        @Override
        protected void configure() {

            //sets up the datasource
            super.configure();

            //we need to fake the request scope and ServletContext when not running in a web container.
            bindScope(RequestScoped.class, new FakeScope(mockSmp));
            bind(ServletContext.class).toInstance(new FakeServletContext());

            bind(CredentialHandler.class).to(SecretKeyCredentialHandler.class);

            // Grabs the current binder being configured in order for us to wire upt the other modules in the same binder,
            // like an "include" statement
            Binder binder = binder();


            binder.install(new OxalisProductionConfigurationModule());

            binder.install(new RepositoryModule());
            //set up the repositories email service etc.
            binder.install(new RingoServiceModule());

            binder.install(new SmpInTestModeModule());

            //sets up either the real smp lookup or the fake one
            if (mockSmp) {
                System.err.println("Binding to TestModeSmpLookupImpl");
                bind(RingoSmpLookup.class).to(TestModeSmpLookupImpl.class);
            } else {
                bind(RingoSmpLookup.class).to(RingoSmpLookupImpl.class);
            }

        }



    }

}

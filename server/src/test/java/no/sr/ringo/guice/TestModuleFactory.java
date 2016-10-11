package no.sr.ringo.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.security.CredentialHandler;
import no.sr.ringo.security.SecretKeyCredentialHandler;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.RingoSmpLookupImpl;
import no.sr.ringo.smp.TestModeSmpLookupImpl;
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
            //set up the repositories and transaction handling
            Binder binder = binder();

            //set up the transaction handler
            new AopJdbcTxManagerModule().configure(binder);

            //set up the repositories email service etc.
            new RingoServiceModule().configure(binder);

            //sets up either the real smp lookup or the fake one
            if (mockSmp) {
                bind(RingoSmpLookup.class).to(TestModeSmpLookupImpl.class);
            } else {
                bind(RingoSmpLookup.class).to(RingoSmpLookupImpl.class);
            }

        }

    }

}

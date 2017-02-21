package no.sr.ringo.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.servlet.RequestScoped;
import no.difi.ringo.UnitTestConfigModule;
import no.sr.ringo.persistence.jdbc.RingoRepositoryModule;
import no.sr.ringo.security.CredentialHandler;
import no.sr.ringo.security.SecretKeyCredentialHandler;
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
public class ServerTestModuleFactory implements IModuleFactory {

    @Override
    public Module createModule(ITestContext context, Class<?> testClass) {
        return new ServerTestModule();
    }

    /**
     * Module that sets up
     * Repositories, test datasource, fake ElmaRegistration
     * optionally fake SMP lookup.
     */
    private class ServerTestModule extends ServerTestDataSourceModule {

        public  final Logger log = LoggerFactory.getLogger(ServerTestModule.class);

        @Override
        protected void configure() {

            //sets up the datasource
            super.configure();


            //we need to fake the request scope and ServletContext when not running in a web container.
            bindScope(RequestScoped.class, new FakeScope());
            bind(ServletContext.class).toInstance(new FakeServletContext());

            bind(CredentialHandler.class).to(SecretKeyCredentialHandler.class);

            // Grabs the current binder being configured in order for us to wire upt the other modules in the same binder,
            // like an "include" statement
            Binder binder = binder();

            binder.install(new UnitTestConfigModule());
            
            binder.install(new RingoRepositoryModule());

            //set up the repositories email service etc.
            binder.install(new RingoServiceModule());

            binder.install(new BlobStoreModule());
        }

     
    }

}

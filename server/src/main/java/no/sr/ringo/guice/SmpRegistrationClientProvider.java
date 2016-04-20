package no.sr.ringo.guice;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.difi.ws.client.SmpRegistrationClient;
import no.difi.ws.client.SmpRegistrationClientDifiImpl;
import no.difi.ws.client.TestSmpRegistrationClientImpl;

import javax.inject.Provider;
import javax.servlet.ServletContext;

/**
 * User: adam
 * Date: 23/07/12
 */
@RequestScoped
public class SmpRegistrationClientProvider implements Provider<SmpRegistrationClient> {

    private ServletContext sc;

    @Inject
    public SmpRegistrationClientProvider(ServletContext sc) {
        this.sc = sc;
    }

    @Override
    public SmpRegistrationClient get() {
        //returns a mock implementation if the mockDifi is set to true
        Boolean mockDifi = (Boolean) sc.getAttribute("mockDifi");
        if (mockDifi != null && mockDifi) {
            return new TestSmpRegistrationClientImpl();
        }
        return new SmpRegistrationClientDifiImpl();
    }
    
}

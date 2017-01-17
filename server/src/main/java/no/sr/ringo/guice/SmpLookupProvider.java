package no.sr.ringo.guice;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.RingoSmpLookupImpl;
import no.sr.ringo.smp.TestModeSmpLookupImpl;

import javax.inject.Provider;
import javax.servlet.ServletContext;

/**
 * User: andy
 * Date: 3/21/12
 * Time: 10:59 AM
 */
@RequestScoped
public class SmpLookupProvider implements Provider<RingoSmpLookup> {

    private ServletContext sc;
    private final LookupClient lookupClient;

    @Inject
    public SmpLookupProvider(ServletContext sc, LookupClient lookupClient) {
        this.sc = sc;
        this.lookupClient = lookupClient;
    }

    @Override
    public RingoSmpLookup get() {
        //returns a mock implementation if the mockSmp is set to true
        Boolean mockSmp = (Boolean) sc.getAttribute("mockSmp");
        if (mockSmp != null && mockSmp) {
            return new TestModeSmpLookupImpl();
        }
        return new RingoSmpLookupImpl(lookupClient);
    }
    
}

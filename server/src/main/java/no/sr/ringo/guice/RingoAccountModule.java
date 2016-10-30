package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import eu.peppol.persistence.api.SrAccountNotFoundException;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.account.RingoAccountProvider;
import no.sr.ringo.security.CredentialHandler;
import no.sr.ringo.security.SecretKeyCredentialHandler;

import java.security.Principal;

/**
 * Makes the javax.security.Principal and RingoAccount objects available for injection.
 * User: andy
 * Date: 1/20/12
 * Time: 1:12 PM
 */
public class RingoAccountModule extends AbstractModule {

    @Override
    protected void configure() {
        //makes the java.security.Principal object available through GUICE
        bind(Principal.class).toProvider(PrincipalProvider.class);
        //Makes the account provider available to objects, which do not have request scope.
        bind(RingoAccountProvider.class);

        bind(CredentialHandler.class).to(SecretKeyCredentialHandler.class);
    }

    /**
     * Allows using @Inject with RingoAccount objects when the object has a RequestScope
     *
     * @param principal
     * @param ringoAccountProvider
     * @return
     */
    @Provides
    public Account getRingoAccount(Principal principal, RingoAccountProvider ringoAccountProvider){
        try {
            return ringoAccountProvider.getAccount(principal);
        } catch (SrAccountNotFoundException e) {
            throw new IllegalStateException("Unable to find account for " + principal);
        }
    }

}

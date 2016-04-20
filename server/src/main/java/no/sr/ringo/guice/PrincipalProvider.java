package no.sr.ringo.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * <p>Makes the java.security.Principal available in the request scope.</p>
 * <p>i.e. you can inject the Principal into objects which have a request scope.</p>
 * <p>e.g. InboxResource</p>
 * User: andy
 * Date: 1/20/12
 * Time: 11:31 AM
 */
@RequestScoped
public final class PrincipalProvider implements Provider<Principal> {

    private SecurityContext sc;

    @Inject
    public PrincipalProvider(SecurityContext sc) {
        this.sc = sc;
    }

    @Override
    public Principal get() {
        return sc.getUserPrincipal();
    }
}

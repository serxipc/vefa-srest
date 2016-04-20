package no.sr.ringo.guice;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.common.IsProductionServer;

import javax.inject.Provider;
import javax.servlet.ServletContext;

@RequestScoped
public class IsProductionServerProvider implements Provider<IsProductionServer> {

    private ServletContext sc;

    @Inject
    public IsProductionServerProvider(ServletContext sc) {
        this.sc = sc;
    }

    @Override
    public IsProductionServer get() {
        return new IsProductionServer(isProductionServer());
    }

    private boolean isProductionServer() {
        String isProductionServer = sc.getInitParameter("isProductionServer");
        return "true".equalsIgnoreCase(isProductionServer);
    }

}

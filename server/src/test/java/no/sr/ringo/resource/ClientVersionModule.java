package no.sr.ringo.resource;

import com.google.inject.AbstractModule;
import no.sr.ringo.guice.ClientVersion;

/**
 * This module binds the current client version number to the @ClientVersion annotation.
 */
public class ClientVersionModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bindConstant().annotatedWith(ClientVersion.class).to("1.1-SNAPSHOT");
    }

}

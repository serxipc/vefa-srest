package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class OxalisOutboundModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(OxalisOutboundModule.class).in(Singleton.class);
    }
}

package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import eu.peppol.smp.SmlHost;
import eu.peppol.util.OperationalMode;

/**
 * @author steinar
 *         Date: 23.10.2016
 *         Time: 23.32
 */
public class SmpInTestModeModule extends AbstractModule{
    @Override
    protected void configure() {

    }

    @Provides
    OperationalMode getOperationalMode() {
        return OperationalMode.TEST;
    }

    @Provides
    SmlHost getSmlHost() {
        return SmlHost.TEST_SML;
    }

}

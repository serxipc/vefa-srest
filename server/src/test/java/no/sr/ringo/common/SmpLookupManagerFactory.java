package no.sr.ringo.common;

import eu.peppol.smp.*;
import eu.peppol.util.OperationalMode;

/**
 * TODO this should be guice injected like everywhere else or a provider
 */
public class SmpLookupManagerFactory {

    public static SmpLookupManager getSmpLookupManager() {
        return new SmpLookupManagerImpl(new SmpContentRetrieverImpl(), new DefaultBusDoxProtocolSelectionStrategyImpl(), OperationalMode.TEST, SmlHost.TEST_SML);
    }

}

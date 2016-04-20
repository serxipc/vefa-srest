package no.sr.ringo.common;

import eu.peppol.smp.*;

/**
 * TODO this should be guice injected like everywhere else or a provider
 */
public class SmpLookupManagerFactory {

    public static SmpLookupManager getSmpLookupManager() {
        return new SmpLookupManagerImpl(new SmpContentRetrieverImpl(), new DefaultBusDoxProtocolSelectionStrategyImpl());
    }

}

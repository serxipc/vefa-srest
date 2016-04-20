package no.sr.ringo.guice;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import eu.peppol.smp.SmpLookupManager;
import no.sr.ringo.common.SmpLookupManagerFactory;
import no.sr.ringo.smp.RingoSmpLookupImpl;
import no.sr.ringo.smp.TestModeSmpLookupImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fake scope used to provide either the real Smp Lookup or the mocked smp lookup
 * in tests only!
 */
class FakeScope implements Scope {
    static final Logger log = LoggerFactory.getLogger(FakeScope.class);
    private final boolean mockSmp;

    public FakeScope(boolean mockSmp) {
        this.mockSmp = mockSmp;
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscopedProvider) {

        if (isSmpLookupProvider(unscopedProvider)) {
            log.info("Faking scope for SMPLookupProvider");
            return createProvider();
        } else {
            log.info(String.format("Unable to fake scope for '%s'", unscopedProvider.getClass().getName()));
            return unscopedProvider;
        }
    }

    private <T> boolean isSmpLookupProvider(Provider<T> unscoped) {
        return unscoped.getClass().equals(SmpLookupProvider.class);
    }

    private <T> Provider<T> createProvider() {
        return new Provider<T>() {
            @Override
            public T get() {
                if (mockSmp) {
                    return (T) new TestModeSmpLookupImpl();
                }
                return (T) new RingoSmpLookupImpl(SmpLookupManagerFactory.getSmpLookupManager());
            }
        };
    }
}

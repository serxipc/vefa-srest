package no.sr.ringo.guice;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fake scope used to provide either the real Smp Lookup or the mocked smp lookup
 * in tests only!
 */
class FakeScope implements Scope {
    static final Logger log = LoggerFactory.getLogger(FakeScope.class);

    public FakeScope() {
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> provider) {
        // Just return the current provider
        return provider;
    }
}
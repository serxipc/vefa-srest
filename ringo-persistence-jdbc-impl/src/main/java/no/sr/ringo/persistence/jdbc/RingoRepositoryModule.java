package no.sr.ringo.persistence.jdbc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.message.MessageRepository;
import no.sr.ringo.persistence.file.ArtifactPathComputer;
import no.sr.ringo.persistence.guice.AopJdbcTxManagerModule;
import no.sr.ringo.persistence.queue.QueueRepository;
import no.sr.ringo.persistence.queue.QueueRepositoryImpl;

/**
 * Provides the classes needed for persistence.
 *
 * NOTE! it is expected that a {@link javax.sql.DataSource} is provided by another module
 * in the injector wrapping this module.
 * 
 * @author steinar
 *         Date: 26.01.2017
 *         Time: 15.17
 */
public class RingoRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {

        // Binds the Guice JDBC Tx stuff
        binder().install(new AopJdbcTxManagerModule());
        bind(ArtifactPathComputer.class);
        bind(MessageRepository.class).to(MessageRepositoryH2Impl.class);
        bind(AccountRepository.class).to(AccountRepositoryImpl.class);
        bind(QueueRepository.class).to(QueueRepositoryImpl.class).in(Singleton.class);
    }
}

package eu.peppol.persistence.jdbc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import eu.peppol.persistence.MessageRepository;
import eu.peppol.persistence.api.account.AccountRepository;
import eu.peppol.persistence.file.ArtifactPathComputer;
import eu.peppol.persistence.guice.AopJdbcTxManagerModule;
import eu.peppol.persistence.queue.QueueRepository;
import eu.peppol.persistence.queue.QueueRepositoryImpl;

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

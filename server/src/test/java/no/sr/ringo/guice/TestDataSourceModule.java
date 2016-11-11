package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import eu.peppol.persistence.jdbc.util.InMemoryDatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.sql.DataSource;

/**
 * The test data source
 *
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 01.01.12
 *         Time: 12:04
 */
public class TestDataSourceModule extends AbstractModule {

    public static final Logger log = LoggerFactory.getLogger(TestDataSourceModule.class);
    @Override
    protected void configure() {
        bind(DatabaseHelper.class);
    }


    @Provides
    @Singleton
    public DataSource provideH2DataSource() {
        log.warn("Creating in memory database and populating the schema. This should happen only once!");
        return InMemoryDatabaseHelper.createInMemoryDatabase();

    }
}

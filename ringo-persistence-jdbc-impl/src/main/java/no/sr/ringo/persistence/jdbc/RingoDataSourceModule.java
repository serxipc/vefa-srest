package no.sr.ringo.persistence.jdbc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.sql.DataSource;

/**
 * @author steinar
 *         Date: 30.01.2017
 *         Time: 09.34
 */
public class RingoDataSourceModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(RingoDataSourceFactoryDbcpImpl.class).in(Singleton.class);
    }

    @Provides
    DataSource provideDataSource(RingoDataSourceFactoryDbcpImpl dsFactory) {
        return dsFactory.getDataSource();
    }
}

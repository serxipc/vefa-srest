package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import no.sr.ringo.common.DatabaseHelper;

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

    @Override
    protected void configure() {
        bind(DatabaseHelper.class);
    }

    @Provides
    public DataSource provideDataSource() {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://localhost/oxalis_test");
        mysqlDataSource.setUser("skrue");
        mysqlDataSource.setPassword("vable");
        return mysqlDataSource;
    }

}

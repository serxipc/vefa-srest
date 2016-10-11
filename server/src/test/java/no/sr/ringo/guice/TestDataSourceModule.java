package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import no.sr.ringo.common.DatabaseHelper;
import org.h2.jdbcx.JdbcDataSource;

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

    /** Use with Mysql */
    public DataSource provideMysqlDataSource() {
        DataSource dataSource = null;
        /*
        dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/oxalis_test");
        dataSource.setUser("skrue");
        dataSource.setPassword("vable");
        */
        return dataSource;
    }

    @Provides
    public DataSource provideH2DataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:~/.oxalis/ap;AUTO_SERVER=TRUE");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        return dataSource;
    }

}

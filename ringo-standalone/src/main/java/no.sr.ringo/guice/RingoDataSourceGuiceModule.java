/* Created by steinar on 01.01.12 at 17:39 */
package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.commons.dbcp.BasicDataSource;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * The Jndi data source is provided by this module
 *
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 01.01.12
 *         Time: 17:39
 */
public class RingoDataSourceGuiceModule extends AbstractModule {

    private final String host;
    private final String user;
    private final String pass;
    private final String name;

    public RingoDataSourceGuiceModule(String dbHost, String dbUser, String dbPass, String dbName) {
        this.host = dbHost;
        this.user = dbUser;
        this.pass = dbPass;
        this.name = dbName;
    }

    @Override
    protected void configure() {
    }

    @Provides
    public DataSource provideDataSource() throws NamingException {

        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        dataSource.setUrl("jdbc:mysql://" + host + ":3306/" + name);
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        dataSource.setInitialSize(5);
        dataSource.setValidationQuery("SELECT 1");

        return dataSource;
    }

}

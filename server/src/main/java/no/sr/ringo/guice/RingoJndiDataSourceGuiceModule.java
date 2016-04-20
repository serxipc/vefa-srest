/* Created by steinar on 01.01.12 at 17:39 */
package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
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
public class RingoJndiDataSourceGuiceModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(RingoJndiDataSourceGuiceModule.class);

    private final String jndiName;

    public RingoJndiDataSourceGuiceModule(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override
    protected void configure() {
    }

    @Provides
    public DataSource provideDataSource() throws NamingException {

        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");

        return (DataSource) envCtx.lookup(jndiName);
    }

}

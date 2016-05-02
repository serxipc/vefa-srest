package no.sr.ringo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * The DataSource provided by this module is used within appservers via JNDI lookup
 *
 * @author Steinar Overbeck Cook
 */
public class RingoJndiDataSourceGuiceModule extends AbstractModule {

    private final String jndiName;

    public RingoJndiDataSourceGuiceModule(String jndiName) {
        this.jndiName = jndiName;
    }

    @Override
    protected void configure() {
        /* nothing */
    }

    @Provides
    public DataSource provideDataSource() throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        return (DataSource) envCtx.lookup(jndiName);
    }

}

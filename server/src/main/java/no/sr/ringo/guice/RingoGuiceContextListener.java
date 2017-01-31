/* Created by steinar on 01.01.12 at 14:40 */
package no.sr.ringo.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import eu.peppol.persistence.jdbc.RingoRepositoryModule;
import no.sr.ringo.common.PropertyHelper;
import no.sr.ringo.config.RingoConfigModule;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.io.IOException;
import java.util.Properties;

/**
 * This is where the whole Ringo server application is wired together :)
 *
 * @author Steinar Overbeck Cook
 */
public class RingoGuiceContextListener extends GuiceServletContextListener {

    private Properties properties;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
        ServletContext servletContext = servletContextEvent.getServletContext();

        setUpExternalServices(servletContext);
    }

    @Override
    protected Injector getInjector() {

        String jndiName= "jdbc/oxalis";

        final String clientVersionNumber = getClientVersion();
        final boolean enableTracingDebug = getTracingDebug();


        return Guice.createInjector(
                // enable Guice access to Principal and injection of RingoAccount and CredentialHandler
                new RingoAccountModule(),
                //the ringo web app
                new RingoWebServletModule(),
                //the jax-rs configuration
                new RingoRestModule(clientVersionNumber,enableTracingDebug),
                //the ringo service
                new RingoServiceModule(),

                // The repositories
                new RingoRepositoryModule(),
                // The JDBC datasource to be obtained via JNDI
                new RingoJndiDataSourceGuiceModule(jndiName),

                new RingoConfigModule()
        );
    }


    private boolean getTracingDebug() {
        return "true".equalsIgnoreCase(fetchProperties().getProperty("ringo.debug"));
    }

    private String getClientVersion() {
        return fetchProperties().getProperty("client.version");
    }

    private Properties fetchProperties() {
        if (properties == null) {
            try {
                properties = PropertyHelper.fetchProperties("/server.properties");
            } catch (IOException e) {
                throw new IllegalStateException("Unable to start server property server.properties not found");
            }
        }
        return properties;
    }

    private void setUpExternalServices(ServletContext servletContext) {
        // determines if this is the production server
        String isProductionServer = servletContext.getInitParameter("isProductionServer");

        if ("true".equalsIgnoreCase(isProductionServer)) {
            servletContext.setAttribute("mockDifi", Boolean.FALSE); // What is this used for?
        }
        else {
            servletContext.setAttribute("mockDifi", Boolean.TRUE);
        }
    }

}

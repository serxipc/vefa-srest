/* Created by steinar on 04.01.12 at 22:20 */
package no.sr.ringo.http;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.client.RingoClientImpl;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.TestFileHelper;
import no.sr.ringo.standalone.DefaultRingoConfig;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Base class which is responsible for several things
 * 1. Setting up jetty and starting it before tests are run.
 * 2. Creating the client for accessing the rest service.
 * 3. Converting responses
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class AbstractHttpClientServerTest {

    protected static final RingoAccount TEST_ACCOUNT = ObjectMother.getTestAccount();
    protected static final String REALM = "test-realm";
    protected static final String WEB_CONTEXT_NAME = "ringo";

    /** Holds the name of the URI path segment, which represents all Jersey resources */
    protected static final int HTTP_PORT = 8888;
    public static final String PEPPOL_BASE_REST_URL = "http://localhost:" + HTTP_PORT + "/" + WEB_CONTEXT_NAME;
    protected static final String PEPPOL_BASE_URL = PEPPOL_BASE_REST_URL ;

    private static Server server;

    private static WebAppContext webAppContext;

    protected DefaultHttpClient httpClient;
    protected RingoClientImpl ringoRestClientImpl;

    protected DefaultRingoConfig config;

    @BeforeGroups(groups = {"integration"})
    public static void setUpJetty() throws Exception {

        // Creates the empty Jetty Server with no connectors or handlers
        server = new Server(HTTP_PORT);

        // Configures the Jetty JNDI DataSource
        EnvConfiguration envConfiguration = getEnvironmentConfiguration();

        setUpWebAppContext(envConfiguration);

        // Adds the list of handlers to the Jetty server
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(webAppContext);
        server.setHandler(handlerList);

        // Configures Jetty implementation of JNDI, this is JNDI mumbo jumbo (woodoo), in our web context
        // thus allowing us to reference java:comp/env/jdbc/peppol-ap
        // Consult jetty-env.xml to see the details
        System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
        System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");

        // Start the server
        server.start();
    }

    private static void setUpWebAppContext(EnvConfiguration envConfiguration) {
        // Establishes a web context from src/main/webapp/WEB-INF/web.xml
        webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        webAppContext.setContextPath("/" + WEB_CONTEXT_NAME);

        // Sets configuration file from which web context should be configured
        webAppContext.setDescriptor(TestFileHelper.sourcePath("src/main/webapp/WEB-INF/web.xml").toString());
        // Sets directory from which files should be served
        webAppContext.setResourceBase(TestFileHelper.sourcePath("src/main/webapp").toString());
        webAppContext.setSecurityHandler(getSecurityHandler());
        webAppContext.setConfigurations(new org.eclipse.jetty.webapp.Configuration[]{new WebInfConfiguration(), envConfiguration, new WebXmlConfiguration()});
    }

    private static EnvConfiguration getEnvironmentConfiguration() throws MalformedURLException {
        EnvConfiguration envConfiguration = new EnvConfiguration();
        URI url = TestFileHelper.sourcePath("src/main/webapp/WEB-INF/jetty-env.xml").toURI();
        if (url == null) {
            throw new IllegalStateException("Unable to find jetty-env.xml");
        }
        envConfiguration.setJettyEnvXml(url.toURL());
        return envConfiguration;
    }

    /**
     * This method allows subclasses to add "superadmin" role to test user ad-hoc
     */
    protected static void allowSuperAdminRoleAccessToAdminConstraint() {
        ConstraintSecurityHandler h = (ConstraintSecurityHandler) webAppContext.getSecurityHandler();
        Constraint constraint2 = new Constraint();
        constraint2.setName(Constraint.__BASIC_AUTH);;
        constraint2.setRoles(new String[]{"superadmin"});
        constraint2.setAuthenticate(true);

        ConstraintMapping cm2 = new ConstraintMapping();
        cm2.setConstraint(constraint2);
        cm2.setPathSpec("/admin/*");
        h.addConstraintMapping(cm2);

    }
    private static SecurityHandler getSecurityHandler() {
        // add authentication
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);;
        constraint.setRoles(new String[]{"client"});
        constraint.setAuthenticate(true);

        // map the security constraint to the root path.
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");


        // create the security handler, set the authentication to Basic
        // and assign the realm.
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName(REALM);
        csh.addConstraintMapping(cm);

        // set the login service
        csh.setLoginService(getHashLoginService());
        return csh;

    }

    static HashLoginService getHashLoginService() {

        // creates the login service, assigns the realm and reads the user credentials
        // from the file /tmp/realm.properties.
        HashLoginService hls = new HashLoginService();
        hls.setName(REALM);

        // Note: user name must not contain ':'
        hls.putUser(TEST_ACCOUNT.getUserName().stringValue(), new Credential() {
            @Override
            public boolean check(Object credentials) {
                if (credentials != null && credentials instanceof String) {
                    String password = (String) credentials;
                    return password.equals(TEST_ACCOUNT.getPassword());
                } else return false;
            }
        }, new String[]{"client", "superadmin"});

        hls.setRefreshInterval(0);
        return hls;
    }

    /** Used when testing the admin section which does not use the RingoClient */
    @BeforeMethod(alwaysRun = true)
    public void createHttpClient(){
        httpClient = new DefaultHttpClient();
        AuthScope authScope = new AuthScope("localhost", HTTP_PORT);
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(TEST_ACCOUNT.getUserName().stringValue(), TEST_ACCOUNT.getPassword());

        httpClient.getCredentialsProvider().setCredentials(authScope, usernamePasswordCredentials);
    }

    @BeforeMethod
    public void setUpPeppolRestClient() throws Exception {
        config = new DefaultRingoConfig(PEPPOL_BASE_URL, null);
        ringoRestClientImpl = new RingoClientImpl(config, TEST_ACCOUNT.getUserName().stringValue(), TEST_ACCOUNT.getPassword());
    }

    @AfterMethod(alwaysRun = true)
    public void closeHttpClient() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    @AfterGroups(groups = {"integration"})
    public static void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Retrieves the HTTP entity as a string
     * @return http entity as a String
     */
    protected String convertEntityToString(HttpResponse getDocumentResponse) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        getDocumentResponse.getEntity().writeTo(baos);
        return baos.toString(RingoConstants.DEFAULT_CHARACTER_SET);
    }
}

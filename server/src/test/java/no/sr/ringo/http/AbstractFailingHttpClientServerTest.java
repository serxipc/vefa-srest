package no.sr.ringo.http;

import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.client.RingoClientImpl;
import no.sr.ringo.common.TestFileHelper;
import no.sr.ringo.standalone.DefaultRingoConfig;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
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

/**
 * We need a server that responds with error messages rather than valid responses.
 * User: andy
 * Date: 2/23/12
 * Time: 11:04 AM
 */
public class AbstractFailingHttpClientServerTest {

    protected static final Account TEST_ACCOUNT = ObjectMother.getTestAccount();
    protected static final String REALM = "test-realm";
    protected static final String WEB_CONTEXT_NAME = "error";
    protected static final int HTTP_PORT = 8889;
    protected static final String PEPPOL_BASE_REST_URL = "http://localhost:" + HTTP_PORT + "/" + WEB_CONTEXT_NAME;

    private static Server server;

    protected DefaultHttpClient httpClient;
    protected RingoClientImpl ringoRestClientImpl;
    protected DefaultRingoConfig config;

    @BeforeGroups(groups = {"failingServer"})
    public static void setUpJetty() throws Exception {

        // Creates the empty Jetty Server with no connectors or handlers
        server = new Server(HTTP_PORT);

        // Establishes a web context from src/main/webapp/WEB-INF/web.xml
        WebAppContext webAppContext = getWebAppContext();

        // Adds the list of handlers to the Jetty server
        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(webAppContext);
        server.setHandler(handlerList);

        // Configures Jetty implementation of JNDI, this is JNDI mumbo jumbo (woodoo), in our web context
        // thus allowing us to reference java:comp/env/jdbc/peppol-ap
        System.setProperty("java.naming.factory.url.pkgs", "org.eclipse.jetty.jndi");
        System.setProperty("java.naming.factory.initial", "org.eclipse.jetty.jndi.InitialContextFactory");


        // Start the server
        server.start();
    }

    /** Establishes the Basic Authentication for the Http client */
    @BeforeMethod(alwaysRun = true)
    public void createHttpClient(){
        httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(new AuthScope("localhost", HTTP_PORT), new UsernamePasswordCredentials(TEST_ACCOUNT.getUserName().stringValue(), TEST_ACCOUNT.getPassword()));
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpPeppolRestClient() throws Exception {
        config = new DefaultRingoConfig(PEPPOL_BASE_REST_URL, null);
        ringoRestClientImpl = new RingoClientImpl(config, TEST_ACCOUNT.getUserName().stringValue(), TEST_ACCOUNT.getPassword());
    }

    @AfterMethod(alwaysRun = true)
    public void closeHttpClient() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
    }

    @AfterGroups(groups = {"failingServer"})
    public static void tearDown() throws Exception {
        server.stop();
    }

    private static WebAppContext getWebAppContext() {
        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        webAppContext.setContextPath("/" + WEB_CONTEXT_NAME);
        // Sets configuration file from which web context should be configured
        webAppContext.setDescriptor(TestFileHelper.sourcePath("src/test/webapp/WEB-INF/web.xml").toString());
        // Sets directory from which files should be served
        webAppContext.setResourceBase(TestFileHelper.sourcePath("src/test/webapp").toString());
        webAppContext.setSecurityHandler(getSecurityHandler());
        webAppContext.setConfigurations(new org.eclipse.jetty.webapp.Configuration[]{new WebInfConfiguration(), new WebXmlConfiguration()});
        return webAppContext;
    }

    private static SecurityHandler getSecurityHandler() {
        // add authentication
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
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

        // create the login service, assign the realm and read the user credentials
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
        }, new String[]{"user", "admin"});

        hls.setRefreshInterval(0);
        return hls;
    }
}

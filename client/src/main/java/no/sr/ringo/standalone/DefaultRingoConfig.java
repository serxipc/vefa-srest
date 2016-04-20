package no.sr.ringo.standalone;

import no.sr.ringo.client.RingoConfig;
import no.sr.ringo.common.ProxySettings;
import no.sr.ringo.common.RingoLoggingStream;
import no.sr.ringo.request.RetryHandler;
import no.sr.ringo.request.RetryStrategy;
import org.apache.http.client.HttpRequestRetryHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Provides the default configuration of the Ringo Client for the standalone app.
 * User: andy
 * Date: 2/27/12
 * Time: 10:00 AM
 */
public class DefaultRingoConfig implements RingoConfig {
    private static final int DEFAULT_SOCKET_TIMEOUT = 20;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 10;

    private final URI baseUri;
    private RetryHandler retryHandler;
    private RingoLoggingStream ringoLoggingStream;

    private int socketTimeout;
    private int connectionTimeout;

    private ProxySettings proxySettings;

    public DefaultRingoConfig(String baseUri, ProxySettings proxySettings) {
        this(baseUri, proxySettings, DEFAULT_SOCKET_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * Use this constructor if you don't wish to use the default timeouts
     */
    public DefaultRingoConfig(String baseUri, ProxySettings proxySettings, int socketTimeout, int connectionTimeout) {
        this.proxySettings = proxySettings;
        this.ringoLoggingStream = new RingoLoggingStream(System.err);
        this.retryHandler = new RetryHandler(RetryStrategy.DEFAULT);
        try {
            this.baseUri = new URI(baseUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI " + baseUri + "; " + e, e);
        }
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Gets the base uri for the ringo server. e.g. http://ringo.domain.com
     *
     * @return
     */
    public URI getBaseUri() {
        return baseUri;
    }

    /**
     * Gets the amount of time to wait in seconds when establishing a connection to the access point.
     *
     * @return
     */
    public int getConnectionTimeOut() {
        return connectionTimeout;
    }

    /**
     * Gets the amount of time to wait for a response during communication with the access point.
     *
     * @return
     */
    public int getSocketTimeOut() {
        return socketTimeout;
    }

    /**
     * Gets the handler which determines what to do if the request to the server fails.
     * <p/>
     * The {@link no.sr.ringo.request.RetryHandler} so that the
     * {@link no.sr.ringo.request.RetryStrategy} can be changed.
     * <p/>
     * For more flexibility a HttpRequestRetryHandler can also be used.
     */
    public HttpRequestRetryHandler getRetryHandler() {
        return retryHandler;
    }

    /**
     * The logger to use for messages and error messages.
     *
     * @return
     */
    public RingoLoggingStream getLogger() {
        return ringoLoggingStream;
    }

    public ProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    /**
     * Sets the retry handler to use
     * @param retryHandler
     */
    public void setRetryHandler(RetryHandler retryHandler) {
        this.retryHandler = retryHandler;
    }

    /**
     * Sets the logger to use
     * @param ringoLoggingStream
     */
    public void setRingoLoggingStream(RingoLoggingStream ringoLoggingStream) {
        this.ringoLoggingStream = ringoLoggingStream;
    }


}

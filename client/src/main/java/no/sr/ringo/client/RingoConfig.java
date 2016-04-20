package no.sr.ringo.client;

import no.sr.ringo.common.ProxySettings;
import no.sr.ringo.common.RingoLoggingStream;
import org.apache.http.client.HttpRequestRetryHandler;

import java.net.URI;

/**
 * Configuration options for the ringo client
 * <p/>
 * User: andy
 * Date: 2/27/12
 * Time: 9:32 AM
 */
public interface RingoConfig {
    /**
     * Gets the base uri for the ringo server. e.g. http://ringo.domain.com
     *
     * @return
     */
    URI getBaseUri();

    /**
     * Gets the amount of time to wait when establishing a connection to the access point.
     *
     * @return
     */
    int getConnectionTimeOut();

    /**
     * Gets the amount of time to wait for a response during communication with the access point.
     *
     * @return
     */
    int getSocketTimeOut();

    /**
     * Gets the handler which determines what to do if the request to the server fails.
     * <p/>
     * The {@link no.sr.ringo.request.RetryHandler} so that the
     * {@link no.sr.ringo.request.RetryStrategy} can be changed.
     * <p/>
     * For more flexibility a HttpRequestRetryHandler can also be used.
     */
    HttpRequestRetryHandler getRetryHandler();

    /**
     * The logger to use for messages and error messages.
     *
     * @return
     */
    RingoLoggingStream getLogger();

    /**
     * Proxy settings
     * @return
     */
    ProxySettings getProxySettings();

}
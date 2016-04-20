package no.sr.ringo.client;

import no.sr.ringo.common.ProxySettings;
import no.sr.ringo.common.RingoLoggingStream;
import org.apache.http.client.HttpRequestRetryHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
* User: andy
* Date: 10/25/12
* Time: 3:56 PM
*/
public class DefaultTestRingoConfig implements RingoConfig{


    /**
     * Gets the base uri for the ringo server. e.g. http://ringo.domain.com
     *
     * @return
     */
    public URI getBaseUri() {
        try {
            return new URI("http://localhost/");
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to get base uri");
        }
    }

    public int getConnectionTimeOut() {
        return 0;
    }

    public int getSocketTimeOut() {
        return 0;
    }

    public HttpRequestRetryHandler getRetryHandler() {
        return null;
    }

    public RingoLoggingStream getLogger() {
        return null;
    }

    public ProxySettings getProxySettings() {
        return null;
    }
}

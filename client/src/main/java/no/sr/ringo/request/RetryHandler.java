package no.sr.ringo.request;

import no.sr.ringo.response.exception.AccessPointTemporarilyUnavailableException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;

/**
 * Automatically retry the request based on the given RetryStrategy
 *
 * User: andy
 * Date: 2/23/12
 * Time: 1:09 PM
 */
public class RetryHandler implements HttpRequestRetryHandler {
    static final Logger log = LoggerFactory.getLogger(RetryHandler.class);

    private final RetryStrategy retryStrategy;

    public RetryHandler(RetryStrategy retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * Retries the request if executionCount < the retry strategies max retry count
     * after waiting for the length of time specified by the retry strategy.
     *
     * @param exception the cause of the retry
     * @param executionCount the current execution index.
     * @param context HttpContext represents execution state of an HTTP process
     * @return
     */
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {

        //if the maximum number of retries has been reached then we exit by returning false
        final int maxRetryCount = retryStrategy.getMaxRetryCount() + 1;
        if (maxRetryCount > 0 && executionCount >= maxRetryCount) {
            log.error(String.format("Unable to complete the request, after %s attempts.", executionCount));
            // Do not retry if over max retry count
            return false;
        }

        if (exception instanceof AccessPointTemporarilyUnavailableException) {
            log.warn(String.format("The SREST Access Point is temporarily unavailable"));

            //The default amount of time to wait.
            int pauseLength = retryStrategy.pauseLength();

            //looks at the response to see if a Retry-After header has been specified
            //if it has use that value instead of the amount specified in the strategy.
            final HttpResponse httpResponse = (HttpResponse) context.getAttribute(ExecutionContext.HTTP_RESPONSE);
            final Header[] headers = httpResponse.getHeaders("Retry-After");
            if (headers != null && headers.length > 0) {
                final String retryAfter = headers[0].getValue();
                int retryAfterSeconds = parseRetryAfterHeader(retryAfter);
                if (retryAfterSeconds > 0) {
                    pauseLength = retryAfterSeconds;
                    log.debug(String.format("Server response indicates waiting for %s seconds", pauseLength));
                }
            }

            //do we pause between each retry?
            if(pauseLength > 0) {
                sleep(pauseLength);
            }
            //503 response from the server
            return true;
        }

        if (exception instanceof NoHttpResponseException) {
            // Retry if the server dropped connection on us
            return true;
        }
        if (exception instanceof SSLHandshakeException) {
            // Do not retry on SSL handshake exception
            return false;
        }
        return false;
    }

    private int parseRetryAfterHeader(String retryAfter) {
        if (retryAfter != null) {
            //the specification says the numeric value is in seconds.
            if (StringUtils.isNumeric(retryAfter)) {
                return Integer.parseInt(retryAfter);
            } else {
                //not supported
                //Date expiresDate = DateUtil.parseRfc1123Date(retryAfter);
            }
        }
        return -1;
    }

    private void sleep(int numberOfSeconds) {
        try {
            log.warn(String.format("Request failed retrying in %s seconds", numberOfSeconds));
            Thread.sleep(numberOfSeconds * 1000);
        } catch (InterruptedException e) {
            //interrupted so we can continue.
        }
    }

}



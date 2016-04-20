package no.sr.ringo.response.exception;

import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Exception thrown when the Rest Service is unavailable.
 * This can be for example 503 when the server is down for maintenance
 *
 * User: andy
 * Date: 2/23/12
 * Time: 10:50 AM
 */
public class AccessPointTemporarilyUnavailableException extends IOException {

    private final int statusCode;
    private HttpContext httpContext;

    public AccessPointTemporarilyUnavailableException(HttpContext httpContext, int statusCode) {
        this.httpContext = httpContext;
        this.statusCode = statusCode;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }
}

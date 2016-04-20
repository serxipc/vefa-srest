package no.sr.ringo.response;

import no.sr.ringo.response.exception.AccessPointTemporarilyUnavailableException;
import no.sr.ringo.response.exception.BadCredentialsException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Handles when
 *
 * User: andy
 * Date: 2/23/12
 * Time: 10:25 AM
 */
public class ResponseCodeInterceptor implements HttpResponseInterceptor {
    /**
     * Throws exceptions if the response code is 503 or 401
     *
     * @param response the response to postprocess
     * @param context  the context for the request
     * @throws org.apache.http.HttpException in case of an HTTP protocol violation
     * @throws java.io.IOException           in case of an I/O error
     */
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 503) {
            throw new AccessPointTemporarilyUnavailableException(context,statusCode);
        }
        if (statusCode == 401) {
            //Depends whether we have authorised or not...!
            throw new BadCredentialsException(statusCode);
        }
    }
}

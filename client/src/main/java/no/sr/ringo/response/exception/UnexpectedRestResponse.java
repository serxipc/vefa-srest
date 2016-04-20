/* Created by steinar on 06.01.12 at 12:57 */
package no.sr.ringo.response.exception;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class UnexpectedRestResponse extends RuntimeException {

    public UnexpectedRestResponse(HttpRequestBase httpRequest, HttpResponse response, Exception e) {
        super(e.getMessage(), e);
    }

}

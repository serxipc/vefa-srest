/* Created by steinar on 06.01.12 at 14:15 */
package no.sr.ringo.response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * The response for a successfull post is CREATED 201
 * So this parser returns 201 if successful.
 *
 * @author Andy andy@sendregning.no
 */
public class MarkAsReadRingoResponseHandler implements RingoResponseHandler<Boolean> {

    public Boolean handleResponse(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }
}

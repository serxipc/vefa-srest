/* Created by steinar on 06.01.12 at 14:15 */
package no.sr.ringo.response;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * The response for a successful post is CREATED 201
 * So this parser returns 201 if successful.
 *
 */
public class NotificationRingoResponseHandler implements RingoResponseHandler<Boolean> {

    public Boolean handleResponse(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }
}

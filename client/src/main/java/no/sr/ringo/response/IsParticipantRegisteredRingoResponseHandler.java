/* Created by steinar on 06.01.12 at 14:15 */
package no.sr.ringo.response;

import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

/**
 * Handles the response from a lookup on /directory
 * If it is 200 ok then the Participant is registered in the SMP.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class IsParticipantRegisteredRingoResponseHandler implements RingoResponseHandler<Boolean> {

    public Boolean handleResponse(HttpResponse response) throws UnexpectedResponseCodeException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return true;
        }
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            return false;
        }
        throw new UnexpectedResponseCodeException(response);
    }

}

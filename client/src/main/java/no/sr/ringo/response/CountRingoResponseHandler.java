/* Created by steinar on 06.01.12 at 14:15 */
package no.sr.ringo.response;

import no.sr.ringo.common.ResponseUtils;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;

/**
 * Handles the response from a Count request.
 * The Count request returns an String containing an Integer
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class CountRingoResponseHandler implements RingoResponseHandler<Integer> {

    public Integer handleResponse(HttpResponse response) throws UnexpectedResponseCodeException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (!(statusCode == HttpStatus.SC_OK)) {
            throw new UnexpectedResponseCodeException(response);
        }

        try {
            String result = ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET);

            //converts the string into an Integer representing the count.
            return Integer.valueOf(result);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write entity to byte stream: "+e,e );
        }
    }

}

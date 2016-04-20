/* Created by steinar on 06.01.12 at 12:53 */
package no.sr.ringo.response.exception;

import no.sr.ringo.common.ResponseUtils;
import no.sr.ringo.common.RingoConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * When the response handler gets an unexpected response code.
 * @author andy@sendregning.no
 */
public class UnexpectedResponseCodeException extends IOException {
    static final Logger log = LoggerFactory.getLogger(UnexpectedResponseCodeException.class);

    private final HttpResponse response;
    private String message;

    public UnexpectedResponseCodeException(HttpResponse response) {
        this.response = response;
        this.message = createMessage();
    }

    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        return message;
    }
    
    private String createMessage(){
        final String message = String.format("Access point server returned response code '%s'",response.getStatusLine().getStatusCode());
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            try {
                return String.format("%s : '%s'" ,message, ResponseUtils.writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET));
            } catch (IOException e) {
                //ignore
                log.error("unable to get error message from response");
            }
        }
        return message;
    }
}

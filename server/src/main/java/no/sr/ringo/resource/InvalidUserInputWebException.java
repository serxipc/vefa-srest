package no.sr.ringo.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Exception which logs the message and produces a 400 BadRequest response.
 * <p/>
 *  The request could not be understood by the server due to incorrect data.
 *  The client SHOULD NOT repeat the request without modifications.
 *  <p/>
 * User: andy
 * Date: 1/25/12
 * Time: 10:29 AM
 */
public class InvalidUserInputWebException extends WebApplicationException {

    private final String message;
    static final Logger log = LoggerFactory.getLogger(InvalidUserInputWebException.class);

    /**
     * Creates a 400 response with the provided message and Logs an info with the same message
     * @param message
     */
    public InvalidUserInputWebException(String message) {
        this(message, null);
    }

    public InvalidUserInputWebException(String message, Exception cause) {
        super(cause,createBadRequestResponse(message));
        this.message = message;
        logMessage(log);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private static Response createBadRequestResponse(String message) {
        return SrResponse.status(400)
                .entity(message).type(RingoMediaType.TEXT_PLAIN).build();
    }

    void logMessage(Logger log) {
        if (log.isInfoEnabled()) {
            log.info(message,getCause());
        }
    }
}

package no.sr.ringo.resource;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catches all unexpected errors
 * User: andy
 * Date: 2/27/12
 * Time: 12:32 PM
 */
@Provider
@Singleton
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
    static final Logger log = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);
    /**
     * Map an exception to a {@link javax.ws.rs.core.Response}. Returning
     * {@code null} results in a {@link javax.ws.rs.core.Response.Status#NO_CONTENT}
     * response. Throwing a runtime exception results in a
     * {@link javax.ws.rs.core.Response.Status#INTERNAL_SERVER_ERROR} response
     *
     * @param exception the exception to map to a response
     * @return a response mapped from the supplied exception
     */
    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException)exception).getResponse();
        }

        log.error("Unexpected exception caught",exception);
        return SrResponse.serverError("Unknown server error occurred.");
    }
}

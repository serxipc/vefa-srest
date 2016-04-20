package no.sr.ringo.resource;

import com.google.inject.Singleton;
import no.sr.ringo.message.PeppolMessageNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handles the peppol message not found exception by returning 204 NO Content
 * User: andy
 * Date: 2/27/12
 * Time: 12:32 PM
 */
@Provider
@Singleton
public class MessageNotFoundExceptionMapper implements ExceptionMapper<PeppolMessageNotFoundException> {

    public Response toResponse(PeppolMessageNotFoundException exception) {
        return SrResponse.exception(exception);
    }
}

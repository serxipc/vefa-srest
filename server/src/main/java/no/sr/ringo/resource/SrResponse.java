/* Created by steinar on 06.01.12 at 13:33 */
package no.sr.ringo.resource;


import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;

/**
 * Helper method which provides default responses for certain conditions.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SrResponse {
    static final Logger log = LoggerFactory.getLogger(SrResponse.class);

    public static Response.ResponseBuilder ok() {
        return Response.ok();
    }

    public static Response.ResponseBuilder created(URI uri) {
        return Response.created(uri);
    }

    /**
     * Creates a response with a message with the given status code.
     * @param message
     * @return
     */
    public static Response serverError(String message) {
        return status(Response.Status.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Creates a response with a message with the given status code.
     * @param status
     * @param message
     * @return
     */
    public static Response status(Response.Status status, String message) {
        return Response.status(status).entity(message).type(RingoMediaType.TEXT_PLAIN).build();
    }

    /**
     * If it is a Sax Exception the XML they provided is invalid therefore a BAD_REQUEST
     * @param e
     * @return
     */
    public static Response exception(SAXException e) {
        return SrResponse.status(Response.Status.BAD_REQUEST, e.getMessage());
    }

    /**
     * IOException is unknown so its an internal server error
     * @param e
     * @return
     */
    public static Response exception(IOException e) {
        return SrResponse.status(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * When message is not found we return 204 NO content
     * @param peppolMessageNotFound
     * @return
     */
    public static Response exception(PeppolMessageNotFoundException peppolMessageNotFound) {
        log.warn("Unable to findDocument message", peppolMessageNotFound);
        return Response.noContent().build();
    }

    public static Response.ResponseBuilder status(int status) {
        return Response.status(status).header("Content-Encoding", RingoConstants.DEFAULT_CHARACTER_SET);
    }
}

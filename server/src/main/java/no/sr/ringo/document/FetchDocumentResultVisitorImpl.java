package no.sr.ringo.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

/**
 * Builds a JAX-RS {@link Response} object using the visitor pattern.
 *
 *
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.29
 */
public class FetchDocumentResultVisitorImpl implements  FetcdocumentResultVisitor<Response> {

    public static final Logger LOGGER = LoggerFactory.getLogger(FetchDocumentResultVisitorImpl.class);

    @Override
    public Response visit(PeppolDocument peppolDocument) {
        return Response.ok().entity(peppolDocument.getXml()).build();
    }

    @Override
    public Response visit(PeppolDocumentReference peppolDocumentReference) {
        // Redirects to whatever URI is held in the PeppolDocumentReference
        LOGGER.debug("Redirecting to {}", peppolDocumentReference.getPayloadUri().toString());
        return Response.seeOther(peppolDocumentReference.getPayloadUri()).build();
    }
}

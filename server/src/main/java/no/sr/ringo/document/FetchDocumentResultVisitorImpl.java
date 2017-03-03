package no.sr.ringo.document;

import no.sr.ringo.message.PayloadUriRewriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Visits a {@link PayloadReference} and builds a JAX-RS {@link Response} object using the visitor pattern
 *
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.29
 */
public class FetchDocumentResultVisitorImpl implements FetchDocumentResultVisitor<Response> {

    public static final Logger LOGGER = LoggerFactory.getLogger(FetchDocumentResultVisitorImpl.class);
    private final PayloadUriRewriter payloadUriRewriter;

    /**
     * Constructor
     * 
     * @param payloadUriRewriter rewrites any non {@code file} scheme URIs.
     */
    @Inject
    public FetchDocumentResultVisitorImpl(PayloadUriRewriter payloadUriRewriter) {

        this.payloadUriRewriter = payloadUriRewriter;
    }

    /**
     * Visits a {@link FetchDocumentResult} represented by an instance of {@link PeppolDocument}
     * holding the actual text of the payload, so simply creates a REST
     * response with the payload as the entity.
     *
     * @param peppolDocument the {@link FetchDocumentResult} instance to visit
     */
    @Override
    public Response visit(PeppolDocument peppolDocument) {
        return Response.ok().entity(peppolDocument.getXml()).build();
    }

    /**
     * Visits a {@link FetchDocumentResult} in the representation of {@link PayloadReference} and
     * performs any URI rewrite required.
     *
     * @param payloadReference the {@link FetchDocumentResult} to visit
     * @return a REST response
     */
    @Override
    public Response visit(PayloadReference payloadReference) {

        LOGGER.debug("Payload rewriting being handled by {}", payloadUriRewriter.getClass().getName());

        // Redirects to whatever URI is held in the PayloadReference
        final URI rewritten = payloadUriRewriter.rewrite(payloadReference.getPayloadUri());

        LOGGER.debug("Payload URI {} rewritten to {}", payloadReference.getPayloadUri().toString(), rewritten);

        return Response.seeOther(rewritten).build();
    }
}

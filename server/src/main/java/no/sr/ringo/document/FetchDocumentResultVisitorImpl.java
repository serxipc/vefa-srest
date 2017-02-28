package no.sr.ringo.document;

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


    @Override
    public Response visit(PeppolDocument peppolDocument) {
        return Response.ok().entity(peppolDocument.getXml()).build();
    }

    @Override
    public Response visit(PeppolDocumentReference peppolDocumentReference) {
        // Redirects to whatever URI is held in the PeppolDocumentReference
        return Response.seeOther(peppolDocumentReference.getPayloadUri()).build();
    }
}

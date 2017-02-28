package no.sr.ringo.document;

import java.net.URI;

/**
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.09
 */
public class PeppolDocumentReference implements FetchDocumentResult {

    private final URI payloadUri;

    public PeppolDocumentReference(URI payloadUri) {
        this.payloadUri = payloadUri;
    }

    @Override
    public <T> T accept(FetcdocumentResultVisitor<T> fetcdocumentResultVisitor) {
        return fetcdocumentResultVisitor.visit(this);
    }

    public URI getPayloadUri() {
        return payloadUri;
    }
}

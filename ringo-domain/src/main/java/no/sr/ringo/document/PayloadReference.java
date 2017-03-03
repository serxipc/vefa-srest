package no.sr.ringo.document;

import java.net.URI;

/**
 * Holds URIs for payload instances residing outside of our file system. I.e. they are not accessible via
 * any file based methods.
 *
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.09
 */
public class PayloadReference implements FetchDocumentResult {

    private final URI payloadUri;

    public PayloadReference(URI payloadUri) {

        assert payloadUri != null : "Missing required argument 'payloadUri'";

        if (payloadUri.getScheme().equals("file")) {
            throw new IllegalArgumentException("Payload URIs in the 'file' scheme are not allowed");
        }
        this.payloadUri = payloadUri;
    }

    @Override
    public <T> T accept(FetchDocumentResultVisitor<T> fetchDocumentResultVisitor) {
        return fetchDocumentResultVisitor.visit(this);
    }

    public URI getPayloadUri() {
        return payloadUri;
    }
}

package no.sr.ringo.message;

import java.net.URI;

/**
 * Rewrites URIs from one form to another, like for instance adding a signature to a URI, adding
 * security access tokens and so forth.
 * 
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 14.56
 */
public interface PayloadUriRewriter {


    /**
     * Rewrites the URI into whatever is required.
     *
     * The defalt is to simply do nothing, i.e. return the same.
     *
     * @param payloadUri URI to be modified
     * @return resulting URI
     */
    default URI rewrite(URI payloadUri) {
        return payloadUri;
    }
}

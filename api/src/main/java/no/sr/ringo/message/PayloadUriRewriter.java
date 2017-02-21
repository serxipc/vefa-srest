package no.sr.ringo.message;

import java.net.URI;

/**
 * Rewrites URIs from one form to another, like for instance adding a signature to a URI, adding
 * seurity access tokens and so forth.
 * 
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 14.56
 */
public interface PayloadUriRewriter {


    default URI rewrite(URI requestUri, MessageMetaData messageMetaData) {
        return messageMetaData.getPayloadUri();
    }
}

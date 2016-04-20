package no.sr.ringo.response.xml;

import no.sr.ringo.response.RingoResponseHandler;

/**
 * Mechanism for extracting objects of type T from an XML Response
 *
 * User: andy
 * Date: 1/30/12
 * Time: 9:35 AM
 */
public interface XmlRingoResponseHandler<T> extends RingoResponseHandler<T> {
    /**
     *
     * @param xmlResponseParser
     * @return
     */
    T resolve(XmlResponseParser xmlResponseParser);
}

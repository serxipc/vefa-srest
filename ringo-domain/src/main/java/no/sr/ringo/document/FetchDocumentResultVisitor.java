package no.sr.ringo.document;

/**
 * Visits various objects and produces a result.
 * 
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.05
 */
public interface FetchDocumentResultVisitor<T> {

    /**
     * Visits a {@link PeppolDocument}, i.e. an XML content and produces some result (typically a string
     * containing the XML text)
     *
     * @param peppolDocument the instance to visit
     * @return an instance of whatever result the visitor implementations chooses.
     */
    T visit(PeppolDocument peppolDocument);

    /**
     * Visits a {@link PayloadReference}, i.e. an object holding a URI to the PEPPOL payload
     * containing the XML text
     *
     * @param payloadReference the reference to visit
     * @return an instance of whatever result the visitor implementations chooses.
     */

    T visit(PayloadReference payloadReference);

}

package no.sr.ringo.document;

/**
 * Visits various objects and produces a result.
 * 
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 18.05
 */
public interface FetcdocumentResultVisitor<T> {

    /**
     * Visits a {@link PeppolDocument}, i.e. an XML content and produces some result (typically a string
     * containing the XML text)
     *
     * @param peppolDocument the instance to visit
     * @return an instance of whatever result the visitor implementations chooses.
     */
    T visit(PeppolDocument peppolDocument);

    /**
     * Visits a {@link PeppolDocumentReference}, i.e. an object holding a URI to the PEPPOL payload
     * containing the XML text
     *
     * @param peppolDocumentReference the reference to visit
     * @return an instance of whatever result the visitor implementations chooses.
     */

    T visit(PeppolDocumentReference peppolDocumentReference);

}

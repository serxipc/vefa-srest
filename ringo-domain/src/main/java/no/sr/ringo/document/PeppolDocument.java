package no.sr.ringo.document;


/**
 * User: andy
 * Date: 10/3/12
 * Time: 10:20 AM
 */
public interface PeppolDocument {

    public <T> T acceptVisitor(PeppolDocumentVisitor<T> visitor);

    public String getXml();
}

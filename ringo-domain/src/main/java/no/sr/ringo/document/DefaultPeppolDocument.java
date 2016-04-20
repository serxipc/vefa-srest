package no.sr.ringo.document;

/**
 * Default peppol document is used for documents that we dont recognise.
 */
public class DefaultPeppolDocument implements PeppolDocument {

    private final String xml;

    public DefaultPeppolDocument(String xml) {
        this.xml = xml;
    }

    @Override
    public <T> T acceptVisitor(PeppolDocumentVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public String getXml() {
        return xml;
    }
}

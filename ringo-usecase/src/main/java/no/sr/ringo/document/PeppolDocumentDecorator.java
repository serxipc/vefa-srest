package no.sr.ringo.document;

/**
 * abstract decorator class which other PeppolDocumentDecorates should extend
 */
public abstract class PeppolDocumentDecorator implements PeppolDocument {

    protected PeppolDocument peppolDocument;

    protected PeppolDocumentDecorator(PeppolDocument peppolDocument) {
        this.peppolDocument = peppolDocument;
    }

    @Override
    public <T> T acceptVisitor(PeppolDocumentVisitor<T> visitor) {
        return peppolDocument.acceptVisitor(visitor);
    }

    @Override
    public String getXml() {
        return peppolDocument.getXml();
    }
}

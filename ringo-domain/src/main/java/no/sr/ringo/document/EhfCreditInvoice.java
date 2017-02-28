package no.sr.ringo.document;

public class EhfCreditInvoice implements PeppolDocument {

    private final String xml;

    public EhfCreditInvoice(String xml) {
        this.xml = xml;
    }

    @Override
    public <T> T acceptVisitor(PeppolDocumentVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(FetcdocumentResultVisitor<T> fetcdocumentResultVisitor) {
        return fetcdocumentResultVisitor.visit(this);
    }

    public String getXml() {
        return xml;
    }
}

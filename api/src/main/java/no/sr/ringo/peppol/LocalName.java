package no.sr.ringo.peppol;

/**
 * Represents the XML local name used in {@link DocumentTypeIdentifier} and as the root element in XML documents.
 */
public final class LocalName {

    private final String localName;

    public static final LocalName Invoice = new LocalName("Invoice");
    public static final LocalName CreditNote = new LocalName("CreditNote");
    public static final LocalName Order = new LocalName("Order");
    public static final LocalName Catalogue = new LocalName("Catalogue");
    public static final LocalName OrderResponse = new LocalName("OrderResponse");
    public static final LocalName ApplicationResponse = new LocalName("ApplicationResponse");
    public static final LocalName Reminder = new LocalName("Reminder");
    public static final LocalName Tender = new LocalName("Tender");
    public static final LocalName TenderReceipt = new LocalName("TenderReceipt");
    public static final LocalName CallForTenders = new LocalName("CallForTenders");

    private LocalName(String localName) {
        this.localName = localName;
    }

    public static LocalName valueOf(String localName) {
        return new LocalName(localName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalName localName1 = (LocalName) o;
        if (localName != null ? !localName.equals(localName1.localName) : localName1.localName != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return localName != null ? localName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return localName;
    }

}

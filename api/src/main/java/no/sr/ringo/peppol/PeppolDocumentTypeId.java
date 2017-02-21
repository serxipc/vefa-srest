package no.sr.ringo.peppol;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.sr.ringo.cenbiimeta.ProfileId;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a PEPPOL document type identificator, which is a string which looks something like this:
 * <pre>
 * urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
 * </pre>
 * Which is to be interpreted like this:
 * <table border="1">
 *     <tr>
 *         <td>Root name space</td>
 *         <td>urn:oasis:names:specification:ubl:schema:xsd:Invoice-2</td>
 *     </tr>
 *     <tr>
 *         <td>Local name</td>
 *         <td>Invoice</td>
 *     </tr>
 *     <tr>
 *         <td>Customization Identifier</td>
 *         <td>urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1</td>
 *     </tr>
 *     <tr>
 *         <td>Version</td>
 *         <td>2.0</td>
 *     </tr>
 * </table>
 * @deprecated use DocumentTypeIdentifier
 */

public class PeppolDocumentTypeId {

    final RootNameSpace rootNameSpace;
    final LocalName localName;
    final CustomizationIdentifier customizationIdentifier;
    private final String version;

    // Popular document type identifiers, which are commonly used
    public static final PeppolDocumentTypeId EHF_INVOICE = new PeppolDocumentTypeId(RootNameSpace.INVOICE, LocalName.Invoice, CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T010_INVOICE_V1 + ":#" + ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY + "#" + ProfileId.Predefined.EHF_INVOICE), "2.0");
    public static final PeppolDocumentTypeId EHF_CREDIT_NOTE = new PeppolDocumentTypeId(RootNameSpace.CREDIT, LocalName.CreditNote, CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T014_CREDIT_NOTE_V1 + ":#" + ProfileId.Predefined.PROPOSED_BII_XX +"#" + ProfileId.Predefined.EHF_CREDIT_NOTE), "2.0");

    private static final Pattern documentIdPattern = Pattern.compile("(urn:.*)::(.*)##(urn:.*)::(.*)");

    private PeppolDocumentTypeId(){
        rootNameSpace = null;
        localName = LocalName.valueOf("UNKNOWN");
        customizationIdentifier = null;
        version = "";
    }

    public PeppolDocumentTypeId(RootNameSpace rootNameSpace, LocalName localName, CustomizationIdentifier customizationIdentifier, String version) {

        this.rootNameSpace = rootNameSpace;
        this.localName = localName;
        this.customizationIdentifier = customizationIdentifier;
        this.version = version;
    }

    public DocumentTypeIdentifier toVefa(){
        return DocumentTypeIdentifier.of(stringValue());
    }


    public static PeppolDocumentTypeId valueOf(String documentIdAsText) {
        PeppolDocumentTypeId result = new UnknownPeppolDocumentTypeId(documentIdAsText);
        if (documentIdAsText != null) {

            Matcher matcher = documentIdPattern.matcher(documentIdAsText);
            if (matcher.matches()) {

                result = createNewInstance(matcher);
            }
        }
        return result;
    }

    private static PeppolDocumentTypeId createNewInstance(Matcher matcher) {
        String rootNameSpace = matcher.group(1);
        LocalName localName = LocalName.valueOf(matcher.group(2));
        String customizationIdAsText = matcher.group(3);
        String version = matcher.group(4);

        return new PeppolDocumentTypeId(new RootNameSpace(rootNameSpace), localName, CustomizationIdentifier.valueOf(customizationIdAsText), version);
    }

    @Override
    public String toString(){
        final StringBuilder sb = new StringBuilder();
        sb.append(rootNameSpace);
        sb.append("::").append(localName);
        sb.append("##").append(customizationIdentifier);
        sb.append("::").append(version);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeppolDocumentTypeId that = (PeppolDocumentTypeId) o;
        if (customizationIdentifier != null ? !customizationIdentifier.equals(that.customizationIdentifier) : that.customizationIdentifier != null) return false;
        if (localName != null ? !localName.equals(that.localName) : that.localName != null) return false;
        if (rootNameSpace != null ? !rootNameSpace.equals(that.rootNameSpace) : that.rootNameSpace != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = rootNameSpace != null ? rootNameSpace.hashCode() : 0;
        result = 31 * result + (localName != null ? localName.hashCode() : 0);
        result = 31 * result + (customizationIdentifier != null ? customizationIdentifier.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    public LocalName getLocalName() {
        return localName;
    }

    public String stringValue() {
        return toString();
    }

    public boolean isKnown() {
        return !(this instanceof UnknownPeppolDocumentTypeId);
    }

    public String getVersion() {
        return version;
    }

    public static final class UnknownPeppolDocumentTypeId extends PeppolDocumentTypeId {

        private final String urn;

        public UnknownPeppolDocumentTypeId(String urn) {
            super();
            this.urn = urn;
        }

        @Override
        public String toString() {
            return urn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            UnknownPeppolDocumentTypeId that = (UnknownPeppolDocumentTypeId) o;

            if (urn != null ? !urn.equals(that.urn) : that.urn != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (urn != null ? urn.hashCode() : 0);
            return result;
        }
    }

    public CustomizationIdentifier getCustomizationIdentifier() {
        return customizationIdentifier;
    }
}

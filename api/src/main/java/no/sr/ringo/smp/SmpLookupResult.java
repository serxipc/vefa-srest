package no.sr.ringo.smp;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.CustomizationIdentifier;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.ProfileIdTranslator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.*;

/**
 * Holds data representing the result of performing a SMP Lookup
 * And
 */
public class SmpLookupResult {

    protected final List<PeppolDocumentTypeId> acceptedDocumentTypes;

    ProfileIdTranslator profileIdTranslator = new ProfileIdTranslator();

    protected PeppolDocumentTypeIdComparator DEFAULT_COMPARATOR = new PeppolDocumentTypeIdComparator();

    public SmpLookupResult(List<PeppolDocumentTypeId> acceptedDocumentTypes) {
        this.acceptedDocumentTypes = acceptedDocumentTypes;
    }

    public List<PeppolDocumentTypeId> getAcceptedDocumentTypes() {
        return sortDocumentTypes(DEFAULT_COMPARATOR);
    }

    /**
     * Provides the optimal complete document type identifier given a local name.
     * @param localName the local name
     * @return
     */
    public PeppolDocumentTypeId optimalDocumentTypeFor(LocalName localName) {
        for (PeppolDocumentTypeId peppolDocumentTypeId : sortDocumentTypes(DEFAULT_COMPARATOR)) {
            if (peppolDocumentTypeId.getLocalName().equals(localName)) {
                return peppolDocumentTypeId;
            }
        }
        return null;
    }

    private List<PeppolDocumentTypeId> sortDocumentTypes(Comparator<PeppolDocumentTypeId> comparator) {
        List<PeppolDocumentTypeId> ordered = new ArrayList<PeppolDocumentTypeId>();
        ordered.addAll(acceptedDocumentTypes);
        Collections.sort(ordered, comparator);
        return ordered;
    }

    /**
     * Provides the CENBII profileId for a given document type.
     * @param documentType
     * @return
     */
    public ProfileId profileIdFor(PeppolDocumentTypeId documentType) {
        return profileIdTranslator.translateToCenBiiProfile(documentType.getCustomizationIdentifier().getPeppolExtensionIdentifier());
    }

}

/**
 * Sort document types based upon the profiles identified in the extension identifiers of the customization identifier,
 * according to these rules:
 *
 * - UBL 2.1 based documents first (all new variants should be based on UBL 2.1)
 * - EHF specific extentions first
 * - EHF generics, i.e. biixx and biixy
 * - then peppol 5a, PEPPOL BILLING
 * - and peppol 6a as the last one , PEPPOL_PROCUREMENT
 */
class PeppolDocumentTypeIdComparator implements Comparator<PeppolDocumentTypeId>, Serializable {

    public int compare(PeppolDocumentTypeId currentDocument, PeppolDocumentTypeId otherDocument) {
        OrdinalNumberProvider ext1 = getOrdinalNumberProvider(currentDocument);
        OrdinalNumberProvider ext2 = getOrdinalNumberProvider(otherDocument);
        return ext1.getOrdinalNumber().compareTo(ext2.getOrdinalNumber());
    }

    OrdinalNumberProvider getOrdinalNumberProvider(PeppolDocumentTypeId documentType) {
        return ("2.1".equalsIgnoreCase(documentType.getVersion())) ? new PeppolDocumentTypeIdWithOrdinalNumber(documentType) : new CustomizationIdWithOrdinalNumber(documentType.getCustomizationIdentifier());
    }

    interface OrdinalNumberProvider {
        public Integer getOrdinalNumber();
    }

    // provides UBL 2.1 based logic for sorting document types (valued at 1-50)
    class PeppolDocumentTypeIdWithOrdinalNumber implements OrdinalNumberProvider {

        private final Integer MIN_PRIORITY = 50;

        private PeppolDocumentTypeId peppolDocumentTypeId;

        PeppolDocumentTypeIdWithOrdinalNumber(PeppolDocumentTypeId peppolDocumentTypeId) {
            this.peppolDocumentTypeId = peppolDocumentTypeId;
        }

        public Integer getOrdinalNumber() {

            int base = 0;

            String customization = peppolDocumentTypeId.getCustomizationIdentifier().toString();
            if (!customization.contains("urn:www.difi.no:ehf")) base = base + 20;

            String localname = peppolDocumentTypeId.getLocalName().toString();
            if ("Invoice".equalsIgnoreCase(localname)) return base + 1;
            if ("CreditNote".equalsIgnoreCase(localname)) return base + 2;
            if ("Order".equalsIgnoreCase(localname)) return base + 3;
            if ("OrderResponse".equalsIgnoreCase(localname)) return base + 4;
            if ("Catalogue".equalsIgnoreCase(localname)) return base + 5;

            return MIN_PRIORITY;
        }

    }

    // provides old UBL 2.0 based logic for sorting document types (valued at 51-100)
    class CustomizationIdWithOrdinalNumber implements OrdinalNumberProvider {

        private final Integer MIN_PRIORITY = 100;

        private CustomizationIdentifier customizationIdentifier;

        CustomizationIdWithOrdinalNumber(CustomizationIdentifier customizationIdentifier) {
            this.customizationIdentifier = customizationIdentifier;
        }

        boolean isEHFInvoice() {
            return customizationIdentifier.containsExtension(EHF_INVOICE);
        }

        boolean isEHFCreditNoteOnly() {
            return customizationIdentifier.containsExtension(EHF_CREDIT_NOTE) && customizationIdentifier.containsExtension(PROPOSED_BII_XX);
        }

        boolean isEHFCreditNote() {
            return customizationIdentifier.containsExtension(EHF_CREDIT_NOTE);
        }

        boolean isPeppolInvoiceOnly() {
            return customizationIdentifier.containsExtension(PEPPOL_4A_INVOICE_ONLY);
        }

        boolean isEhfInvoiceCreditNoteAndReminder() {
            return customizationIdentifier.containsExtension(PROPOSED_BII_XY);
        }

        boolean isPeppolBilling() {
            return customizationIdentifier.containsExtension(PEPPOL_5A_BILLING);
        }

        boolean isPeppolProcurement() {
            return customizationIdentifier.containsExtension(PEPPOL_6A_PROCUREMENT);
        }

        public Integer getOrdinalNumber() {
            if (isPeppolInvoiceOnly() && isEHFInvoice()) {                          // peppol4a && EHF invoice
                return 51;
            } else if (isEHFCreditNoteOnly()) {                                     // biixx && EHF CN
                return 52;
            } else if (isEhfInvoiceCreditNoteAndReminder() && isEHFInvoice()) {     // biixy && EHF invoice
                return 53;
            } else if (isEhfInvoiceCreditNoteAndReminder() && isEHFCreditNote()) {  // biixy && EHF CN
                return 54;
            } else if (isEhfInvoiceCreditNoteAndReminder()) {                       // bixy
                return 55;
            } else if (isPeppolInvoiceOnly()) {                                     // peppol4a
                return 56;
            } else if (isPeppolBilling()) {                                         // peppol5a
                return 57;
            } else if (isPeppolProcurement()) {                                     // peppol6a
                return 58;
            } else {
                return MIN_PRIORITY;
            }
        }

    }

}

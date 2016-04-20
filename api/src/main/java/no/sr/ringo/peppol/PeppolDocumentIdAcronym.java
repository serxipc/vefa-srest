package no.sr.ringo.peppol;

/**
 * <p>Translates deprecated acronyms into <em>PEPPOL Document type identifier local name</em> and vice versa.</p>
 * <p>
 * To simplify coding, we introduced a finite set of acronyms rather than having to use the complicated Peppol document type identifiers.
 * However; this turned out to be too simple. Since these acronyms are exposed in the external API, we could not
 * simply remove the use of this class. Henceforth; the class is still here.
 * </p>
 * <p>
 * <table border="1">
 *     <tr>
 *         <th>Acronym name</th><th>Local name</th>
 *     </tr>
 *     <tr>
 *         <td>INVOICE</td><td>Invoice</td>
 *     </tr>
 *     <tr>
 *         <td>CREDIT_NOTE</td><td>CreditNote</td>
 *     </tr>
 *     <tr>
 *         <td>ORDER</td><td>Order</td>
 *     </tr>
 *     <tr>
 *         <td>UNKNOWN</td><td>*</td>
 *     </tr>
 * </table>
 *<p>Package protected to prevent use outside of this package.</p>
 *
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 04.12.11
 *         Time: 18:52
 *
 *         @deprecated use the constructor of {@link PeppolDocumentTypeId}
 */
enum PeppolDocumentIdAcronym {
    /** PEPPOL 4A invoice */
    INVOICE(LocalName.Invoice),
    /** PEPPOL 6A credit invoice */
    CREDIT_NOTE(LocalName.CreditNote),
    /** PEPPOL 3A Order */
    ORDER(LocalName.Order),

    UNKNOWN(LocalName.valueOf("UNKNOWN") );

    private final LocalName localName;

    /**
     *
     * @param localName
     */
        private PeppolDocumentIdAcronym(LocalName localName) {
        this.localName = localName;
    }

    /*
     * Returns the PeppolDocumentIdAcronym for the given acronym name represented in plain text.
     * UNKNOWN is returned if the acronym is unknown, i.e. is not found.
     * I.e. acronym name -> PeppolDocumentIdAcronym.name
     */
    public static PeppolDocumentIdAcronym fromAcronym(String acronymName) {
        for (PeppolDocumentIdAcronym peppolDocumentIdAcronym : values()) {
            if (peppolDocumentIdAcronym.name().equals(acronymName)) {
                return peppolDocumentIdAcronym;
            }
        }
        return UNKNOWN;
    }

    /**
     * Provides an acronym for a given PEPPOL Document type identifier LocalName.
     * I.e. LocalName -> PeppolDocumentIdAcronym
     */
    public static PeppolDocumentIdAcronym fromLocalName(LocalName localName) {
        for (PeppolDocumentIdAcronym peppolDocumentIdAcronym : values()) {
            if (peppolDocumentIdAcronym.toLocalName().equals(localName)) {
                return peppolDocumentIdAcronym;
            }
        }
        return UNKNOWN;
    }

    public LocalName toLocalName() {
        return localName;
    }
}


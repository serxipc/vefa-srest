package no.sr.ringo.peppol;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.SmpLookupResult;

/**
 * Resolve document id passed as string into proper VO. Performs SMPLookup if acronym used.
 *
 * User: Adam
 * Date: 10/29/12
 * Time: 8:24 AM
 */
public class DocumentTypeIdResolver {

    private final RingoSmpLookup ringoSmpLookup;


    public DocumentTypeIdResolver(RingoSmpLookup ringoSmpLookup) {
        this.ringoSmpLookup = ringoSmpLookup;
    }

    /**
     * Checks if documentIdString is an acronym, if so performs SMPLookup to
     * fetch available documentTypeIds and takes the first one from the list.
     *
     * @param documentIdString represents the document type id either as a valid Peppol document type id or as an old fashioned acronym
     *
     * Returns PeppolDocumentTypeId id given String is already a proper value
     */
    public PeppolDocumentTypeId resolve(ParticipantId peppolParticipantId, String documentIdString) {

        PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(documentIdString);
        if (peppolDocumentTypeId.isKnown()) {
            return peppolDocumentTypeId;
        } else {
            LocalName localName = deprecatedDocumentIdAcronym2LocalName(documentIdString);
            return findOptimalDocumentTypeIdentifierFor(peppolParticipantId, localName);
        }
    }

    /**
     * Performs SMP lookup to fetch document types accepted by the recipient. If the resultset
     * is not empty return the optimal value (first one in ordered list).
     * Throws an exception if there're no accepted document types available
     */
    private PeppolDocumentTypeId findOptimalDocumentTypeIdentifierFor(ParticipantId peppolParticipantId, LocalName localName) {
        SmpLookupResult result = ringoSmpLookup.fetchSmpMetaData(peppolParticipantId, localName);
        if (result.getAcceptedDocumentTypes().isEmpty()) {
            throw new IllegalArgumentException(String.format("No accepted document type identifiers for local name: %s", localName));
        }

        return result.optimalDocumentTypeFor(localName);
    }

    /**
     * Tries to create PeppolDocumentIdAcronym object from given string value.
     * Throws an exception if value doesn't match any known acronym value
     */
    private LocalName deprecatedDocumentIdAcronym2LocalName(String documentIdString) {
        PeppolDocumentIdAcronym acronym = PeppolDocumentIdAcronym.fromAcronym(documentIdString);
        if (PeppolDocumentIdAcronym.UNKNOWN.equals(acronym)) {
            throw new IllegalArgumentException(String.format("Invalid documentId value: %s", documentIdString));
        }
        return acronym.toLocalName();
    }
}

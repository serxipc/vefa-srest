package no.sr.ringo.smp;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;

/**
 * Defines the RingoSmpLookup 'service'
 *
 * User: andy
 * Date: 1/25/12
 * Time: 11:04 AM
 */
public interface RingoSmpLookup {
    /**
     * Checks if a participant is available in the SMP register for any document type
     */
    boolean isRegistered(ParticipantId participantId);


    /** Retrieves all variants of PEPPOL Document types having the supplied localname (XML local name), which is accepted by the
     * supplied participant.
     * @param peppolParticipantId PEPPOL Participant identifier
     * @param localName XML local name of the document types in question
     * @return container holding the accepted document types
     */
    SmpLookupResult fetchSmpMetaData(ParticipantId peppolParticipantId, LocalName localName);

    /**
     * Verifies whether a participant will accept the supplied document type or not. A value of false does not imply that
     * the participant does not accept other document types.
     * @param participantId
     * @param peppolDocumentTypeId
     * @return true if participant is registered in the SMP and will accept the supplied document type. Returns false if the participant is not registered in SMP for
     * the supplied document type.
     */
    public boolean isAcceptable(ParticipantId participantId, PeppolDocumentTypeId peppolDocumentTypeId);
}

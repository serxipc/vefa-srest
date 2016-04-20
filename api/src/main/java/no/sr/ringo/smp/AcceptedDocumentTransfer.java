package no.sr.ringo.smp;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;

/**
 * Represents document transfer method accepted by the receiver (Peppol Participant)
 * User: Adam
 */
public class AcceptedDocumentTransfer {

    private final PeppolDocumentTypeId documentTypeId;
    private final ProfileId profileId;

    public AcceptedDocumentTransfer(PeppolDocumentTypeId documentTypeId, ProfileId profileId) {
        this.documentTypeId = documentTypeId;
        this.profileId = profileId;
    }

    public PeppolDocumentTypeId getDocumentTypeId() {
        return documentTypeId;
    }

    public ProfileId getProfileId() {
        return profileId;
    }
}

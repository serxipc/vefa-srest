package no.sr.ringo.account;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.ParticipantId;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 31.12.11
 *         Time: 17:19
 */
public class SrAccountNotFoundException extends RuntimeException {
    public SrAccountNotFoundException(AccountId id) {
        super("SR Account " + id + " not found");
    }

    public SrAccountNotFoundException(ParticipantId participantId) {
        super("SR Account for participant id " + participantId + " not found");
    }

    public SrAccountNotFoundException(UserName username) {
        super("SR Account for username" + username + " not found");
    }
}

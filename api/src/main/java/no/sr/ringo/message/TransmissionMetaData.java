package no.sr.ringo.message;

import no.sr.ringo.account.AccountId;

import java.net.URI;

/**
 * Represents a complete message meta data entry holding all the properties of a transmission.
 * 
 * @author steinar
 *         Date: 17.02.2017
 *         Time: 10.11
 */
public interface TransmissionMetaData extends MessageMetaData {

    AccountId getAccountId();

    URI getEvidenceUri();
}

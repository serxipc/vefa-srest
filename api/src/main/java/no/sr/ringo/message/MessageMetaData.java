package no.sr.ringo.message;

import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.transport.TransferDirection;

import java.net.URI;
import java.util.Date;

/**
 *
 * The Meta data used in the REST based backend to describe a message to be received or sent from or to
 * the PEPPOL network.
 * 
 * User: andy
 * Date: 1/20/12
 * Time: 3:03 PM
 */
public interface MessageMetaData {

    TransferDirection getTransferDirection();

    Date getReceived();

    Date getDelivered();

    PeppolHeader getPeppolHeader();

    MessageNumber getMsgNo();

    TransmissionIdentifier getTransmissionId();         // AS2 Message-ID

    ReceptionId getReceptionId();

    URI getPayloadUri();
}

package no.sr.ringo.message;

import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.transport.TransferDirection;
import no.sr.ringo.transport.TransmissionId;

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

    Long getMsgNo();

    TransmissionId getTransmissionId();         // AS2 Message-ID

    ReceptionId getReceptionId();

    URI getPayloadUri();
}

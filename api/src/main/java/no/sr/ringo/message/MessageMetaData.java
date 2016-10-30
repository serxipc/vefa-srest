package no.sr.ringo.message;

import eu.peppol.persistence.TransferDirection;
import no.sr.ringo.peppol.PeppolHeader;

import java.util.Date;
import java.util.UUID;

/**
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

    String getUuid();

}

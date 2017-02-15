package no.sr.ringo.message;

import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.transport.TransferDirection;

import java.util.Date;

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

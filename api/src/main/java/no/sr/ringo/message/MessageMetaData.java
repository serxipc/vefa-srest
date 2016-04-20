package no.sr.ringo.message;

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

    Integer getMsgNo();

    String getUuid();

    String getInvoiceNo();


}

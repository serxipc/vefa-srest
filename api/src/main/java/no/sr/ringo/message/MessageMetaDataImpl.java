/* Created by steinar on 08.01.12 at 19:55 */
package no.sr.ringo.message;

import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.vefa.peppol.common.model.InstanceIdentifier;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.transport.TransferDirection;

import java.net.URI;
import java.util.Date;

/**
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class MessageMetaDataImpl implements TransmissionMetaData {

    private MessageNumber msgNo;

    private AccountId accountId;
    private TransferDirection transferDirection;
    private Date received = new Date();
    private Date delivered;

    private ReceptionId receptionId = new ReceptionId();    // Our internal reception identification
    private TransmissionIdentifier transmissionId;      // AS2 Message-ID
    private InstanceIdentifier sbdhInstanceIdentifier; // SBDH instance identifier

    URI payloadUri;
    URI evidenceUri;

    private PeppolHeader peppolHeader;

    public MessageMetaDataImpl() {
        peppolHeader = new PeppolHeader();
    }

    public MessageMetaDataImpl(MessageMetaDataImpl mmd) {
        msgNo = mmd.getMsgNo();

        accountId = mmd.getAccountId();
        transferDirection = mmd.getTransferDirection();
        received = mmd.getReceived();
        delivered = mmd.getDelivered();

        final PeppolHeader ph = new PeppolHeader();
        peppolHeader = ph;

        ph.setPeppolChannelId(mmd.getPeppolHeader().getPeppolChannelId());
        ph.setProcessIdentifier(mmd.getPeppolHeader().getProcessIdentifier());
        ph.setDocumentTypeIdentifier(mmd.getPeppolHeader().getPeppolDocumentTypeId());
        ph.setSender(mmd.getPeppolHeader().getSender());
        ph.setReceiver(mmd.getPeppolHeader().getReceiver());

        receptionId = mmd.getReceptionId();
        transmissionId = mmd.getTransmissionId();
        sbdhInstanceIdentifier = mmd.getSbdhInstanceIdentifier();
        payloadUri = mmd.getPayloadUri();
        evidenceUri = mmd.getEvidenceUri();
    }
    
    @Override
    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

    @Override
    public TransferDirection getTransferDirection() {
        return transferDirection;
    }

    public void setTransferDirection(TransferDirection transferDirection) {
        this.transferDirection = transferDirection;
    }


    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }


    public PeppolHeader getPeppolHeader() {
        return peppolHeader;
    }

    public void setPeppolHeader(PeppolHeader peppolHeader) {
        this.peppolHeader = peppolHeader;
    }


    public MessageNumber getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(MessageNumber msgNo) {
        this.msgNo = msgNo;
    }


    public TransmissionIdentifier getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(TransmissionIdentifier transmissionId) {
        this.transmissionId = transmissionId;
    }

    @Override
    public URI getPayloadUri() {
        return payloadUri;
    }

    public void setPayloadUri(URI payloadUri) {
        this.payloadUri = payloadUri;
    }

    @Override
    public URI getEvidenceUri() {
        return evidenceUri;
    }

    public void setEvidenceUri(URI nativeEvidenceUri) {
        this.evidenceUri = nativeEvidenceUri;
    }

    @Override
    public ReceptionId getReceptionId() {
        return receptionId;
    }

    public void setReceptionId(ReceptionId receptionId) {
        this.receptionId = receptionId;
    }

    public void setSbdhInstanceIdentifier(InstanceIdentifier sbdhInstanceIdentifier) {
        this.sbdhInstanceIdentifier = sbdhInstanceIdentifier;
    }

    public InstanceIdentifier getSbdhInstanceIdentifier() {
        return sbdhInstanceIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageMetaDataImpl that = (MessageMetaDataImpl) o;

        if (msgNo != null ? !msgNo.equals(that.msgNo) : that.msgNo != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (transferDirection != that.transferDirection) return false;
        if (received != null ? !received.equals(that.received) : that.received != null) return false;
        if (delivered != null ? !delivered.equals(that.delivered) : that.delivered != null) return false;
        if (transmissionId != null ? !transmissionId.equals(that.transmissionId) : that.transmissionId != null)
            return false;
        if (payloadUri != null ? !payloadUri.equals(that.payloadUri) : that.payloadUri != null) return false;
        if (evidenceUri != null ? !evidenceUri.equals(that.evidenceUri) : that.evidenceUri != null)
            return false;
        return peppolHeader != null ? peppolHeader.equals(that.peppolHeader) : that.peppolHeader == null;
    }

    @Override
    public int hashCode() {
        int result = msgNo != null ? msgNo.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (transferDirection != null ? transferDirection.hashCode() : 0);
        result = 31 * result + (received != null ? received.hashCode() : 0);
        result = 31 * result + (delivered != null ? delivered.hashCode() : 0);
        result = 31 * result + (transmissionId != null ? transmissionId.hashCode() : 0);
        result = 31 * result + (payloadUri != null ? payloadUri.hashCode() : 0);
        result = 31 * result + (evidenceUri != null ? evidenceUri.hashCode() : 0);
        result = 31 * result + (peppolHeader != null ? peppolHeader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageMetaDataImpl{");
        sb.append("msgNo=").append(msgNo);
        sb.append(", accountId=").append(accountId);
        sb.append(", transferDirection=").append(transferDirection);
        sb.append(", received=").append(received);
        sb.append(", delivered=").append(delivered);
        sb.append(", receptionId=").append(receptionId);
        sb.append(", transmissionId='").append(transmissionId).append('\'');
        sb.append(", sbdhInstanceIdentifier=").append(sbdhInstanceIdentifier);
        sb.append(", payloadUri=").append(payloadUri);
        sb.append(", nativeEvidenceUri=").append(evidenceUri);
        sb.append(", peppolHeader=").append(peppolHeader);
        sb.append('}');
        return sb.toString();
    }

}

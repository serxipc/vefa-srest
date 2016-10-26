/* Created by steinar on 08.01.12 at 19:55 */
package no.sr.ringo.message;

import no.sr.ringo.account.AccountId;
import no.sr.ringo.peppol.PeppolHeader;

import java.util.Date;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class MessageMetaDataImpl implements MessageMetaData {

    private Long msgNo;

    private AccountId accountId;
    private TransferDirection transferDirection;
    private Date received = new Date();
    private Date delivered;
    private String uuid;

    private PeppolHeader peppolHeader;

    public MessageMetaDataImpl() {
        peppolHeader = new PeppolHeader();
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public void setAccountId(AccountId accountId) {
        this.accountId = accountId;
    }

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


    public Long getMsgNo() {
        return msgNo;
    }

    public void setMsgNo(Long msgNo) {
        this.msgNo = msgNo;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageMetaDataImpl that = (MessageMetaDataImpl) o;

        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (delivered != null ? !delivered.equals(that.delivered) : that.delivered != null) return false;
        if (msgNo != null ? !msgNo.equals(that.msgNo) : that.msgNo != null) return false;
        if (peppolHeader != null ? !peppolHeader.equals(that.peppolHeader) : that.peppolHeader != null) return false;
        if (received != null ? !received.equals(that.received) : that.received != null) return false;
        if (transferDirection != that.transferDirection) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = msgNo != null ? msgNo.hashCode() : 0;
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (transferDirection != null ? transferDirection.hashCode() : 0);
        result = 31 * result + (received != null ? received.hashCode() : 0);
        result = 31 * result + (delivered != null ? delivered.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (peppolHeader != null ? peppolHeader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageMetaDataImpl{" +
                "msgNo=" + msgNo +
                ", accountId=" + accountId +
                ", transferDirection=" + transferDirection +
                ", received=" + received +
                ", delivered=" + delivered +
                ", uuid='" + uuid + '\'' +
                ", peppolHeader=" + peppolHeader +
                '}';
    }
}

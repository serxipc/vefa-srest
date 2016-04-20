package no.sr.ringo.account;

import no.sr.ringo.message.TransferDirection;

import java.util.Date;

/**
 * Represents statistics for given account (messages per month)
 * User: Adam
 * Date: 11/5/12
 * Time: 2:26 PM
 */
public class AccountStatistics {
    TransferDirection transferDirection;
    Integer total;
    Date lastDayOfMonth;

    public TransferDirection getTransferDirection() {
        return transferDirection;
    }

    public void setTransferDirection(TransferDirection transferDirection) {
        this.transferDirection = transferDirection;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getLastDayOfMonth() {
        return lastDayOfMonth;
    }

    public void setLastDayOfMonth(Date lastDayOfMonth) {
        this.lastDayOfMonth = lastDayOfMonth;
    }
}

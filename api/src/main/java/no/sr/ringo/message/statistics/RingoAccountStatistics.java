package no.sr.ringo.message.statistics;

import no.sr.ringo.account.AccountId;
import no.sr.ringo.peppol.RingoUtils;

import java.util.Date;

/**
 * DTO which holds the statistics for a given ringo account
 * User: andy
 * Date: 9/5/12
 * Time: 1:30 PM
 */
public class RingoAccountStatistics {

    private final AccountId accountId;
    private final String accountName;
    private final String contactEmail;

    private final int total;
    private final InboxStatistics inboxStatistics;
    private final OutboxStatistics outboxStatistics;


    public RingoAccountStatistics(int total, InboxStatistics inboxStatistics, OutboxStatistics outboxStatistics, AccountId accountId, String accountName, String contactEmail) {
        this.total = total;
        this.inboxStatistics = inboxStatistics;
        this.outboxStatistics = outboxStatistics;
        this.accountId = accountId;
        this.accountName = accountName;
        this.contactEmail = contactEmail;
    }

    /**
     * The id of the ringo Account
     * @return
     */
    public AccountId getRingoAccountId() {
        return accountId;
    }

    /**
     * The name of the account
     * @return
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * The registered contact email for the customer
     * @return
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * The total number of messages
     * @return
     */
    public int getTotal() {
        return total;
    }

    /**
     * The total number of messages inbound
     * @return
     */
    public int getIn() {
        return inboxStatistics.total;
    }

    /**
     * The number of messages inbound that are not downloaded
     * @return
     */
    public int getUndeliveredIn() {
        return inboxStatistics.undelivered;
    }

    /**
     * The last time a file was downloaded from the inbox.
     * @return
     */
    public Date getLastDownloaded() {
        return inboxStatistics.delivered;
    }

    /**
     * The last time a file was received via PEPPOL.
     * @return
     */
    public Date getLastReceived() {
        return inboxStatistics.received;
    }

    public Date getOldestUndelivered() {
        return inboxStatistics.oldestUndelivered;
    }

    /**
     * The total number of messages sent
     * @return
     */
    public int getOut() {
        return outboxStatistics.total;
    }

    /**
     * The number of undelivered outbound messages i.e. the number of
     * messages waiting to be sent via peppol
     * @return
     */
    public int getUndeliveredOut() {
        return outboxStatistics.undelivered;
    }

    /**
     * The last time a message was sent from the outbox via peppol
     * @return
     */
    public Date getLastSent() {
        return outboxStatistics.delivered;
    }

    /**
     * The last time a message was uploaded to the accesspoint
     * via SREST
     * @return
     */
    public Date getLastUploaded() {
        return outboxStatistics.received;
    }


    public InboxStatistics getInboxStatistics() {
        return inboxStatistics;
    }

    public OutboxStatistics getOutboxStatistics() {
        return outboxStatistics;
    }

    public String toXml() {
        return String.format("<account name=\"%s\" contact_email=\"%s\"><messages total=\"%s\">%s%s</messages></account>",
                RingoUtils.toXml(accountName), RingoUtils.toXml(contactEmail), total, inboxStatistics.toXml(), outboxStatistics.toXml());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RingoAccountStatistics that = (RingoAccountStatistics) o;

        if (total != that.total) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (accountName != null ? !accountName.equals(that.accountName) : that.accountName != null) return false;
        if (contactEmail != null ? !contactEmail.equals(that.contactEmail) : that.contactEmail != null) return false;
        if (inboxStatistics != null ? !inboxStatistics.equals(that.inboxStatistics) : that.inboxStatistics != null) return false;
        if (outboxStatistics != null ? !outboxStatistics.equals(that.outboxStatistics) : that.outboxStatistics != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountId != null ? accountId.hashCode() : 0;
        result = 31 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 31 * result + (contactEmail != null ? contactEmail.hashCode() : 0);
        result = 31 * result + total;
        result = 31 * result + (inboxStatistics != null ? inboxStatistics.hashCode() : 0);
        result = 31 * result + (outboxStatistics != null ? outboxStatistics.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RingoAccountStatistics");
        sb.append("{accountId=").append(accountId);
        sb.append(", accountName='").append(accountName).append('\'');
        sb.append(", contactEmail='").append(contactEmail).append('\'');
        sb.append(", total=").append(total);
        sb.append(", inboxStatistics=").append(inboxStatistics);
        sb.append(", outboxStatistics=").append(outboxStatistics);
        sb.append('}');
        return sb.toString();
    }
}

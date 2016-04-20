package no.sr.ringo.message.statistics;

import no.sr.ringo.peppol.RingoUtils;

import java.util.Date;

/**
 * Statistics for the inbox
 * User: andy
 * Date: 9/11/12
 * Time: 1:06 PM
 */
public class InboxStatistics extends MessageContainerStatistics {


    public Date oldestUndelivered;

    public InboxStatistics(int total, int totalUndelivered, Date lastDelivered, Date lastReceived, Date oldestUndelivered) {
        super(total, totalUndelivered, lastDelivered, lastReceived);
        this.oldestUndelivered = oldestUndelivered;
    }

    public Date getOldestUndelivered() {
        return oldestUndelivered;
    }

    @Override
    public String toXml() {
        return String.format("<in total=\"%s\" undelivered=\"%s\" oldest_undelivered=\"%s\" last_downloaded=\"%s\" last_received=\"%s\"></in>", total, undelivered, RingoUtils.formatDateTimeAsISO8601String(oldestUndelivered), RingoUtils.formatDateTimeAsISO8601String(delivered), RingoUtils.formatDateTimeAsISO8601String(received));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("InboxStatistics");
        sb.append("{total=").append(total);
        sb.append(", undelivered=").append(undelivered);
        sb.append("  oldestUndelivered=").append(oldestUndelivered);
        sb.append(", delivered=").append(delivered);
        sb.append(", received=").append(received);
        sb.append('}');
        return sb.toString();
    }
}

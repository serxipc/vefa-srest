package no.sr.ringo.message.statistics;

import no.sr.ringo.peppol.RingoUtils;

import java.util.Date;

/**
 * Statistics for the outbox
 * User: andy
 * Date: 9/11/12
 * Time: 1:06 PM
 */
public class OutboxStatistics extends MessageContainerStatistics {

    public OutboxStatistics(int total, int totalUndelivered, Date lastDelivered, Date lastReceived) {
        super(total, totalUndelivered, lastDelivered, lastReceived);
    }

    @Override
    public String toXml() {
        return String.format("<out total=\"%s\" undelivered=\"%s\" last_sent=\"%s\" last_received=\"%s\"></out>", total, undelivered, RingoUtils.formatDateTimeAsISO8601String(delivered),RingoUtils.formatDateTimeAsISO8601String(received));
    }

}

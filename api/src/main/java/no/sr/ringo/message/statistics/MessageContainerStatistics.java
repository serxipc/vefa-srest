package no.sr.ringo.message.statistics;

import java.util.Date;

/**
 * Base class for the statistics for all message containers e.g. Inbox
 * User: andy
 * Date: 9/11/12
 * Time: 1:06 PM
 */
public abstract class MessageContainerStatistics {
    protected final int total;
    protected final int undelivered;
    protected final Date delivered;
    protected final Date received;

    public MessageContainerStatistics(int total, int totalUndelivered, Date lastDelivered, Date lastReceived) {
        this.total = total;
        this.undelivered = totalUndelivered;
        this.delivered = lastDelivered;
        this.received = lastReceived;
    }


    public int getTotal() {
        return total;
    }

    public int getUndelivered() {
        return undelivered;
    }

    public Date getDelivered() {
        return delivered;
    }

    public Date getReceived() {
        return received;
    }

    public abstract String toXml();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageContainerStatistics that = (MessageContainerStatistics) o;

        if (total != that.total) return false;
        if (undelivered != that.undelivered) return false;
        if (delivered != null ? !delivered.equals(that.delivered) : that.delivered != null) return false;
        if (received != null ? !received.equals(that.received) : that.received != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = total;
        result = 31 * result + undelivered;
        result = 31 * result + (delivered != null ? delivered.hashCode() : 0);
        result = 31 * result + (received != null ? received.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MessageContainerStatistics");
        sb.append("{total=").append(total);
        sb.append(", undelivered=").append(undelivered);
        sb.append(", delivered=").append(delivered);
        sb.append(", received=").append(received);
        sb.append('}');
        return sb.toString();
    }
}

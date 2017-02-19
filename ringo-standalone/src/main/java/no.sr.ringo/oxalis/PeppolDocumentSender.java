package no.sr.ringo.oxalis;

import no.difi.vefa.peppol.common.model.Receipt;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.ReceptionId;

import java.net.URI;
import java.util.Date;

/**
 * User: andy
 * Date: 2/1/12
 * Time: 12:45 PM
 */
public interface PeppolDocumentSender {

    /**
     * Sends the document referenced by the message
     *
     * @param message the Message to send
     * @param xmlMessage
     * @return a Receipt containing the UUID and the TimeStamp for the delivery
     */
    TransmissionReceipt sendDocument(MessageMetaData message, String xmlMessage) throws Exception;

    /**
     * Value Object containing
     * the Transmission Timestamp and the UUID of the transfer.
     */
    public static final class TransmissionReceipt {

        private final ReceptionId receptionId;
        private final String remoteAccessPoint;
        private final Date date;
        private final Receipt receipt;

        public TransmissionReceipt(ReceptionId receptionId, URI remoteAccessPoint, Date date, Receipt receipt) {
            this.receptionId = receptionId;
            this.remoteAccessPoint = remoteAccessPoint != null ? remoteAccessPoint.toString() : "n/a";
            this.date = date;
            this.receipt = receipt;
        }

        public ReceptionId getReceptionId() {
            return receptionId;
        }

        public String getRemoteAccessPoint() {
            return remoteAccessPoint;
        }

        public Date getDate() {
            return date;
        }

        public Receipt getReceipt() {
            return receipt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TransmissionReceipt that = (TransmissionReceipt) o;

            if (receptionId != null ? !receptionId.equals(that.receptionId) : that.receptionId != null) return false;
            if (remoteAccessPoint != null ? !remoteAccessPoint.equals(that.remoteAccessPoint) : that.remoteAccessPoint != null)
                return false;
            return date != null ? date.equals(that.date) : that.date == null;
        }

        @Override
        public int hashCode() {
            int result = receptionId != null ? receptionId.hashCode() : 0;
            result = 31 * result + (remoteAccessPoint != null ? remoteAccessPoint.hashCode() : 0);
            result = 31 * result + (date != null ? date.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("TransmissionReceipt");
            sb.append("{messageId=").append(receptionId);
            sb.append(", remoteAccessPoint=").append(remoteAccessPoint);
            sb.append(", date=").append(date);
            sb.append('}');
            return sb.toString();
        }

    }

}

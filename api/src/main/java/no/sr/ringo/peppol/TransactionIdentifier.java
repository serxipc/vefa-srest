/* Created by steinar on 20.05.12 at 13:00 */
package no.sr.ringo.peppol;

/**
 * Type safe value object holding a PEPPOL transaction id  Universal Resource Name (urn)
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class TransactionIdentifier {

    private final String transactionId;

    public TransactionIdentifier(String transactionId) {
        this.transactionId = transactionId;
    }


    public static TransactionIdentifier valueFor(String transactionId) {
        return new TransactionIdentifier(transactionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionIdentifier that = (TransactionIdentifier) o;

        if (!transactionId.equals(that.transactionId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return transactionId.hashCode();
    }

    @Override
    public String toString() {
        return transactionId;
    }

    @Deprecated
    public static class Predefined {
        public static final TransactionIdentifier T001_ORDER = new TransactionIdentifier("urn:www.cenbii.eu:transaction:biicoretrdm001");
        public static final TransactionIdentifier T010_INVOICE_V1 = new TransactionIdentifier("urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0");
        public static final TransactionIdentifier T014_CREDIT_NOTE_V1 = new TransactionIdentifier("urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0");

        /** Issued together with a credit invoice in order to correct a previous erroneous invoice (T10) */
        public static final TransactionIdentifier T015_INVOICE_CORRECTIVE_V1 = new TransactionIdentifier("urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0");
        public static final TransactionIdentifier T017_CREDIT_NOTE_V1 = new TransactionIdentifier("urn:www.cenbii.eu:transaction:biicoretrdm017:ver1.0");
    }

}

package no.sr.ringo.transport;

/**
 * @author steinar
 *         Date: 19.02.2017
 *         Time: 11.49
 */
public class TransmissionId {

    private final String transmissionId;

    public TransmissionId(String transmissionId) {

        this.transmissionId = transmissionId;
    }


    public String stringValue() {
        return transmissionId;
    }

    @Override
    public String toString() {
        return stringValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransmissionId that = (TransmissionId) o;

        return transmissionId.equals(that.transmissionId);
    }

    @Override
    public int hashCode() {
        return transmissionId.hashCode();
    }
}

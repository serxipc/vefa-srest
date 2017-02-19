package no.sr.ringo.message;

import java.util.UUID;

/**
 * Represents the unique identification of any inbound or outbound message received by this access point.
 *
 * This identifier is globally unique and may as such be used in clusters without a common database holding
 * unique integer identificatiors.
 *
 * @author steinar
 *         Date: 17.02.2017
 *         Time: 10.23
 */
public class ReceptionId {

    UUID value;

    public ReceptionId() {
        value = UUID.randomUUID();
    }


    public ReceptionId(String uuid) {
        value = UUID.fromString(uuid);
    }

    public UUID getValue() {
        return value;
    }

    public String stringValue() {
        return value.toString();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReceptionId that = (ReceptionId) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

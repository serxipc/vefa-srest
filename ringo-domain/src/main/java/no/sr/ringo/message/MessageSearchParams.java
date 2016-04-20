package no.sr.ringo.message;

import eu.peppol.identifier.ParticipantId;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 1:29 PM
 */
public interface MessageSearchParams {
    Integer getPageIndex();

    TransferDirection getDirection();

    ParticipantId getSender();

    ParticipantId getReceiver();

    String getSent();

    DateCondition getDateCondition();

    /**
     * Enum representing search condition
     */
    public enum DateCondition {
        EQUAL("="), LESS("<"), GREATER(">"), GREATER_EQUAL(">="), LESS_EQUAL("<=");

        private final String value;

        private DateCondition(String value) {
            this.value = value;
        }

        public static DateCondition fromString(String string) {
            DateCondition result = null;
            for (DateCondition dc : DateCondition.values()) {
                if (dc.value.equals(string)) {
                    result = dc;
                }
            }
            if (result != null) {
                return result;
            } else {
                throw new IllegalArgumentException(String.format("Invalid date condition %s", string));
            }
        }

        public String getValue() {
            return value;
        }

    }
}

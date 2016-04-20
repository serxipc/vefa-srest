package no.sr.ringo.account;

/**
 * User: Adam
 * Date: 4/4/12
 * Time: 11:58 AM
 */
public class ValidationResult {

    private final boolean valid;
    private final String message;

    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}

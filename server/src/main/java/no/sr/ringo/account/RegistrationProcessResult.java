package no.sr.ringo.account;

/**
 * User: Adam
 * Date: 4/4/12
 * Time: 11:58 AM
 */
public class RegistrationProcessResult {


    private final boolean success;
    private final String message;
    private final RegistrationSource source;

    public RegistrationProcessResult(RegistrationSource source, boolean success, String message) {
        this.success = success;
        this.message = message;
        this.source = source;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getSource() {
        return source.name();
    }
    public enum RegistrationSource{
        RINGO, DIFI
    }
}

package no.sr.ringo.peppol;

/**
 * Thrown when the peppol header is not valid
 */
public class InvalidPeppolHeaderException extends IllegalStateException {
    private final PeppolHeader peppolHeader;

    public InvalidPeppolHeaderException(PeppolHeader peppolHeader) {
        this.peppolHeader = peppolHeader;
    }

    public PeppolHeader getPeppolHeader() {
        return peppolHeader;
    }

    @Override
    public String getMessage() {
        return peppolHeader.toString();
    }
}

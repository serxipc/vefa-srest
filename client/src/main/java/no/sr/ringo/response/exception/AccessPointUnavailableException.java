package no.sr.ringo.response.exception;

/**
 * User: andy
 * Date: 2/24/12
 * Time: 8:54 AM
 */
public class AccessPointUnavailableException extends RuntimeException {
    private final int executionCount;

    /**
     * The number of times the request was made.
     * @param executionCount
     * @param exception
     */
    public AccessPointUnavailableException(int executionCount, AccessPointTemporarilyUnavailableException exception) {
        super("Unable to automatically recover from the SendRegning Access point being temporarily down", exception);
        this.executionCount = executionCount;
    }

    public int getExecutionCount() {
        return executionCount;
    }
}

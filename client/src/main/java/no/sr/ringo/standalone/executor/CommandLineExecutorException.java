package no.sr.ringo.standalone.executor;

import no.sr.ringo.exception.NotifyingException;

/**
 * User: Adam
 * Date: 1/31/12
 * Time: 9:50 AM
 */
public class CommandLineExecutorException extends NotifyingException {

    public CommandLineExecutorException(String message) {
        super(message);
    }
    public CommandLineExecutorException(String message, NotificationType notificationType) {
        super(message, notificationType);
    }
}

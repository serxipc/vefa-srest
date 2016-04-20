package no.sr.ringo.standalone.parser;

import no.sr.ringo.exception.NotifyingException;

/**
 * User: Adam
 * Date: 1/31/12
 * Time: 9:50 AM
 */
public class CommandLineParserException extends NotifyingException{

    public CommandLineParserException(String message) {
        super(message);
    }

    public CommandLineParserException(String message, NotificationType notificationType) {
        super(message, notificationType);
    }

}

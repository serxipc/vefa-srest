package no.sr.ringo.client;

import no.sr.ringo.exception.NotifyingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Adam
 * Date: 4/10/13
 * Time: 12:55 PM
 */
public class ErrorNotificationData {
    private final String commandLine;
    private final NotifyingException.NotificationType notificationType;
    private final String errorMessage;

    public ErrorNotificationData(String commandLine, NotifyingException.NotificationType notificationType, String errorMessage) {
        this.commandLine = commandLine;
        this.notificationType = notificationType;
        this.errorMessage = errorMessage;
    }

    public String getCommandLine() {
        return maskPassword(commandLine);
    }

    public NotifyingException.NotificationType getNotificationType() {
        return notificationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String maskPassword(String commandLine) {
        Pattern pat = Pattern.compile("(--password\\s*|-p\\s*)([^\\s]*)(.*)");
        Matcher mat = pat.matcher(commandLine);
        if (mat.find()) {
            return mat.replaceAll("$1*****$3");
        }
        return commandLine;
    }

}

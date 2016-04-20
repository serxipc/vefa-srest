package no.sr.ringo.exception;

/**
 * When this exception is caught and notify is set to true
 * email notification will be send
 * User: Adam
 * Date: 4/9/13
 * Time: 11:28 AM
 */
public class NotifyingException extends Exception {
    public enum NotificationType {
        BATCH_UPLOAD, DOWNLOAD
    }

    private NotificationType notificationType;

    public NotifyingException(String message, NotificationType notificationType) {
        super(message);
        this.notificationType = notificationType;
    }

    /*
     * Calling constructor without notification type will not cause notification to be sent
     */
    public NotifyingException(String message) {
        super(message);
        this.notificationType = null;
    }

    public boolean isNotify() {
        return notificationType != null;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }
}

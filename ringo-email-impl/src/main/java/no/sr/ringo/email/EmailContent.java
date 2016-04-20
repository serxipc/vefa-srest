package no.sr.ringo.email;

/**
 * Defines the email content when sending emails in old Ringo SR
 *
 * @author thore
 */
public interface EmailContent {

    String getSubject();

    String getBody();

    String getContentType();

    boolean isMultipart();

}

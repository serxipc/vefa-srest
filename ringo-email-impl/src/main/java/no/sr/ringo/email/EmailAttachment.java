package no.sr.ringo.email;

import java.io.InputStream;

/**
 * Defines the email attachment when sending emails in old Ringo SR
 *
 * @author thore
 */
public interface EmailAttachment {

    String getFileName();

    String getContentType();

    InputStream getInputStream();

}

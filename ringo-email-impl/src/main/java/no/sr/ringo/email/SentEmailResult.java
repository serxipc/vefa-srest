package no.sr.ringo.email;

/**
 * Defines the result after sending emails inherited from old Ringo SR
 *
 * @author thore
 */
public interface SentEmailResult {

    EmailAddress getFrom();

    EmailAddress getReplyTo();

    EmailAddress getSender();

    EmailAddress getRecipient();

    EmailContent getContent();

    EmailAttachment getAttachment();

}

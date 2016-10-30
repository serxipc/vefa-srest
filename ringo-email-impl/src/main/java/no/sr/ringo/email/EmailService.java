package no.sr.ringo.email;

import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.message.MessageNumber;

/**
 * Defines the email functionality used by the old Ringo SR.
 *
 * Consider to refactor this into a standard notification service,
 * allowing to customers to be alerted in multiple ways.
 *
 * @author thore
 */
public interface EmailService {

    /**
     * Sends usage report to the given email address
     */
    void sendReport(String recipientEmailAddress, String result);

    /**
     * Sends information to sales department whenever new user has registered
     * so that the sales department will contact new customer with correct contract
     */
    SentEmailResult sendRegistrationNotification(Account account, String billingCode);

    //
    // Used to alert the customer (owner of the Account) by mail (of situations that need attention)
    //

    /**
     * Alerts the REST API customer that there was an error when he tried to upload a new message
     */
    SentEmailResult sendUploadErrorNotification(Account account, String errorMessage, String filename);

    /**
     * Alerts the REST API customer that there we were unable to deliver the message
     */
    SentEmailResult sendProcessingErrorNotification(Account account, String errorMessage, MessageNumber messageNumber);

    /**
     * Alerts the Ringo Client customer that there we were trouble uploading one or more files
     */
    SentEmailResult sendClientBatchUploadErrorNotification(Account account, String commandLine, String errorMessage);

    /**
     * Alerts the Ringo Client customer that there we were trouble downloading one or more files
     */
    SentEmailResult sendClientDownloadErrorNotification(Account account, String commandLine, String errorMessage);

}

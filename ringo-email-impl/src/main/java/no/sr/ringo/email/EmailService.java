package no.sr.ringo.email;

import no.sr.ringo.account.RingoAccount;
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
    SentEmailResult sendRegistrationNotification(RingoAccount ringoAccount, String billingCode);

    //
    // Used to alert the customer (owner of the RingoAccount) by mail (of situations that need attention)
    //

    /**
     * Alerts the REST API customer that there was an error when he tried to upload a new message
     */
    SentEmailResult sendUploadErrorNotification(RingoAccount ringoAccount, String errorMessage, String filename);

    /**
     * Alerts the REST API customer that there we were unable to deliver the message
     */
    SentEmailResult sendProcessingErrorNotification(RingoAccount ringoAccount, String errorMessage, MessageNumber messageNumber, String invoiceNumber);

    /**
     * Alerts the Ringo Client customer that there we were trouble uploading one or more files
     */
    SentEmailResult sendClientBatchUploadErrorNotification(RingoAccount ringoAccount, String commandLine, String errorMessage);

    /**
     * Alerts the Ringo Client customer that there we were trouble downloading one or more files
     */
    SentEmailResult sendClientDownloadErrorNotification(RingoAccount ringoAccount, String commandLine, String errorMessage);

}

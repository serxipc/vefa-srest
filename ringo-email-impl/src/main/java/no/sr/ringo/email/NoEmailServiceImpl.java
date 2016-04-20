package no.sr.ringo.email;

import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.message.MessageNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation that do not send any mails at all, just logs the situation as error.
 *
 * @author thore
 */
public class NoEmailServiceImpl implements EmailService {

    static final Logger log = LoggerFactory.getLogger(NoEmailServiceImpl.class);

    @Override
    public void sendReport(String recipientEmailAddress, String result) {
        log.error("Mail not implemented - no mail was sent to {}", recipientEmailAddress);
    }

    @Override
    public SentEmailResult sendRegistrationNotification(RingoAccount ringoAccount, String billingCode) {
        log.error("Mail not implemented - no mail was sent to sales department for account {} and billingCode {}", ringoAccount.getUserName().stringValue(), billingCode);
        return null;
    }

    @Override
    public SentEmailResult sendUploadErrorNotification(RingoAccount ringoAccount, String errorMessage, String filename) {
        log.error("Mail not implemented - no upload error notification was sent to user {} for file {} and error {}", ringoAccount.getUserName().stringValue(), filename, errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendProcessingErrorNotification(RingoAccount ringoAccount, String errorMessage, MessageNumber messageNumber, String invoiceNumber) {
        log.error("Mail not implemented - no processing error notification was sent to user {} for message number {} and error {}", ringoAccount.getUserName().stringValue(), messageNumber.getValue(), errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendClientBatchUploadErrorNotification(RingoAccount ringoAccount, String commandLine, String errorMessage) {
        log.error("Mail not implemented - no Ringo Client batch upload notification was sent to user {} for command {} and error {}", ringoAccount.getUserName().stringValue(), commandLine, errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendClientDownloadErrorNotification(RingoAccount ringoAccount, String commandLine, String errorMessage) {
        log.error("Mail not implemented - no Ringo Client download error notification was sent to user {} for command {} and error {}", ringoAccount.getUserName().stringValue(), commandLine, errorMessage);
        return null;
    }

}

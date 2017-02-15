package no.sr.ringo.email;

import no.sr.ringo.account.Account;
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
    public SentEmailResult sendRegistrationNotification(Account account, String info) {
        log.error("Mail not implemented - no mail was sent to sales department for account {} and info {}", account.getUserName().stringValue(), info);
        return null;
    }

    @Override
    public SentEmailResult sendUploadErrorNotification(Account account, String errorMessage, String filename) {
        log.error("Mail not implemented - no upload error notification was sent to user {} for file {} and error {}", account.getUserName().stringValue(), filename, errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendProcessingErrorNotification(Account account, String errorMessage, MessageNumber messageNumber) {
        log.error("Mail not implemented - no processing error notification was sent to user {} for message number {} and error {}", account.getUserName().stringValue(), messageNumber.getValue(), errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendClientBatchUploadErrorNotification(Account account, String commandLine, String errorMessage) {
        log.error("Mail not implemented - no Ringo Client batch upload notification was sent to user {} for command {} and error {}", account.getUserName().stringValue(), commandLine, errorMessage);
        return null;
    }

    @Override
    public SentEmailResult sendClientDownloadErrorNotification(Account account, String commandLine, String errorMessage) {
        log.error("Mail not implemented - no Ringo Client download error notification was sent to user {} for command {} and error {}", account.getUserName().stringValue(), commandLine, errorMessage);
        return null;
    }

}

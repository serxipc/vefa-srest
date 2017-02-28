/* Created by steinar on 01.01.12 at 17:54 */
package no.sr.ringo.usecase;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.account.Account;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.message.*;
import no.sr.ringo.persistence.queue.OutboundMessageQueueId;
import no.sr.ringo.persistence.queue.QueueRepository;
import no.sr.ringo.resource.InvalidUserInputWebException;
import no.sr.ringo.xml.EhfEntityExtractor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.ws.rs.WebApplicationException;

/**
 * Complete use case which will verify the supplied parameters and persistOutboundMessage the message.
 * This object is stateful in request scope
 *
 * @author Steinar Overbeck Cook
 */
@RequestScoped
public class ReceiveMessageFromClientUseCase {

    static final Logger log = LoggerFactory.getLogger(ReceiveMessageFromClientUseCase.class);

    private final Account account;
    private final PeppolMessageRepository messageRepository;
    private final QueueRepository queueRepository;
    private PeppolMessage peppolMessage;
    private OutboundPostParams postParams;
    private UploadMode uploadMode;
    private final EmailService emailService;
    private PeppolMessageCreator peppolMessageCreator;
    private PeppolMessageValidator peppolMessageValidator;


    @Inject
    ReceiveMessageFromClientUseCase(Account account, PeppolMessageRepository messageRepository, QueueRepository queueRepository, EmailService emailService) {
        this.account = account;
        this.messageRepository = messageRepository;
        this.queueRepository = queueRepository;
        this.emailService = emailService;
    }

    /**
     * Creates PeppolMessage and validates it, extracts invoiceNo  and persists message + the queue.
     * If anything goes wrong in case of batch upload, message notification is sent and InvalidUserInputWebException is rethrown
     * setting 400 in response.
     */
    public MessageWithLocations handleMessage(OutboundPostParams postParams) {
        this.postParams = postParams;

        extractUploadMode();

        try {

            //it's important to fail fast, that's why extraction and validation is split into 2 parts.
            //First we extract + validate header and then extract + validate the xml document

            extractHeader(postParams);

            validateHeader(postParams);

            extractDocument();

            validateDocument();

        } catch (InvalidUserInputWebException webException) {
            if (UploadMode.BATCH.equals(uploadMode)) {
                handleInvalidInputException(webException);
            }
            throw webException;
        }

        MessageWithLocations messageWithLocations = persistOutboundMessage(account);

        // Places the message number into the outbound queue
        queueMessage(messageWithLocations);

        return messageWithLocations;
    }

    private void validateDocument() {
        if (account.isValidateUpload()) {
            peppolMessageValidator.validateDocument();
        }
    }

    private void extractDocument() {
        peppolMessage = peppolMessageCreator.extractDocument();
    }

    private void validateHeader(OutboundPostParams postParams) {
        peppolMessageValidator = new PeppolMessageValidator(peppolMessage, postParams);
        peppolMessageValidator.validateHeader();
    }

    private void extractHeader(OutboundPostParams postParams) {
        peppolMessageCreator = new PeppolMessageCreator(account, postParams);
        try {
            peppolMessage = peppolMessageCreator.extractHeader();
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }


    /**
     * Store message in db, even though it might not be valid
     */
    private MessageWithLocations persistOutboundMessage(Account account) {

        if (peppolMessage == null) {
            throw new IllegalStateException("Tried to persist message which doesn't exist.");
        }

        // Saves the message into the database
        return messageRepository.persistOutboundMessage(account, peppolMessage);
    }

    /**
     * As message is valid, we put it on the queue
     */
    protected OutboundMessageQueueId queueMessage(MessageWithLocations message) {
        return queueRepository.putMessageOnQueue(message.getMsgNo().toLong());
    }

    protected String extractInvoiceNoFromDocument(Document document) {
        EhfEntityExtractor ehfEntityExtractor = new EhfEntityExtractor(document);
        String result = null;
        try {
            result = ehfEntityExtractor.extractInvoiceNo();
        } catch (Exception e) {
            log.error("Unable to retrieve InvoiceNo, continue processing", e);
        }

        if (result == null) {
            log.warn(String.format("InvoiceNo not extracted from xml document with msg_no = %d", peppolMessage.getMsgNo()));
        }

        return result;
    }

    /**
     * Tries to extractHeader uploadMode
     */
    private void extractUploadMode() {
        if (StringUtils.isNotBlank(postParams.getUploadMode())) {
            try {
                this.uploadMode = UploadMode.valueOf(postParams.getUploadMode());
            } catch (Exception e) {
                //defaults to false line below
            }
        }
    }

    /**
     * Sends email notification informing about exception situation that has occurred
     */
    private void handleInvalidInputException(InvalidUserInputWebException webException) {
        emailService.sendUploadErrorNotification(account, webException.getMessage(), postParams.getFilename());
    }

}

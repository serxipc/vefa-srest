package no.sr.ringo.client;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.common.MessageContainer;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.document.ClientPeppolDocument;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.response.Navigation;
import no.sr.ringo.response.RingoResponseHandler;
import no.sr.ringo.smp.AcceptedDocumentTransfer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * The methods available when communicating with the RingoRestServer.
 *
 * User: andy
 * Date: 1/27/12
 * Time: 11:07 AM
 */
public interface RingoService {

    /**
     * Marks a message as read on the ringo server.
     * @param message
     * @return
     */
    boolean markAsRead(MessageWithLocations message);

    /**
     * Fetches the next group of 25 messages based on the navigation provided
     *
     * @param navigation
     * @param ringoResponseHandler
     * @return
     */
    Messages next(Navigation navigation, RingoResponseHandler<? extends Messages> ringoResponseHandler);

    /**
     * Fetches the number of messages in the container.
     * @param messageContainer The object which represents a collection of messages e.g. Inbox, MessageBox
     * @return
     */
    Integer count(MessageContainer messageContainer);

    /**
     * Fetches the messages for the container.
     *
     * @param messageContainer The object which represents a collection of messages e.g. Inbox, MessageBox
     * @param messagesResponseHandler
     * @return
     */
    Messages messages(MessageContainer messageContainer, RingoResponseHandler<? extends Messages> messagesResponseHandler);

    /**
     * Sends the document to Ringo server using the SRest protocol.
     *
     * @param peppolDocument The document to send
     * @param peppolHeader The peppol headers to use when sending the document via PEPPOL
     * @param uploadMode
     * @return
     */
    Message sendDocument(ClientPeppolDocument peppolDocument, PeppolHeader peppolHeader, UploadMode uploadMode);

    /**
     * When creating a Peppol document one needs to know the Document and process ids that the recipient supports.
     * So that the generated Peppol Document can use these values. Also The PeppolHeader needs to contain
     * both the DocumentId and the ProcessId which is required when sending the document.
     *
     * @param peppolParticipantId  The participant id of the recipient to check
     * @param localName the local name of the document e.g. Invoice,Order,CreditNote
     * @return a List of Objects containing the DocumentId and ProcessId combination
     */
    List<AcceptedDocumentTransfer> fetchAcceptedDocumentTransfers(ParticipantId peppolParticipantId, LocalName localName);

    /**
     * Determines if the participant is registered on the PEPPOL network
     *
     * @param peppolParticipantId The participant id of the recipient to check
     * @return true if the participant is registered false otherwise
     */
    boolean isParticipantRegistered(ParticipantId peppolParticipantId);

    /**
     * Downloads the message to the output stream provided
     * @param message the message to download
     * @param outputStream the stream to write the document to. It will be closed when completed
     */
    void downloadMessage(MessageWithLocations message, OutputStream outputStream) throws IOException;


    Boolean sendErrorNotification(ErrorNotificationData errorNotificationData);
}

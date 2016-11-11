package no.sr.ringo.client;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.smp.AcceptedDocumentTransfer;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * The interface describing the RingoRestClient
 *
 * User: andy
 * Date: 1/27/12
 * Time: 2:19 PM
 */
public interface RingoClient {


    /**
     * Fetches the inbox which contains all incoming messages that have not yet been read.
     * @return
     */
    Inbox getInbox();

    /**
     * Determines if the participant is registered on the PEPPOL network
     *
     * @param peppolParticipantId The participant id of the recipient to check
     * @return true if the participant is registered false otherwise
     */
    boolean isParticipantRegistered(ParticipantId peppolParticipantId);

    /**
     * Sends the given file using the peppol network.
     * <p/>
     * The peppol document type and process type are determined from the contents of the file
     *
     * @param file              The Xml file to upload
     * @param peppolChannelId   The id of the channel
     * @param senderIdPeppol    The participant id of the document sender the document. Will be extracted from xml file if not specified.
     * @param recipientIdPeppol The participant id of the recipient.  Will be extracted from xml file if not specified.
     * @param uploadMode        Single or batch upload mode. To inform the server whether errors should be reported
     * @return the Message indicating that the file was uploaded
     */
    Message send(File file, PeppolChannelId peppolChannelId, ParticipantId senderIdPeppol, ParticipantId recipientIdPeppol, UploadMode uploadMode);

    /**
     * Sends the document provided by the inputStream using the peppol network. Using a Stream helps to reduce the memory footprint
     * required to send a document.
     *
     * This method is used by SendRegning application.
     *
     * @param inputStream
     * @param peppolHeader
     * @return the Message indicating that the file was uploaded
     */
    Message send(InputStream inputStream, PeppolHeader peppolHeader);


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
}

package no.sr.ringo.standalone.parser;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.peppol.PeppolChannelId;

import java.io.File;

/**
 * Holds RingoClient execution params parsed from command line arguments
 * <p/>
 * User: adam
 * Date: 1/27/12
 * Time: 8:08 AM
 */
public class RingoClientParams {

    //path from which files will be uploaded (folder or a file)
    private File outboxPath;

    //path to which files will be downloaded
    private File inboxPath;

    //path where files will be moved after successful upload
    private File archivePath;

    //operation user wants to perform (download, upload, SMP lookup)
    private ClientOperation operation;

    //participantID used for SMP lookup
    private ParticipantId peppolParticipantId;

    //ChannelId used when uploading files
    private PeppolChannelId peppolChannelId;

    //participantID of sender when uploading file
    private ParticipantId senderIdPeppol;

    //participantId of recipient when uploading single gile
    private ParticipantId recipientIdPeppol;


    public File getOutboxPath() {

        return outboxPath;
    }

    /**
     * Enum representing search condition
     */
    public enum ClientOperation {
        UPLOAD, DOWNLOAD, UPLOAD_SINGLE, SMP_LOOKUP;
        
        /**
         * Returns a string consisting enum values
         */
        public static String listValues() {
            StringBuffer sb = new StringBuffer();
            for (ClientOperation o : values()) {
                sb.append(o.name()).append(" ");
            }

            return sb.toString();
        }
    }


    /**
     * Getters and setters
     */

    public File getInboxPath() {
        return inboxPath;
    }

    public File getArchivePath() {
        return archivePath;
    }

    public ClientOperation getOperation() {
        return operation;
    }

    public ParticipantId getParticipantId() {
        return peppolParticipantId;
    }

    public PeppolChannelId getChannelId() {
        return peppolChannelId;
    }

    public ParticipantId getSenderId() {
        return senderIdPeppol;
    }

    public void setOutboxPath(File outboxPath) {
        this.outboxPath = outboxPath;
    }

    public void setInboxPath(File inboxPath) {
        this.inboxPath = inboxPath;
    }

    public void setArchivePath(File archivePath) {
        this.archivePath = archivePath;
    }

    public void setOperation(ClientOperation operation) {
        this.operation = operation;
    }

    public void setParticipantId(ParticipantId peppolParticipantId) {
        this.peppolParticipantId = peppolParticipantId;
    }

    public void setChannelId(PeppolChannelId peppolChannelId) {
        this.peppolChannelId = peppolChannelId;
    }

    public void setSenderId(ParticipantId senderIdPeppol) {
        this.senderIdPeppol = senderIdPeppol;
    }

    public ParticipantId getRecipientId() {
        return recipientIdPeppol;
    }

    public void setRecipientId(ParticipantId recipientIdPeppol) {
        this.recipientIdPeppol = recipientIdPeppol;
    }

}

/* Created by steinar on 08.01.12 at 19:49 */
package no.sr.ringo.peppol;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class PeppolHeader {
    private ParticipantIdentifier sender;
    private ParticipantIdentifier receiver;
    private PeppolChannelId peppolChannel;
    private DocumentTypeIdentifier peppolDocumentTypeId;
    private ProcessIdentifier profileId;

    public ParticipantIdentifier getSender() {
        return sender;
    }

    public void setSender(ParticipantIdentifier sender) {
        this.sender = sender;
    }

    public ParticipantIdentifier getReceiver() {
        return receiver;
    }

    public void setReceiver(ParticipantIdentifier receiver) {
        this.receiver = receiver;
    }

    public PeppolChannelId getPeppolChannelId() {
        return peppolChannel;
    }

    public void setPeppolChannelId(PeppolChannelId peppolChannel) {
        this.peppolChannel = peppolChannel;
    }

    public DocumentTypeIdentifier getPeppolDocumentTypeId() {
        return peppolDocumentTypeId;
    }

    public void setDocumentTypeIdentifier(DocumentTypeIdentifier peppolDocumentTypeId) {
        this.peppolDocumentTypeId = peppolDocumentTypeId;
    }

    public ProcessIdentifier getProcessIdentifier() {
        return profileId;
    }

    public void setProcessIdentifier(ProcessIdentifier profileId) {
        this.profileId = profileId;
    }

    public void validate() {
        if (peppolChannel == null || receiver == null || sender == null || peppolDocumentTypeId == null || profileId == null) {
            throw new InvalidPeppolHeaderException(this);
        }
    }

    /**
     * Creates a valid peppol header for the provided details
     * @return
     * @param peppolDocumentTypeId
     * @param sender
     * @param receiver
     */
    public static PeppolHeader forDocumentType(DocumentTypeIdentifier peppolDocumentTypeId, ProcessIdentifier processIdentifier, ParticipantIdentifier sender, ParticipantIdentifier receiver) {
        PeppolHeader result = new PeppolHeader();
        result.setDocumentTypeIdentifier(peppolDocumentTypeId);
        result.setProcessIdentifier(processIdentifier);
        result.setPeppolChannelId(new PeppolChannelId(ChannelProtocol.SREST.name()));
        result.setReceiver(receiver);
        result.setSender(sender);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeppolHeader that = (PeppolHeader) o;

        if (!sender.equals(that.sender)) return false;
        if (!receiver.equals(that.receiver)) return false;
        if (!peppolChannel.equals(that.peppolChannel)) return false;
        if (!peppolDocumentTypeId.equals(that.peppolDocumentTypeId)) return false;
        return profileId.equals(that.profileId);
    }

    @Override
    public int hashCode() {
        int result = sender.hashCode();
        result = 31 * result + receiver.hashCode();
        result = 31 * result + peppolChannel.hashCode();
        result = 31 * result + peppolDocumentTypeId.hashCode();
        result = 31 * result + profileId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PeppolHeader{");
        sb.append("sender=").append(sender);
        sb.append(", receiver=").append(receiver);
        sb.append(", peppolChannel=").append(peppolChannel);
        sb.append(", peppolDocumentTypeId=").append(peppolDocumentTypeId);
        sb.append(", profileId=").append(profileId);
        sb.append('}');
        return sb.toString();
    }
}

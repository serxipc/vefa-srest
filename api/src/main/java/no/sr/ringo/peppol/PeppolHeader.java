/* Created by steinar on 08.01.12 at 19:49 */
package no.sr.ringo.peppol;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.cenbiimeta.ProfileId;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class PeppolHeader {
    private ParticipantId sender;
    private ParticipantId receiver;
    private PeppolChannelId peppolChannel;
    private PeppolDocumentTypeId peppolDocumentTypeId;
    private ProfileId profileId;

    @Deprecated
    private PeppolDocumentIdAcronym peppolDocumentId;

    @Deprecated
    private PeppolProcessIdAcronym peppolProcessIdAcronym;

    public ParticipantId getSender() {
        return sender;
    }

    public void setSender(ParticipantId sender) {
        this.sender = sender;
    }

    public ParticipantId getReceiver() {
        return receiver;
    }

    public void setReceiver(ParticipantId receiver) {
        this.receiver = receiver;
    }

    public PeppolChannelId getPeppolChannelId() {
        return peppolChannel;
    }

    public void setPeppolChannelId(PeppolChannelId peppolChannel) {
        this.peppolChannel = peppolChannel;
    }

    public PeppolDocumentTypeId getPeppolDocumentTypeId() {
        return peppolDocumentTypeId;
    }

    public void setPeppolDocumentTypeId(PeppolDocumentTypeId peppolDocumentTypeId) {
        this.peppolDocumentTypeId = peppolDocumentTypeId;
        this.peppolDocumentId = PeppolDocumentIdAcronym.fromLocalName(peppolDocumentTypeId.getLocalName());
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    public void setProfileId(ProfileId profileId) {
        this.profileId = profileId;
        this.peppolProcessIdAcronym = PeppolProcessIdAcronym.valueFor(profileId.toString());
    }

    public void validate() {
        if (peppolChannel == null || receiver == null || sender == null || peppolDocumentTypeId == null || profileId == null) {
            throw new InvalidPeppolHeaderException(this);
        }
    }

    @Deprecated
    public PeppolDocumentIdAcronym getPeppolDocumentIdAcronym() {
        return peppolDocumentId;
    }

    @Deprecated
    public PeppolProcessIdAcronym getPeppolProcessIdAcronym() {
        return peppolProcessIdAcronym;
    }

    /**
     * Creates a valid peppol header for the provided details
     * @return
     * @param peppolDocumentTypeId
     * @param sender
     * @param receiver
     */
    public static PeppolHeader forDocumentType(PeppolDocumentTypeId peppolDocumentTypeId, ParticipantId sender, ParticipantId receiver) {
        PeppolHeader result = new PeppolHeader();
        result.setPeppolDocumentTypeId(peppolDocumentTypeId);

        ProfileIdTranslator profileIdTranslator = new ProfileIdTranslator();

        ProfileId cenBiiProfileId = profileIdTranslator.translateToCenBiiProfile(peppolDocumentTypeId.getCustomizationIdentifier().getPeppolExtensionIdentifier());
        result.setProfileId(cenBiiProfileId);

        result.setPeppolChannelId(new PeppolChannelId("SendRegning"));
        result.setReceiver(receiver);
        result.setSender(sender);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeppolHeader that = (PeppolHeader) o;

        if (peppolChannel != null ? !peppolChannel.equals(that.peppolChannel) : that.peppolChannel != null) return false;
        if (peppolDocumentId != that.peppolDocumentId) return false;
        if (peppolDocumentTypeId != null ? !peppolDocumentTypeId.equals(that.peppolDocumentTypeId) : that.peppolDocumentTypeId != null) return false;
        if (peppolProcessIdAcronym != that.peppolProcessIdAcronym) return false;
        if (profileId != null ? !profileId.equals(that.profileId) : that.profileId != null) return false;
        if (receiver != null ? !receiver.equals(that.receiver) : that.receiver != null) return false;
        if (sender != null ? !sender.equals(that.sender) : that.sender != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sender != null ? sender.hashCode() : 0;
        result = 31 * result + (receiver != null ? receiver.hashCode() : 0);
        result = 31 * result + (peppolChannel != null ? peppolChannel.hashCode() : 0);
        result = 31 * result + (peppolDocumentTypeId != null ? peppolDocumentTypeId.hashCode() : 0);
        result = 31 * result + (profileId != null ? profileId.hashCode() : 0);
        result = 31 * result + (peppolDocumentId != null ? peppolDocumentId.hashCode() : 0);
        result = 31 * result + (peppolProcessIdAcronym != null ? peppolProcessIdAcronym.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PeppolHeader");
        sb.append("\n{ sender=").append(sender);
        sb.append(",\n  receiver=").append(receiver);
        sb.append(",\n  peppolChannel=").append(peppolChannel);
        sb.append(",\n  peppolDocumentTypeId=").append(peppolDocumentTypeId);
        sb.append(",\n  profileId=").append(profileId);
        sb.append(",\n  peppolDocumentId=").append(peppolDocumentId);
        sb.append(",\n  peppolProcessIdAcronym=").append(peppolProcessIdAcronym);
        sb.append("\n}");
        return sb.toString();
    }
}

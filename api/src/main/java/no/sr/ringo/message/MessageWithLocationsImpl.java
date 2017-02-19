/* Created by steinar on 08.01.12 at 20:18 */
package no.sr.ringo.message;

import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.transport.TransferDirection;
import no.sr.ringo.transport.TransmissionId;

import java.net.URI;
import java.util.Date;

/**
 * Decorates a {@link MessageMetaData} object and adds two more methods
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class MessageWithLocationsImpl implements MessageWithLocations {

    private URI self;
    private URI xmlDocument;
    private MessageMetaData messageMetaData =  new MessageMetaDataImpl();

    public MessageWithLocationsImpl() {
    }

    public MessageWithLocationsImpl(MessageMetaData messageMetaData, URI self, URI xmlDocument) {
        this.messageMetaData = messageMetaData;
        this.self = self;
        this.xmlDocument = xmlDocument;
    }

    public MessageWithLocationsImpl(MessageMetaDataImpl messageMetaData) {
        this.messageMetaData = messageMetaData;
    }

    public URI getSelfURI() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getXmlDocumentURI() {
        return xmlDocument;
    }

    public void setXmlDocument(URI xmlDocument) {
        this.xmlDocument = xmlDocument;
    }


    public TransferDirection getTransferDirection() {
        return messageMetaData.getTransferDirection();
    }

    public Date getReceived() {
        return messageMetaData.getReceived();
    }

    public Date getDelivered() {
        return messageMetaData.getDelivered();
    }

    public PeppolHeader getPeppolHeader() {
        return messageMetaData.getPeppolHeader();
    }

    public Long getMsgNo() {
        return messageMetaData.getMsgNo();
    }

    public TransmissionId getTransmissionId() {
        return messageMetaData.getTransmissionId();
    }

    @Override
    public ReceptionId getReceptionId() {
        return messageMetaData.getReceptionId();
    }


    public void setMessageMetaData(MessageMetaData messageMetaData) {
        this.messageMetaData = messageMetaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageWithLocationsImpl that = (MessageWithLocationsImpl) o;

        if (messageMetaData != null ? !messageMetaData.equals(that.messageMetaData) : that.messageMetaData != null) return false;
        if (self != null ? !self.equals(that.self) : that.self != null) return false;
        if (xmlDocument != null ? !xmlDocument.equals(that.xmlDocument) : that.xmlDocument != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = self != null ? self.hashCode() : 0;
        result = 31 * result + (xmlDocument != null ? xmlDocument.hashCode() : 0);
        result = 31 * result + (messageMetaData != null ? messageMetaData.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PeppolMessageResultWithLocators");
        sb.append("{self=").append(self);
        sb.append(", xmlDocument=").append(xmlDocument);
        sb.append(", PeppolMessageResult=").append(messageMetaData);
        sb.append('}');
        return sb.toString();
    }

}

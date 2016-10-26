package no.sr.ringo.response.xml;

/**
 * User: andy
 * Date: 1/27/12
 * Time: 3:21 PM
 */

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.RingoUtils;
import no.sr.ringo.common.XmlSpecification;
import no.sr.ringo.message.MessageMetaDataImpl;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.MessageWithLocationsImpl;
import no.sr.ringo.message.TransferDirection;
import no.sr.ringo.peppol.*;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.xpath.XPath;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * XML specification which parses and creates {@link MessageWithLocations} objects from an XML Element.
 *
 * @author andy andy@sendregning.no
 */
public class MessageXmlSpec implements XmlSpecification<MessageWithLocations> {

    /**
     * Creates the specification.
     */
    public MessageXmlSpec() {

    }

    /**
     * the name of the entity, this is used only for logging.
     *
     * @return
     */
    public String getName() {
        return "message";
    }

    /**
     * The xpath expression used to select node/nodes
     *
     * @return
     */
    public String getXPath() {
        return "//message";
    }

    /**
     * Implementations will try and extract the MessageWithLocations object
     * from the provided XML &lt;message&gt; element.
     * <p/>
     * The exception will be caught by the helper and logged.
     *
     * @param element holds the XML &lt;message> element to be parsed
     * @return the MessageWithLocations object parsed from the XML element
     * @throws Exception if a problem occurs during parsing etc..
     */
    public MessageWithLocations extractEntity(Element element) throws Exception {

        return extractMessage(element);
    }

    private MessageWithLocations extractMessage(Element messageElement) throws JDOMException {

        String xmlDocument = getXmlValue(messageElement,"xml-document");
        String self = getXmlValue(messageElement,"self");

        //validation

        final String delivered = getXmlValue(messageElement, "message-meta-data/delivered");
        final String messageNo = getXmlValue(messageElement, "message-meta-data/msg-no");
        final String received = getXmlValue(messageElement, "message-meta-data/received");
        final String transferDirection = getXmlValue(messageElement, "message-meta-data/direction");
        final String uuid = getXmlValue(messageElement, "message-meta-data/uuid");

        //peppol header
        final String sender = getXmlValue(messageElement, "message-meta-data/peppol-header/sender");
        final String receiver = getXmlValue(messageElement, "message-meta-data/peppol-header/receiver");
        final String channel= getXmlValue(messageElement, "message-meta-data/peppol-header/channel");
        final String documentId= getXmlValue(messageElement, "message-meta-data/peppol-header/document-id");
        final String processId= getXmlValue(messageElement, "message-meta-data/peppol-header/process-id");

        PeppolHeader peppolHeader = new PeppolHeader();
        peppolHeader.setPeppolChannelId(new PeppolChannelId(channel));
        peppolHeader.setPeppolDocumentTypeId(PeppolDocumentTypeId.valueFor(documentId));
        peppolHeader.setProfileId(new ProfileId(processId));
        peppolHeader.setReceiver(PeppolParticipantId.valueFor(receiver));
        peppolHeader.setSender(PeppolParticipantId.valueFor(sender));

        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();

        messageMetaData.setDelivered(RingoUtils.getDateTimeFromISO8601String(delivered));
        messageMetaData.setUuid(uuid);
        messageMetaData.setMsgNo(Long.parseLong(messageNo));
        messageMetaData.setReceived(RingoUtils.getDateTimeFromISO8601String(received));
        messageMetaData.setTransferDirection(TransferDirection.valueOf(transferDirection));
        messageMetaData.setPeppolHeader(peppolHeader);

        try {
            final URI selfUri = new URI(self);
            final URI xmlDocumentUri = new URI(xmlDocument);
            return new MessageWithLocationsImpl(messageMetaData, selfUri, xmlDocumentUri);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getXmlValue(Parent root, String xPathString) throws JDOMException {
        XPath xPath = XPath.newInstance(xPathString);
        Element node = (Element) xPath.selectSingleNode(root);
        if (node == null) {
            return null;
        }
        return node.getText();
    }

}
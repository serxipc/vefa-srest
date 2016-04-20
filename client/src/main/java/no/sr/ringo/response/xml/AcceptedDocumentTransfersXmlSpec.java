package no.sr.ringo.response.xml;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.XmlSpecification;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.xpath.XPath;

/**
 * XML specification which parses and creates {@link no.sr.ringo.smp.AcceptedDocumentTransfer} objects from an XML Element.
 *
 * @author andy andy@sendregning.no
 */
public class AcceptedDocumentTransfersXmlSpec implements XmlSpecification<AcceptedDocumentTransfer> {

    /**
     * the name of the entity, this is used only for logging.
     *
     * @return
     */
    public String getName() {
        return "accepted-document-transfer";
    }

    public String getXPath() {
        return "//accepted-document-transfer";
    }

    public AcceptedDocumentTransfer extractEntity(Element element) throws Exception {
        String documentId = getXmlValue(element,"DocumentID");
        String processId = getXmlValue(element,"ProcessID");
        return new AcceptedDocumentTransfer(PeppolDocumentTypeId.valueFor(documentId), new ProfileId(processId));
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
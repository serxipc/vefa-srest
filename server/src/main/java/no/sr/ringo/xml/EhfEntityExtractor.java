package no.sr.ringo.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Simple class extracting entity from Document.
 * It iterates over tags until it reaches desired element
 * <p/>
 * User: Adam
 * Date: 3/25/13
 * Time: 2:45 PM
 */
public class EhfEntityExtractor {

    private static final Logger log = LoggerFactory.getLogger(EhfEntityExtractor.class);
    private static final String INVOICE_NO = "ID";
    private final Document document;

    /**
     * @param document to extractHeader entity value from
     */
    public EhfEntityExtractor(Document document) {
        this.document = document;
        if (document == null) {
            throw new IllegalArgumentException("Cannot pass null Document to entity extractor");
        }
    }

    /**
     * Extracts invoiceNo from ehf document (<cbc:id></cbc:id>)
     *
     * @return
     * @throws XMLStreamException
     */
    public String extractInvoiceNo() {
        return extractEntity(INVOICE_NO);
    }

    /**
     * Extracts element value from document
     * To extractHeader <cbc:id> use "cbc" as prefix and "ID" as entity name
     *
     * @param entityName
     * @return extracted element value
     */
    private String extractEntity(String entityName) {

        NodeList nodeList = document.getDocumentElement().getElementsByTagName("*");
        for (int i = 0, len = nodeList.getLength(); i < len; i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if (node.getNodeName().toLowerCase().endsWith(":"+entityName.toLowerCase())) {
                return node.getTextContent();
            }
        }

        return null;
    }

}

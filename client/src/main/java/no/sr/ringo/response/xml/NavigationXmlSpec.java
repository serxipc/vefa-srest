package no.sr.ringo.response.xml;

/**
 * User: andy
 * Date: 1/27/12
 * Time: 3:21 PM
 */

import no.sr.ringo.common.XmlSpecification;
import no.sr.ringo.response.Navigation;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;
import org.jdom.xpath.XPath;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Xml specification which is able to extract MessageWithLocations objects from an XML document.
 *
 * @author andy andy@sendregning.no
 */
public class NavigationXmlSpec implements XmlSpecification<Navigation> {

    /**
     * Creates the specification.
     */
    public NavigationXmlSpec() {
    }

    /**
     * the name of the entity, this is used only for logging.
     *
     * @return
     */
    public String getName() {
        return "navigation";
    }

    /**
     * The xpath expression used to select node/nodes
     *
     * @return
     */
    public String getXPath() {
        return "//navigation";
    }

    /**
     * Implementations will try and extract the object
     * from the provided XML element.
     * <p/>
     * The exception will be caught by the helper and logged.
     *
     * @param element
     * @return
     * @throws Exception if a problem occurs during parsing etc..
     */
    public Navigation extractEntity(Element element) throws Exception {

        return extractNavigation(element);
    }

    private Navigation extractNavigation(Element messageElement) throws JDOMException, URISyntaxException {

        String next = getXmlValue(messageElement,"next");
        String previous = getXmlValue(messageElement,"previous");

        URI nextURI = next == null ? null : new URI(next);
        URI previousURI = previous == null ? null : new URI(previous);

        return new Navigation(previousURI, nextURI);
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
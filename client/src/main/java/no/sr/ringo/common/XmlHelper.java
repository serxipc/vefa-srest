package no.sr.ringo.common;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that provides error handling and logging when extracting objects from xml.
 *
 * The idea is that you of a new instance with a Specification object which is able
 * to extract an Object of type &lt;T&gt; from the xml, and then
 * you can use this class to extract either a list or an invididual Object&lt;T&gt;
 *
 * @author andy
 *
 * @param <T> The type of object that this helper will extract.
 */
public class XmlHelper<T> {
    static final Logger log = LoggerFactory.getLogger(XmlHelper.class);
	private XmlSpecification<T> xmlSpec;
    private boolean logError = true;

    /**
     * Creates a new instance of this helper configured to extract Objects of type T
     * @param xmlSpec
     */
	public XmlHelper(XmlSpecification<T> xmlSpec) {
		this.xmlSpec = xmlSpec;
	}

    /**
     * Selects a list of the object using the xml specification provided in the constructor.
     *
     * @param document
     * @return
     */
	public List<T> selectList(Document document){
		List<T> result = new ArrayList<T>();
		try {
            XPath xPath = setUpXPath();

            // select all the entity nodes
			List<Element> nodeList = (List<Element>) xPath.selectNodes(document);
			if (nodeList != null) {
				for (Element element : nodeList) {
					// extract the entity from the node.
					T object = xmlSpec.extractEntity(element);
					if (object != null) {
						result.add(object);
					}
				}
			}
		} catch (Exception e) {
            return handleException(document, e);
        }
		return result;
	}

    /**
     * Selects the first found object from the provided document.
     *
     * @param document
     * @return
     */
	public T selectSingle(Document document) {
        T result = null;
        try {
            XPath xPath = setUpXPath();

            //select exactly one node.
			Element node = (Element) xPath.selectSingleNode(document);
			if(node == null){
                XMLOutputter xml = new XMLOutputter();
                log.info(String.format("Unable to extract entity: %s using xpath: %s from xml: \n %s", xmlSpec.getName(), xmlSpec.getXPath(), xml.outputString(document)));
				return null;
			}
			//extract the entity from that node.
            result = xmlSpec.extractEntity(node);
		}
        catch (Exception e) {
            handleException(document, e);
        }
        return result;
    }

    /**
     * XPath object knows nothing of namespaces so if the XMLSpecification object is
     * NamespaceAware we need to let the XPath object know about the namespaces in use.
     *
     * @return a new instance of the XPath object for the XMLSpecification objects xpath expression.
     * @throws JDOMException
     */
    private XPath setUpXPath() throws JDOMException {
        final XPath xPath = XPath.newInstance(xmlSpec.getXPath());

        //handle namespaces
        if (xmlSpec instanceof NamespaceAware) {
            List<Namespace> namespaces = ((NamespaceAware) xmlSpec).getNamespaces();
            for (Namespace namespace : namespaces) {
                xPath.addNamespace(namespace);
            }
        }

        return xPath;
    }

    public void rethrowException() {
        logError = false;
    }

    private List<T> handleException(Document document, Exception e) {
        XMLOutputter xml = new XMLOutputter();
        String message = String.format("Unable to extract '%s' using xpath: %s from xml: \n %s", xmlSpec.getName(), xmlSpec.getXPath(), xml.outputString(document));
        if (logError) {
            log.error(message, e);
        }

        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new IllegalStateException(message, e);
    }
}

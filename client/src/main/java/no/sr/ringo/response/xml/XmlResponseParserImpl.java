package no.sr.ringo.response.xml;

import no.sr.ringo.common.XmlHelper;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.util.List;

/**
 * Xml parser responsible for parsing the response content.
 * <p/>
 * Uses an xml response handler in order to extract the expected objects
 * from the actual response.
 *
 * User: andy andy@sendregning.no
 * Date: 1/23/12
 * Time: 9:17 AM
 */
public class XmlResponseParserImpl implements XmlResponseParser {


    private final SAXBuilder sb;
    private final XmlRingoResponseHandler xmlResponseHandler;
    private Document document;

    public XmlResponseParserImpl(XmlRingoResponseHandler xmlResponseHandler) {
        this.xmlResponseHandler = xmlResponseHandler;
        sb = getSaxBuilder();
    }

    public <T> T parse(InputStream content) {

        try {
            document = sb.build(content);
            return (T) xmlResponseHandler.resolve(this);
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to read the xml returned",e);
        }
    }

    public <T> List<T> selectList(XmlHelper<T> xmlHelper) {
        return xmlHelper.selectList(document);
    }

    public <T> T selectEntity(XmlHelper<T> xmlHelper) {
        return xmlHelper.selectSingle(document);
    }

    /**
     * getter for a SaxBuilder
     */
    protected static SAXBuilder getSaxBuilder() {
        SAXBuilder sb = new SAXBuilder();
        return sb;
    }
}

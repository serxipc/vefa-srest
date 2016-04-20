package no.sr.ringo.response;

import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.response.xml.XmlResponseParser;
import no.sr.ringo.response.xml.XmlRingoResponseHandler;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * User: andy
 * Date: 10/26/12
 * Time: 8:51 AM
 */
public class NoContentXmlResponseParser implements XmlResponseParser {

    private final XmlRingoResponseHandler xmlResponseHandler;

    public NoContentXmlResponseParser(XmlRingoResponseHandler xmlResponseHandler) {
        this.xmlResponseHandler = xmlResponseHandler;
    }

    public <T> T parse(InputStream content) {

        try {
            return (T) xmlResponseHandler.resolve(this);
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to read the xml returned",e);
        }
    }

    /**
     * Selects a List of Objects of type T from the xml using the xmlHelper provided.
     *
     * @param xmlHelper
     * @param <T>
     * @return
     */
    public <T> List<T> selectList(XmlHelper<T> xmlHelper) {
        return Collections.emptyList();
    }

    /**
     * Selects the Object of type T using the xmlHelper provided.
     *
     * @param xmlHelper
     * @param <T>
     * @return
     */
    public <T> T selectEntity(XmlHelper<T> xmlHelper) {
        return null;
    }
}

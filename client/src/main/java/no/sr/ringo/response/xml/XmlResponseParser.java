package no.sr.ringo.response.xml;

import no.sr.ringo.common.XmlHelper;

import java.io.InputStream;
import java.util.List;

/**
 * Parses an InputStream which contains xml so that it is possible
 * to extract either a single entity or a list of entities.
 *
 * User: andy andy@sendrenging.no
 * Date: 1/20/12
 * Time: 3:29 PM
 */
public interface XmlResponseParser {

    /**
     * The input stream will contain the content of the http response.
     * The input stream may be empty, but will never be null.
     * @param content
     * @param <T>
     * @return
     */
    public <T> T parse(InputStream content);

    /**
     * Selects a List of Objects of type T from the xml using the xmlHelper provided.
     *
     * @param xmlHelper
     * @param <T>
     * @return
     */
    public <T> List<T> selectList(XmlHelper<T> xmlHelper);

    /**
     * Selects the Object of type T using the xmlHelper provided.
     *
     * @param xmlHelper
     * @param <T>
     * @return
     */
    public <T> T selectEntity(XmlHelper<T> xmlHelper);
}

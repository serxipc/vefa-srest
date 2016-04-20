/* Created by steinar on 06.01.12 at 14:21 */
package no.sr.ringo.response;

import no.sr.ringo.client.RingoService;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import no.sr.ringo.response.xml.XmlResponseParser;
import no.sr.ringo.response.xml.XmlResponseParserImpl;
import no.sr.ringo.response.xml.XmlRingoResponseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic Xml response handler which automatically
 * parses the xml and uses double dispatch to allow extending implementations
 * to only have to implement the resolve method.
 * It is possible to add extra status codes to the set of valid status codes if the
 * response handler is supposed to handle for example 204 responses
 *
 * @author Andy andy@sendregning.no
 */
public abstract class GenericXmlRingoResponseHandler<T> implements XmlRingoResponseHandler<T> {

    protected final RingoService ringoService;
    protected final Set<Integer> validResponseStatusCodes = new HashSet<Integer>();

    protected HttpResponse response;

    public GenericXmlRingoResponseHandler(RingoService ringoService) {
        this.ringoService = ringoService;
        validResponseStatusCodes.add(HttpStatus.SC_OK);
    }

    public T handleResponse(HttpResponse response) throws UnexpectedResponseCodeException {
        this.response = response;

        if (unexpectedStatusCode()) {
            throw new UnexpectedResponseCodeException(response);
        }

        try {
            InputStream content = getResponseContent();
            return getXmlResponseParser().parse(content);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse response.", e);
        }
    }

    protected boolean unexpectedStatusCode() {
        int statusCode = response.getStatusLine().getStatusCode();
        return !validResponseStatusCodes.contains(statusCode);
    }

    protected InputStream getResponseContent() throws IOException {
        HttpEntity entity = response.getEntity();
        return entity == null ? nullInputStream() : entity.getContent();
    }

    protected XmlResponseParser getXmlResponseParser() {
        if (responseHasNoContent()) {
            return new NoContentXmlResponseParser(this);
        }
        //not thread safe so new Instance required.
        return new XmlResponseParserImpl(this);
    }

    private boolean responseHasNoContent() {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT;
    }

    private InputStream nullInputStream() {
        return new ByteArrayInputStream(new byte[0]);
    }

}

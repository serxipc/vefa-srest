/* Created by steinar on 06.01.12 at 14:21 */
package no.sr.ringo.response;

import no.sr.ringo.client.Message;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import no.sr.ringo.response.xml.MessageXmlSpec;
import no.sr.ringo.response.xml.XmlResponseParser;
import no.sr.ringo.response.xml.XmlResponseParserImpl;
import no.sr.ringo.response.xml.XmlRingoResponseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

/**
 * Parses the response of uploading a Peppol document.
 *
 * @author andy andy@sendregning.no
 */
public class UploadRingoResponseHandler implements XmlRingoResponseHandler<Message> {

    private final RingoService ringoService;

    public UploadRingoResponseHandler(RingoService ringoService) {
        this.ringoService = ringoService;
    }

    public Message handleResponse(HttpResponse response) throws UnexpectedResponseCodeException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (!(statusCode == HttpStatus.SC_CREATED)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) EntityUtils.consumeQuietly(entity);
            throw new UnexpectedResponseCodeException(response);
        }
        try {
            XmlResponseParserImpl formatter = new XmlResponseParserImpl(this);
            return formatter.parse(response.getEntity().getContent());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse response." + response.getEntity().toString());
        }

    }

    public Message resolve(XmlResponseParser xmlResponseParser) {
        return new Message(ringoService, getMessage(xmlResponseParser));
    }

    /**
     * Extracts the Message using the MessageXmlSpec
     *
     * @param xmlResponseParser
     * @return
     */
    public MessageWithLocations getMessage(XmlResponseParser xmlResponseParser) {
        XmlHelper<MessageWithLocations> xmlHelper = new XmlHelper<MessageWithLocations>(new MessageXmlSpec());
        return xmlResponseParser.selectEntity(xmlHelper);
    }

}

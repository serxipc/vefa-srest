/* Created by steinar on 02.01.12 at 13:55 */
package no.sr.ringo.response;

import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.MessageWithLocations;

import java.io.UnsupportedEncodingException;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 *
 *
 *
 */
public class OutboxPostResponse extends SingleMessagesResponse {


    public OutboxPostResponse(MessageWithLocations message) {
        super(message);
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<outbox-post-response version=\""+version+"\">\n");

        SingleMessagesResponse.singleMessageAsXml(resultXml,message);

        resultXml.append("\n</outbox-post-response>");
        return resultXml.toString();
    }

    /**
     * Jersey is able to automatically set the content length but only if
     * the entity used is a byte[] :)
     *
     * @return byte array
     */
    public byte[] asEntity() {
        try {
            return asXml().getBytes(RingoConstants.DEFAULT_CHARACTER_SET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}

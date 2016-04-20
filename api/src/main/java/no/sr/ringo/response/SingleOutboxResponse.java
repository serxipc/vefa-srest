/* Created by steinar on 08.01.12 at 22:20 */
package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;

/**
 * Class representing the result of /outbox/{msg_no} request
 * @author adam
 */
public class SingleOutboxResponse extends SingleMessagesResponse {

    public SingleOutboxResponse(MessageWithLocations message) {
        super(message);
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<outbox-query-response version=\""+version+"\">\n");
        resultXml.append("<navigation/>\n");
        SingleMessagesResponse.singleMessageAsXml(resultXml, message);
        resultXml.append("</outbox-query-response>");
        return resultXml.toString();
    }


}

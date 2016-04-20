/* Created by steinar on 08.01.12 at 22:20 */
package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;

/**
 * Class representing the result of /inbox/{msg_no} request
 * @author adam
 */
public class SingleInboxResponse extends SingleMessagesResponse {


    public SingleInboxResponse(MessageWithLocations message) {
        super(message);
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<inbox-query-response version=\""+version+"\">\n");
        SingleMessagesResponse.singleMessageAsXml(resultXml, message);
        resultXml.append("</inbox-query-response>");
        return resultXml.toString();
    }


}

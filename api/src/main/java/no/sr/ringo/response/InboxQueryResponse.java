/* Created by steinar on 08.01.12 at 22:20 */
package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;

import java.util.List;

/**
 * Represents a list of messages from the /inbox
 * This list may represent only part of the full list. I.e. if there are thousands of messages in the
 * /inbox, only a subset will be held here. Navigation will not be supported as the contents of
 * the inbox can be changed by marking messages as read which is what we want to encourage the consumers
 * of the webservice to do.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class InboxQueryResponse extends MessagesQueryResponse {

    public InboxQueryResponse(List<MessageWithLocations> messages) {
        super(messages);
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<inbox-query-response version=\""+version+"\">\n");
        resultXml.append("<messages>\n");
        messagesAsXml(resultXml);
        resultXml.append("</messages>\n</inbox-query-response>");
        return resultXml.toString();
    }


    @Override
    public Navigation getNavigation() {
        return null;
    }

    @Override
    public void setNavigation(Navigation navigation) {
        //do nothing navigation is not supported on the inbox.
    }
}

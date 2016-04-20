/* Created by steinar on 06.01.12 at 15:27 */
package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;

import java.util.List;

/**
 * Represents a list of messages from the /outbox
 * This list may represent only part of the full list. I.e. if there are thousands of messages in the
 * /outbox, only a subset will be held here. Navigation will not be supported as the contents of
 * the outbox is dynamic.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class OutboxQueryResponse extends MessagesQueryResponse {

    public OutboxQueryResponse(List<MessageWithLocations> messages) {
        super(messages);
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<outbox-query-response version=\""+version+"\">\n");
        resultXml.append("<messages>\n");
        messagesAsXml(resultXml);
        resultXml.append("</messages>\n</outbox-query-response>");
        return resultXml.toString();
    }

    @Override
    public Navigation getNavigation() {
        return null;
    }

    @Override
    public void setNavigation(Navigation navigation) {
        //do nothing navigation is not supported on the outbox.
    }
}

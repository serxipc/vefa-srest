/* Created by steinar on 06.01.12 at 14:21 */
package no.sr.ringo.response;

import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.xml.XmlResponseParser;

import java.util.List;

/**
 * Handles parsing messages for the Outbox XML responses.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class OutboxRingoResponseHandler extends MessageListRingoResponseHandler {


    public OutboxRingoResponseHandler(RingoService ringoService) {
        super(ringoService);
    }

    @Override
    /**
     * This method is overridden so we can of a response specific for the Outbox.
     */
    public Messages resolve(XmlResponseParser xmlResponseParser) {

        // Parses the XML response into a list of MessageWithLocations objects
        List<MessageWithLocations> messages = getMessages(xmlResponseParser);

        // Wraps the message list into an object holding Outbox messages
        MessageQueryRestResponse response = new OutboxQueryResponse(messages);

        // ringoService is injected so we can navigate the result set, i.e. jump to next or previous batch of messages
        return new Messages(ringoService, response, this);
    }
}

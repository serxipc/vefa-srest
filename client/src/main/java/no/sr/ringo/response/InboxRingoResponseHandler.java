/* Created by steinar on 06.01.12 at 14:21 */
package no.sr.ringo.response;

import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.response.xml.XmlResponseParser;

/**
 * Handles fetching Messages from the Inbox response.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class InboxRingoResponseHandler extends MessageListRingoResponseHandler {


    public InboxRingoResponseHandler(RingoService ringoService) {
        super(ringoService);
    }

    @Override
    /**
     * This method is overriden so we can create a response specific for the Inbox.
     */
    public Messages resolve(XmlResponseParser xmlResponseParser) {
        InboxQueryResponse result = new InboxQueryResponse(getMessages(xmlResponseParser));
        return new Messages(ringoService, result, this);
    }
}

/* Created by steinar on 06.01.12 at 14:21 */
package no.sr.ringo.response;

import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.response.xml.MessageXmlSpec;
import no.sr.ringo.response.xml.NavigationXmlSpec;
import no.sr.ringo.response.xml.XmlResponseParser;

import java.util.List;

/**
 * Handles parsing the response containing a message list.
 *
 * @author Andy andy@sendregning.no
 */
public class MessageListRingoResponseHandler extends GenericXmlRingoResponseHandler<Messages> {


    public MessageListRingoResponseHandler(RingoService ringoService) {
        super(ringoService);
    }

    public Messages resolve(XmlResponseParser xmlResponseParser) {

        MessagesQueryResponse response = new MessagesQueryResponse(getMessages(xmlResponseParser));
        response.setNavigation(getNavigation(xmlResponseParser));

        return new Messages(ringoService,response, this);
    }

    /**
     * gets the list of messages
     * @param xmlResponseParser
     * @return
     */
    protected List<MessageWithLocations> getMessages(XmlResponseParser xmlResponseParser){
        XmlHelper<MessageWithLocations> xmlHelper = new XmlHelper<MessageWithLocations>(new MessageXmlSpec());
        return xmlResponseParser.selectList(xmlHelper);
    }

    /**
     * gets the navigation
     * @param xmlResponseParser
     * @return
     */
    protected Navigation getNavigation(XmlResponseParser xmlResponseParser) {
        XmlHelper<Navigation> xmlHelper = new XmlHelper<Navigation>(new NavigationXmlSpec());
        return xmlResponseParser.selectEntity(xmlHelper);
    }
}

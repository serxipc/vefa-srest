package no.sr.ringo.client;

import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.MessageWithLocationsImpl;
import no.sr.ringo.response.MessageListRingoResponseHandler;
import no.sr.ringo.response.MessageQueryRestResponse;
import no.sr.ringo.response.Navigation;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 2/20/12
 * Time: 1:35 PM
 */
public class MessagesTest {


    private RingoService mockRingoService;
    private MessageWithLocationsImpl messageContents;
    private MessageQueryRestResponse mockRestResponse;

    @BeforeMethod
    protected void setUp() throws Exception {
        messageContents = new MessageWithLocationsImpl();
        mockRingoService = EasyMock.createStrictMock(RingoService.class);
        mockRestResponse = EasyMock.createStrictMock(MessageQueryRestResponse.class);
    }

    /**
     * Tests that iteration with navigation works
     * @throws Exception
     */
    @Test
    public void testIterator() throws Exception {

        // The first response contains 0 messages, but navigation links to next
        // The second response contains 1 message but no navigation.

        Message testMessage = new Message(mockRingoService, messageContents);
        final MessageListRingoResponseHandler messagesResponseHandler = new MessageListRingoResponseHandler(mockRingoService);
        Messages messages = new Messages(mockRingoService, mockRestResponse, messagesResponseHandler);

        Messages navigationResponse =  new Messages(mockRingoService, mockRestResponse, messagesResponseHandler);

        final URI previous = new URI("http://localhost");
        final URI next = new URI("http://localhost");

        expect(mockRestResponse.getMessageList()).andReturn(new ArrayList<MessageWithLocations>());
        Navigation navigation = new Navigation(previous, next);
        expect(mockRestResponse.getNavigation()).andReturn(navigation);

        expect(mockRingoService.next(navigation, messagesResponseHandler)).andReturn(navigationResponse);

        final ArrayList<MessageWithLocations> value = new ArrayList<MessageWithLocations>();
        value.add(messageContents);
        expect(mockRestResponse.getMessageList()).andReturn(value);
        expect(mockRestResponse.getNavigation()).andReturn(null);

        replay(mockRestResponse, mockRingoService);

        final Iterator<Message> iterator = messages.iterator();
        //yes there should be more messages.
        assertTrue(iterator.hasNext());

        // the message should be the one associated with the second response
        final Message message = iterator.next();
        assertEquals(message, testMessage);

        // there should be no more messages.
        assertFalse(iterator.hasNext());

        verify(mockRestResponse, mockRingoService);
    }

}

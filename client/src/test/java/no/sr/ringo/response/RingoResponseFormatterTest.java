package no.sr.ringo.response;

import no.sr.ringo.client.Message;
import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.response.xml.XmlResponseParser;
import no.sr.ringo.response.xml.XmlResponseParserImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: andy
 * Date: 1/23/12
 * Time: 10:13 AM
 */
public class RingoResponseFormatterTest {


    private InputStream resourceAsStream;
    private RingoService mockRingoService;

    @BeforeMethod
    public void setUp() throws Exception {
        resourceAsStream = RingoResponseFormatterTest.class.getResourceAsStream("/inbox.xml");
    }

    @Test
    public void testInbox() throws Exception {

        InboxRingoResponseHandler parser = new InboxRingoResponseHandler(mockRingoService);

        XmlResponseParser formatter = new XmlResponseParserImpl(parser);
        final Messages messagesIterator = formatter.parse(resourceAsStream);

        assertNotNull(messagesIterator);
        
        int counter = 0;
        Message firstMessage = null;
        for (Message messageWithLocators : messagesIterator) {
            if (counter == 0) {
                firstMessage = messageWithLocators;
            }
            assertNotNull(messageWithLocators);
            counter++;
        }
        assertEquals(6,counter);

        assertNotNull(firstMessage);

    }
}

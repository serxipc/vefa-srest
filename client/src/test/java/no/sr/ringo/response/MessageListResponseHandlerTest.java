package no.sr.ringo.response;

import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 12:46 PM
 */
public class MessageListResponseHandlerTest extends AbstractResponseHandlerTest{


    private RingoService mockRingoServer;

    @Test
    public void testMessageListResponseHandler() throws Exception {

        URL testResultFileUri = MessageListResponseHandlerTest.class.getResource("/messages.xml");
        assertNotNull(testResultFileUri);
        final File testFile = new File(testResultFileUri.toURI());
        
        FileEntity entity = new FileEntity(testFile, RingoConstants.DEFAULT_CHARACTER_SET);
        MessageListRingoResponseHandler handler = new MessageListRingoResponseHandler(mockRingoServer);

        expect(mockResponse.getStatusLine()).andStubReturn(okStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        final Messages messages = handler.handleResponse(mockResponse);

        verify(mockRequest, mockResponse);

        assertTrue(messages.iterator().hasNext());
    }

    @Test
    public void testMessageListHandlerFailure() throws Exception {

        StringEntity entity = new StringEntity("The server was not happy \u00E5 no",RingoConstants.DEFAULT_CHARACTER_SET);
        MessageListRingoResponseHandler handler = new MessageListRingoResponseHandler(mockRingoServer);

        expect(mockResponse.getStatusLine()).andStubReturn(notOkStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        try {
            handler.handleResponse(mockResponse);
            fail("Should have thrown an exception");
        } catch (UnexpectedResponseCodeException e) {
            //expected
            assertEquals("Access point server returned response code '500' : 'The server was not happy \u00E5 no'",e.getMessage());
        }
    }
}



package no.sr.ringo.response;

import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests the handler for the mark as read operation.
 * User: andy
 * Date: 1/30/12
 * Time: 12:57 PM
 */
public class MarkAsReadResponseHandlerTest extends AbstractResponseHandlerTest {


    @Test
    public void testMarkAsReadOk() throws Exception {

        expect(mockResponse.getStatusLine()).andStubReturn(okStatus);

        MarkAsReadRingoResponseHandler handler = new MarkAsReadRingoResponseHandler();

        replay(mockRequest,mockResponse);

        final Boolean markAsReadOk = handler.handleResponse(mockResponse);

        assertTrue(markAsReadOk);
        verify(mockRequest, mockResponse);
    }


    @Test
    public void testMarkAsReadFailure() throws Exception {
        expect(mockResponse.getStatusLine()).andStubReturn(notOkStatus);
        
        MarkAsReadRingoResponseHandler handler = new MarkAsReadRingoResponseHandler();
        replay(mockRequest,mockResponse);

        final Boolean markAsReadOk = handler.handleResponse(mockResponse);

        assertFalse(markAsReadOk);
        verify(mockRequest,mockResponse);
    }


    @Test
    public void testMarkAsReadFailureNoMessage() throws Exception {
        expect(mockResponse.getStatusLine()).andStubReturn(noContentStatus);

        MarkAsReadRingoResponseHandler handler = new MarkAsReadRingoResponseHandler();
        replay(mockRequest,mockResponse);

        final Boolean markAsReadOk = handler.handleResponse(mockResponse);

        assertFalse(markAsReadOk);
        verify(mockRequest,mockResponse);
    }

}

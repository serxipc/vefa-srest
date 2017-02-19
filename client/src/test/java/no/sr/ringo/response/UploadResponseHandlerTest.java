package no.sr.ringo.response;

import no.sr.ringo.client.Message;
import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * Tests that the response handler can parse the xml from the file outbox.xml
 * Also checks the behaviour when the server responds with an error
 * User: andy
 * Date: 1/30/12
 * Time: 12:46 PM
 */
public class UploadResponseHandlerTest extends AbstractResponseHandlerTest{


    private RingoService mockRingoService;

    @Test
    public void testMessageListResponseHandler() throws Exception {

        URL testResultFileUri = UploadResponseHandlerTest.class.getResource("/outbox.xml");
        assertNotNull(testResultFileUri);
        final File testFile = new File(testResultFileUri.toURI());
        FileEntity entity = new FileEntity(testFile, RingoConstants.DEFAULT_CHARACTER_SET);
        UploadRingoResponseHandler handler = new UploadRingoResponseHandler(mockRingoService);

        expect(mockResponse.getStatusLine()).andStubReturn(okCreatedStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        final Message message = handler.handleResponse(mockResponse);
        assertEquals(message.getReceptionId().stringValue(), "1743866e-62e0-43e6-901a-3bc670823ee4");

        verify(mockRequest, mockResponse);
    }

    @Test
    public void testUploadResponseHandlerFailure() throws Exception {

        StringEntity entity = new StringEntity("The server was not happy \u00E5 no",RingoConstants.DEFAULT_CHARACTER_SET);
        UploadRingoResponseHandler handler = new UploadRingoResponseHandler(mockRingoService);

        expect(mockResponse.getStatusLine()).andStubReturn(badRequestStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        try {
            handler.handleResponse(mockResponse);
            fail("Should have thrown an exception");
        } catch (UnexpectedResponseCodeException e) {
            //expected
            assertEquals("Access point server returned response code '400' : 'The server was not happy \u00E5 no'",e.getMessage());
        }
    }
}



package no.sr.ringo.response;

import no.sr.ringo.common.RingoConstants;
import org.apache.http.entity.StringEntity;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 11:54 AM
 */
public class IsParticipantRegisteredResponseHandlerTest extends AbstractResponseHandlerTest {


    @Test
    public void testIsParticipantRegisteredResponseHandler() throws Exception {

        StringEntity entity = new StringEntity("", RingoConstants.DEFAULT_CHARACTER_SET);
        IsParticipantRegisteredRingoResponseHandler handler = new IsParticipantRegisteredRingoResponseHandler();

        expect(mockResponse.getStatusLine()).andStubReturn(okStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);
        

        replay(mockRequest, mockResponse);

        final Boolean result = handler.handleResponse(mockResponse);

        verify(mockRequest,mockResponse);

        assertTrue(result);
    }

    @Test
    public void testIsParticipantRegisteredResponseHandlerFailure() throws Exception {

        StringEntity entity = new StringEntity("",RingoConstants.DEFAULT_CHARACTER_SET);
        IsParticipantRegisteredRingoResponseHandler handler = new IsParticipantRegisteredRingoResponseHandler();

        expect(mockResponse.getStatusLine()).andStubReturn(noContentStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        final Boolean result = handler.handleResponse(mockResponse);

        verify(mockRequest,mockResponse);

        assertFalse(result);
    }

}

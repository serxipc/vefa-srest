package no.sr.ringo.response;

import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.response.exception.UnexpectedResponseCodeException;
import org.apache.http.entity.StringEntity;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 11:54 AM
 */
public class CountResponseHandlerTest extends AbstractResponseHandlerTest {


    @Test
    public void testCountResponseHandler() throws Exception {

        StringEntity entity = new StringEntity("12", RingoConstants.DEFAULT_CHARACTER_SET);
        CountRingoResponseHandler handler = new CountRingoResponseHandler();

        expect(mockResponse.getStatusLine()).andStubReturn(okStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);
        

        replay(mockRequest, mockResponse);

        final Integer count = handler.handleResponse(mockResponse);

        verify(mockRequest,mockResponse);

        assertEquals(count.intValue(), 12);
    }

    @Test
    public void testCountResponseHandlerFailure() throws Exception {

        StringEntity entity = new StringEntity("The server was not happy \u00E5 no",RingoConstants.DEFAULT_CHARACTER_SET);
        CountRingoResponseHandler handler = new CountRingoResponseHandler();

        expect(mockResponse.getStatusLine()).andStubReturn(notOkStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);

        replay(mockRequest, mockResponse);

        try {
            handler.handleResponse(mockResponse);
            fail("Should have thrown an exception");
        } catch (UnexpectedResponseCodeException e) {
            //expected
            assertEquals("Access point server returned response code '500' : 'The server was not happy \u00E5 no'", e.getMessage());
        }

        verify(mockRequest, mockResponse);
    }

}

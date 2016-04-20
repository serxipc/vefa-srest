package no.sr.ringo.resource;

import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 10/9/12
 * Time: 12:44 PM
 */
public class InvalidUserInputWebExceptionTest {

    private Logger mockLogger;
    private InvalidUserInputWebException exception;

    @BeforeMethod
    public void setUp() throws Exception {
        mockLogger = EasyMock.createStrictMock(Logger.class);
        exception = new InvalidUserInputWebException("this should be logged to info");
    }

    @Test
    public void testWebExceptionLogsInfoMessage() throws Exception {

        expectLogInfoCalled();

        exception.logMessage(mockLogger);

        verify(mockLogger);
    }

    @Test
    public void testBadResponseGenerated() throws Exception {
        Response response = exception.getResponse();
        assertEquals(response.getStatus(), 400);
        assertEquals(response.getEntity(), exception.getMessage());
    }

    private void expectLogInfoCalled() {
        expect(mockLogger.isInfoEnabled()).andReturn(true);
        mockLogger.info(exception.getMessage(), exception.getCause());
        expectLastCall();
        replay(mockLogger);
    }
}

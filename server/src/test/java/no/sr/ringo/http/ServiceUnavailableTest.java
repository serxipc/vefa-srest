package no.sr.ringo.http;

import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Messages;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.request.RetryHandler;
import no.sr.ringo.request.RetryStrategy;
import no.sr.ringo.response.exception.AccessPointUnavailableException;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.OutputStream;
import java.net.URI;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests that when we get a 503 it is handled differently.
 *
 * User: andy
 * Date: 2/23/12
 * Time: 10:58 AM
 */
public class ServiceUnavailableTest extends AbstractFailingHttpClientServerTest {


    private OutputStream mockOutputStream;

    @BeforeMethod
    protected void setUp() throws Exception {
        mockOutputStream = EasyMock.createStrictMock(OutputStream.class);
    }

    @Test(groups = {"failingServer"})
    public void testServiceUnvailable() throws Exception {
        config.setRetryHandler(new RetryHandler(RetryStrategy.ONCE));

        final Inbox inbox = ringoRestClientImpl.getInbox();
        try {
            final Messages messages = inbox.getMessages();
            fail("Access point should have been unavailable");
        } catch (AccessPointUnavailableException e) {
            //the number of executions of the request should be 2
            assertEquals(e.getExecutionCount(), 2);
        }
    }

    @Test(groups = {"failingServer"})
    public void testDownloadServiceUnvailable() throws Exception {
        config.setRetryHandler(new RetryHandler(RetryStrategy.ONCE));
        MessageWithLocations message = EasyMock.createStrictMock(MessageWithLocations.class);
        expect(message.getXmlDocumentURI()).andStubReturn(new URI(PEPPOL_BASE_REST_URL + "/messages/1/xml-doc"));
        replay(message);
        try {
            ringoRestClientImpl.getRingoService().downloadMessage(message, mockOutputStream);
            fail("Access point should have been unavailable");
        } catch (AccessPointUnavailableException e) {
            //the number of executions of the request should be 2
            assertEquals(e.getExecutionCount(), 2);
        }

        config.setRetryHandler(new RetryHandler(RetryStrategy.NONE));
        try {
            ringoRestClientImpl.getRingoService().downloadMessage(message, mockOutputStream);
            fail("Access point should have been unavailable");
        } catch (AccessPointUnavailableException e) {
            //the number of executions of the request should be 2
            assertEquals(e.getExecutionCount(), 1);
        }

        verify(message);
    }
}

package no.sr.ringo.http;

import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoClientImpl;
import no.sr.ringo.response.exception.BadCredentialsException;
import no.sr.ringo.standalone.DefaultRingoConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Tests that when we get a 401 Not authorised it is handled differently.
 */
public class BadCredentialsTest extends AbstractHttpClientServerTest {



    @Test(groups = {"integration"}, enabled = false)
    public void testInvalidUserNameAndPassword() throws Exception {
        RingoClientImpl client = new RingoClientImpl(new DefaultRingoConfig(PEPPOL_BASE_URL, null), "rubbish", "more rubbish");

        final Inbox inbox = client.getInbox();
        try {
            final Messages messages = inbox.getMessages();
            fail("IllegalArgumentException should have been thrown due to invalid credentials");
        } catch (IllegalArgumentException e) {
            //the cause should have been bad credentials
            assertEquals(e.getCause().getClass(), BadCredentialsException.class);
        }
    }

}

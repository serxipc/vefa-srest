package no.sr.ringo.client;

import no.sr.ringo.exception.NotifyingException;
import no.sr.ringo.standalone.parser.RingoClientConnectionParams;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.*;

/**
 * User: Adam
 * Date: 4/11/13
 * Time: 8:38 AM
 */
public class ClientErrorNotifierTest {

    RingoService mockRingoService;

    @BeforeMethod
    public void setUp() throws Exception {
        mockRingoService = createMock(RingoService.class);
    }

    @Test
    public void testSendErrorNotification() throws Exception {

        ErrorNotificationData data = new ErrorNotificationData("testCommandLine", NotifyingException.NotificationType.DOWNLOAD, "testErrorMessage");
        expect(mockRingoService.sendErrorNotification(data)).andReturn(true);

        replay(mockRingoService);

        ClientErrorNotifier notifier = new ClientErrorNotifier();
        notifier.injectRingoService(mockRingoService);
        notifier.sendErrorNotification(data, new RingoClientConnectionParams());

        verify(mockRingoService);
    }
}

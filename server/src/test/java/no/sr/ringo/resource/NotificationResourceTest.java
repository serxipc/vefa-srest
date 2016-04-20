package no.sr.ringo.resource;

import com.sun.jersey.api.uri.UriBuilderImpl;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.account.RingoAccount;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;


/**
 * User: adam
 * Date: 12/4/13
 */
public class NotificationResourceTest {

    private RingoAccount mockRingoAccount;
    private NotificationResource notificationResource;
    private EmailService mockEmailService;

    String commandLine = "testCommandLine";
    String errorMessage = "testErrorMessage";

    @BeforeMethod
    public void setUp() throws Exception {

        mockRingoAccount = EasyMock.createStrictMock(RingoAccount.class);
        mockEmailService = createStrictMock(EmailService.class);
        notificationResource = new NotificationResource(null, mockEmailService);
    }

    @Test
    public void testSendBatchUploadErrorNotification() throws Exception {
        UriInfo uriInfo = createMockUriInfo();

        expect(mockEmailService.sendClientBatchUploadErrorNotification(mockRingoAccount, commandLine, errorMessage)).andReturn(null);

        replayAllMocks();

        Response message = notificationResource.batchUploadError(commandLine, errorMessage, uriInfo);

        assertEquals(message.getStatus(), 200);
    }

    @Test
    public void testSendDownloadErrorNotification() throws Exception {
        UriInfo uriInfo = createMockUriInfo();

        expect(mockEmailService.sendClientDownloadErrorNotification(mockRingoAccount, commandLine, errorMessage)).andReturn(null);

        replayAllMocks();

        Response message = notificationResource.downloadError(commandLine, errorMessage, uriInfo);

        assertEquals(message.getStatus(), 200);
    }

    private UriInfo createMockUriInfo() {
        UriInfo strictMock = EasyMock.createStrictMock(UriInfo.class);
        UriBuilderImpl value = new UriBuilderImpl();
        expect(strictMock.getBaseUriBuilder()).andStubReturn(value);
        replay(strictMock);
        return strictMock;
    }

    private void replayAllMocks() {
        replay(mockRingoAccount, mockEmailService);
    }

    private void verifyAllMocks() {
        verify(mockRingoAccount, mockEmailService);
    }

}

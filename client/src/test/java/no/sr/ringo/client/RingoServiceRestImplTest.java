package no.sr.ringo.client;

import no.sr.ringo.common.UploadMode;
import no.sr.ringo.document.ClientPeppolDocument;
import no.sr.ringo.exception.NotifyingException;
import no.sr.ringo.peppol.*;
import no.sr.ringo.response.AcceptedDocumentTransfersRingoResponseHandler;
import no.sr.ringo.response.NotificationRingoResponseHandler;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.BasicHttpContext;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 10/25/12
 * Time: 2:33 PM
 */
public class RingoServiceRestImplTest {


    private HttpClient httpClient;
    private RingoServiceRestImpl ringoService;
    private ClientPeppolDocument clientPeppolDocument;
    private UploadMode uploadMode = UploadMode.SINGLE;

    @BeforeMethod
    public void setUp() throws Exception {
        httpClient = EasyMock.createStrictMock(HttpClient.class);
        ringoService = new RingoServiceRestImpl(new DefaultTestRingoConfig(),"andy","password");
        ringoService.setHttpClient(httpClient);

        clientPeppolDocument = EasyMock.createMock(ClientPeppolDocument.class);
    }

    @Test
    public void testFetchAcceptedDocumentTransfer() throws Exception {

        List<AcceptedDocumentTransfer> expectedResult = new ArrayList<AcceptedDocumentTransfer>();
        expect(httpClient.execute(isA(HttpHost.class),isA(HttpGet.class),isA(AcceptedDocumentTransfersRingoResponseHandler.class),isA(BasicHttpContext.class))).andStubReturn(expectedResult);

        replay(httpClient);
        List<AcceptedDocumentTransfer> result = ringoService.fetchAcceptedDocumentTransfers(PeppolParticipantId.valueFor("976098897"), LocalName.Invoice);

        assertNotNull(result);
        verify(httpClient);
    }

    @Test(expectedExceptions = InvalidPeppolHeaderException.class)
    public void testSendDocumentInvalidHeaders() throws Exception {

        PeppolHeader peppolHeader = new PeppolHeader();

        expect(clientPeppolDocument.getContentBody()).andStubReturn(new StringBody("Hello"));
        replay(clientPeppolDocument);

        ringoService.sendDocument(clientPeppolDocument, peppolHeader, uploadMode);
    }

    @Test
    public void testSendDocumentValidInvoiceHeaders() throws Exception {

        PeppolParticipantId receiver = PeppolParticipantId.valueFor("0037:12345");
        PeppolParticipantId sender = PeppolParticipantId.valueFor("0037:12345");
        PeppolDocumentTypeId invoiceOnly = PeppolDocumentTypeId.EHF_INVOICE;
        PeppolHeader peppolHeader = PeppolHeader.forDocumentType(invoiceOnly, sender, receiver);

        expect(clientPeppolDocument.getContentBody()).andStubReturn(new StringBody("Hello"));
        replay(clientPeppolDocument);

        ringoService.sendDocument(clientPeppolDocument, peppolHeader, uploadMode);
    }


    @Test
    public void testUrlEncodeSwedishOrgNum() throws Exception {

        String swedishOrgNum = "0007:2021005026";
        PeppolParticipantId receiver = PeppolParticipantId.valueFor(swedishOrgNum);
        String asString = ringoService.urlEncode(receiver);
        assertEquals(asString,"0007%3A2021005026");
    }

    @Test
    public void testSendErrorNotification() throws Exception {

        ErrorNotificationData data = new ErrorNotificationData("test commandLine", NotifyingException.NotificationType.DOWNLOAD, "errorMessage");

        expect(httpClient.execute(isA(HttpHost.class), isA(HttpPost.class), isA(NotificationRingoResponseHandler.class), isA(BasicHttpContext.class))).andStubReturn(true);

        replay(httpClient);
        Boolean result = ringoService.sendErrorNotification(data);

        assertTrue(result);
        verify(httpClient);
    }
}

package no.sr.ringo.client;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
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
import static org.testng.Assert.*;

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
        List<AcceptedDocumentTransfer> result = ringoService.fetchAcceptedDocumentTransfers(ParticipantIdentifier.of("976098897"), LocalName.Invoice);

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

        ParticipantIdentifier receiver = ParticipantIdentifier.of("0037:12345");
        ParticipantIdentifier sender = ParticipantIdentifier.of("0037:12345");
        final DocumentTypeIdentifier documentTypeId = PeppolDocumentTypeId.EHF_INVOICE.toVefa();
        PeppolHeader peppolHeader = PeppolHeader.forDocumentType(documentTypeId, PeppolProcessIdAcronym.INVOICE_ONLY.toVefa(),sender, receiver);

        expect(clientPeppolDocument.getContentBody()).andStubReturn(new StringBody("Hello"));
        replay(clientPeppolDocument);

        ringoService.sendDocument(clientPeppolDocument, peppolHeader, uploadMode);
    }


    @Test
    public void testUrlEncodeSwedishOrgNum() throws Exception {

        String swedishOrgNum = "0007:2021005026";
        ParticipantIdentifier receiver = ParticipantIdentifier.of(swedishOrgNum);
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

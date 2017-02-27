package no.sr.ringo.resource;

import com.sun.jersey.api.uri.UriBuilderImpl;
import no.sr.ringo.account.Account;
import no.sr.ringo.document.DefaultPeppolDocument;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.document.PeppolDocument;
import no.sr.ringo.message.*;
import no.sr.ringo.transport.TransferDirection;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;


/**
 * User: andy
 * Date: 10/5/12
 * Time: 10:17 AM
 */
public class OutboxResourceTest {


    private Account mockRingoAccount;
    private FetchMessagesUseCase mockFetchMessageUseCase;
    private OutboxResource outboxResource;
    private FetchDocumentUseCase mockFetchDocumentUseCase;

    @BeforeMethod
    public void setUp() throws Exception {

        mockRingoAccount = EasyMock.createStrictMock(Account.class);
        mockFetchMessageUseCase = EasyMock.createStrictMock(FetchMessagesUseCase.class);
        mockFetchDocumentUseCase = EasyMock.createStrictMock(FetchDocumentUseCase.class);
        outboxResource = new OutboxResource(null, mockRingoAccount, mockFetchMessageUseCase, mockFetchDocumentUseCase);
    }

    @Test
    public void testGetSpecificMessageForOutBox() throws Exception {
        UriInfo uriInfo = createMockUriInfo();
        MessageMetaData messageMetaData = messageMetaData(1);

        expect(mockFetchMessageUseCase.findOutBoundMessageByMessageNo(mockRingoAccount, messageMetaData.getMsgNo().longValue())).andStubReturn(messageMetaData);

        replayAllMocks();

        Response message = outboxResource.getMessage(messageMetaData.getMsgNo().toString(), uriInfo);

        assertEquals(message.getStatus(), 200);
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testGetSpecificMessageForOutBoxException() throws Exception {
        Long msgNo = 1L;
        UriInfo uriInfo = createMockUriInfo();

        final MessageNumber messageNo = MessageNumber.create(msgNo);
        
        expect(mockFetchMessageUseCase.findOutBoundMessageByMessageNo(mockRingoAccount, msgNo)).andThrow(new PeppolMessageNotFoundException(messageNo));

        replayAllMocks();

        Response message = outboxResource.getMessage(msgNo.toString(), uriInfo);
    }

    @Test
    public void testDownloadPeppolDocument() throws Exception {
        MessageNumber msgNo = MessageNumber.create(1);
        PeppolDocument peppolDocument = new DefaultPeppolDocument("");
        expect(mockFetchDocumentUseCase.execute(mockRingoAccount, msgNo)).andStubReturn(peppolDocument);
        replayAllMocks();

        Response response = outboxResource.downloadPeppolDocument(msgNo.toString());

        assertEquals(response.getStatus(), 200);
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testDownloadPeppolDocumentException() throws Exception {
        MessageNumber msgNo = MessageNumber.create(1);
        expect(mockFetchDocumentUseCase.execute(mockRingoAccount, msgNo)).andThrow(new PeppolMessageNotFoundException(msgNo));
        replayAllMocks();

        Response response = outboxResource.downloadPeppolDocument(msgNo.toString());
    }


    private MessageMetaDataImpl messageMetaData(Integer msgNo) {
        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
        messageMetaData.setMsgNo(1L);
        messageMetaData.setTransferDirection(TransferDirection.OUT);
        return messageMetaData;
    }


    private UriInfo createMockUriInfo() {
        UriInfo strictMock = EasyMock.createStrictMock(UriInfo.class);
        UriBuilderImpl value = new UriBuilderImpl();
        expect(strictMock.getBaseUriBuilder()).andStubReturn(value);
        replay(strictMock);
        return strictMock;
    }

    private void replayAllMocks() {
        replay(mockRingoAccount, mockFetchMessageUseCase, mockFetchDocumentUseCase);
    }
}

package no.sr.ringo.message;

import com.sun.jersey.api.uri.UriBuilderImpl;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.document.DefaultPeppolDocument;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.resource.MessagesResource;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;

/**
 * User: Adam
 * Date: 2/24/12
 * Time: 9:04 AM
 */
public class MessagesResourceMockTest {

    MessagesResource messagesResource;

    ReceiveMessageFromClientUseCase mockReceiveMessageFromClientUseCase;
    FetchMessagesUseCase fetchMessagesUseCase;
    FetchDocumentUseCase mockFetchDocumentUseCase;

    PeppolMessageRepository mockPeppolMessageRepository;
    UriInfo mockUriInfo;

    RingoAccount ringoAccount;

    @BeforeMethod
    public void setup() {
        mockUriInfo = createStrictMock(UriInfo.class);
        mockPeppolMessageRepository = createStrictMock(PeppolMessageRepository.class);
        mockFetchDocumentUseCase = createStrictMock(FetchDocumentUseCase.class);
        mockReceiveMessageFromClientUseCase = createStrictMock(ReceiveMessageFromClientUseCase.class);

        fetchMessagesUseCase = new FetchMessagesUseCase(mockPeppolMessageRepository);
        ringoAccount = ObjectMother.getTestAccount();

        messagesResource = new MessagesResource(mockReceiveMessageFromClientUseCase, fetchMessagesUseCase, mockPeppolMessageRepository, mockFetchDocumentUseCase, ringoAccount);
    }


    /*
    * We had an error passing arguments to SearchParams in wrong order, so this test makes sure that search params values are equal to what we pass to getMessages
    */
    @Test(groups = {"integration"})
    public void testGetMessages() throws Exception {

        String direction = "OUT";
        String sender = "sender";
        String receiver = "receiver";
        String sent = "<=2112-01-01";
        String index = "4";

        SearchParams params = new SearchParams(direction, sender, receiver, sent, index);
        expect(mockPeppolMessageRepository.findMessages(new AccountId(1), params)).andReturn(new ArrayList<MessageMetaData>());
        expect(mockPeppolMessageRepository.getMessagesCount(new AccountId(1), params)).andReturn(0);
        expect(mockUriInfo.getBaseUriBuilder()).andReturn(new UriBuilderImpl());

        replayAllMocks();

        Response messages = messagesResource.getMessages(mockUriInfo, sent, sender, receiver, direction, index);
        assertEquals(messages.getStatus(), 200);
        verifyAllMocks();
    }

    @Test
    public void testGetXmlDocument() throws Exception {

        expect(mockFetchDocumentUseCase.execute(ringoAccount, MessageNumber.create(1))).andReturn(new DefaultPeppolDocument("An xml document"));
        replayAllMocks();

        Response xmlResponse = messagesResource.getXmlDocument("1");

        assertEquals(xmlResponse.getStatus(), 200);
        verifyAllMocks();
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testMessageNotFoundException() throws Exception {

        expect(mockFetchDocumentUseCase.execute(ringoAccount, MessageNumber.create(1))).andThrow(new PeppolMessageNotFoundException(1));
        replayAllMocks();

        messagesResource.getXmlDocument("1");

        verifyAllMocks();
    }

    private void verifyAllMocks() {
        verify(mockPeppolMessageRepository, mockUriInfo, mockFetchDocumentUseCase, mockReceiveMessageFromClientUseCase);
    }

    private void replayAllMocks() {
        replay(mockPeppolMessageRepository, mockUriInfo, mockFetchDocumentUseCase, mockReceiveMessageFromClientUseCase);
    }


}

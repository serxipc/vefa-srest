package no.sr.ringo.message;

import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.resource.UriLocationAware;
import no.sr.ringo.response.MessagesQueryResponse;
import no.sr.ringo.response.Navigation;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 2/21/12
 * Time: 1:42 PM
 */
public class FetchMessagesUseCaseTest {
    private PeppolMessageRepository mockPeppolMessageRepository;
    private UriInfo mockUriInfo;
    private UriLocationAware mockLocationAware;
    private URI OK_URI;
    private MessagesDataProvider mockMessagesDataProvider;
    private FetchMessagesUseCase useCase;
    private Account account;

    @BeforeMethod
    public void setUp() throws Exception {
        mockPeppolMessageRepository = EasyMock.createStrictMock(PeppolMessageRepository.class);
        mockUriInfo = EasyMock.createStrictMock(UriInfo.class);
        mockLocationAware = EasyMock.createStrictMock(UriLocationAware.class);
        mockMessagesDataProvider = EasyMock.createStrictMock(MessagesDataProvider.class);
        OK_URI = new URI("http://test");
        //set up the usecase with mocks
        useCase = new FetchMessagesUseCase(mockPeppolMessageRepository);
        useCase.messagesDataProvider = mockMessagesDataProvider;
        useCase.locationAware = mockLocationAware;
        account = account();
    }

    @Test
    public void testNavigationWithNoNextAndPreviousLinks() throws Exception{
        final SearchParams searchParams = new SearchParams("IN","9908:123456789","9908:123456789","",null);

        expect(mockMessagesDataProvider.getCount(searchParams)).andReturn(0);

        replay(mockLocationAware, mockMessagesDataProvider);

        final Navigation navigation = useCase.getNavigation(searchParams);
        assertNull(navigation.getNext());
        assertNull(navigation.getPrevious());

        verify(mockLocationAware, mockMessagesDataProvider);
    }

    @Test
    public void testNavigationWithOnlyNextLinks() throws Exception{
        final SearchParams searchParams = new SearchParams("IN","9908:123456789","9908:123456789","",null);

        expect(mockMessagesDataProvider.getCount(searchParams)).andReturn(26);
        expect(mockLocationAware.linkToResource(null, searchParams, 2)).andReturn(OK_URI);

        replay(mockLocationAware, mockMessagesDataProvider);

        final Navigation navigation = useCase.getNavigation(searchParams);

        assertEquals(navigation.getNext(), OK_URI);
        assertNull(navigation.getPrevious());
        verify(mockLocationAware, mockMessagesDataProvider);

    }

    @Test
    public void testNavigationWithOnlyPreviousLinks() throws Exception{
        final SearchParams searchParams = new SearchParams("IN","9908:123456789","9908:123456789","","2");

        expect(mockMessagesDataProvider.getCount(searchParams)).andReturn(48);
        expect(mockLocationAware.linkToResource(null, searchParams, 1)).andReturn(OK_URI);

        replay(mockLocationAware,mockMessagesDataProvider);

        final Navigation navigation = useCase.getNavigation(searchParams);
        assertNull(navigation.getNext());
        assertEquals(navigation.getPrevious(),OK_URI);

        verify(mockLocationAware, mockMessagesDataProvider);
    }

    @Test
    public void testNavigationWithOnlyPreviousLinksUpperLimit() throws Exception{
        final SearchParams searchParams = new SearchParams("IN","9908:123456789","9908:123456789","","2");

        expect(mockMessagesDataProvider.getCount(searchParams)).andReturn(50);
        expect(mockLocationAware.linkToResource(null, searchParams, 1)).andReturn(OK_URI);

        replay(mockLocationAware,mockMessagesDataProvider);

        final Navigation navigation = useCase.getNavigation(searchParams);
        assertNull(navigation.getNext());
        assertEquals(navigation.getPrevious(),OK_URI);

        verify(mockLocationAware, mockMessagesDataProvider);
    }

    @Test
    public void testsThatMessagesWithoutSearchParamsBehavesAsExpected() throws Exception{
        List<MessageMetaData> messages = new ArrayList<MessageMetaData>();

        expect(mockPeppolMessageRepository.findMessages(account.getId(), null)).andReturn(messages);
        replayMocks();

        useCase.messagesFor(account.getId());

        final MessagesQueryResponse messagesQueryResponse = useCase.getMessages();

        assertEquals(0,messagesQueryResponse.getMessageList().size());

        verify(mockPeppolMessageRepository, mockMessagesDataProvider);
    }

    @Test
    public void testsThatMessagesWithoutAccountIdBehaveAsExpected() throws Exception{
        List<MessageMetaData> messages = new ArrayList<MessageMetaData>();

        expect(mockPeppolMessageRepository.findMessagesWithoutAccountId()).andReturn(messages);
        replayMocks();

        useCase.messagesWithoutAccountId();

        final MessagesQueryResponse messagesQueryResponse = useCase.getMessages();

        assertEquals(0,messagesQueryResponse.getMessageList().size());

        verify(mockPeppolMessageRepository, mockMessagesDataProvider);
    }

    @Test
    public void testsGetMessagesFromInbox() throws Exception{
        List<MessageMetaData> messages = new ArrayList<MessageMetaData>();

        expect(mockMessagesDataProvider.getMessages(TransferDirection.IN)).andReturn(messages);
        replayMocks();

        final MessagesQueryResponse messagesQueryResponse = useCase.getInbox();

        assertEquals(0,messagesQueryResponse.getMessageList().size());

        verify(mockPeppolMessageRepository, mockMessagesDataProvider);
    }


    @Test
    public void testsGetMessagesFromOutbox() throws Exception{
        List<MessageMetaData> messages = new ArrayList<MessageMetaData>();

        expect(mockMessagesDataProvider.getMessages(TransferDirection.OUT)).andReturn(messages);
        replayMocks();

        final MessagesQueryResponse messagesQueryResponse = useCase.getOutbox();

        assertEquals(0,messagesQueryResponse.getMessageList().size());

        verify(mockPeppolMessageRepository, mockMessagesDataProvider);
    }

    @Test
    public void testFindOutboundMessage() throws Exception {
        expect(mockPeppolMessageRepository.findMessageByMessageNo(account,1L)).andStubReturn(validOutboundMessage());
        replayMocks();

        MessageMetaData outBoundMessageByMessageNo = useCase.findOutBoundMessageByMessageNo(account, 1L);

        assertNotNull(outBoundMessageByMessageNo);
    }

    @Test(expectedExceptions = PeppolMessageNotFoundException.class)
    public void testFindOutboundMessageException() throws Exception {
        expect(mockPeppolMessageRepository.findMessageByMessageNo(account,1L)).andStubReturn(invalidOutboundMessage());
        replayMocks();

        MessageMetaData outBoundMessageByMessageNo = useCase.findOutBoundMessageByMessageNo(account, 1L);

        assertNotNull(outBoundMessageByMessageNo);
    }

    private MessageMetaData invalidOutboundMessage() {
        return new MessageMetaDataImpl();
    }

    private MessageMetaData validOutboundMessage() {

        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
        messageMetaData.setTransferDirection(TransferDirection.OUT);
        return messageMetaData;
    }


    private void replayMocks() {
        replay(mockPeppolMessageRepository, mockMessagesDataProvider);
    }



    private Account account() {
        Account account = ObjectMother.getTestAccount();
        return account;
    }
}

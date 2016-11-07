package no.sr.ringo.usecase;

import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountRepository;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.OutboundMessageQueueState;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.oxalis.PeppolDocumentSender;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.queue.*;
import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 8/28/12
 * Time: 2:41 PM
 */

public class SendQueuedMessageUseCaseTest {

    private PeppolMessageRepository mockMessageRepository;
    private QueueRepository mockQueueRepository;
    private PeppolDocumentSender mockDocumentSender;
    private EmailService mockEmailService;
    private AccountRepository mockAccountRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        mockMessageRepository = EasyMock.createMock(PeppolMessageRepository.class);
        mockQueueRepository = EasyMock.createMock(QueueRepository.class);
        mockDocumentSender = EasyMock.createMock(PeppolDocumentSender.class);
        mockEmailService = EasyMock.createMock(EmailService.class);
        mockAccountRepository = EasyMock.createMock(AccountRepository.class);
    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testSendDocumentViaPeppolOk() throws Exception {

        //Tests the all ok path for sending a document

        final Long msgNo = 1L;
        final OutboundMessageQueueId queueId = new OutboundMessageQueueId(10);

        String message = "a fancy invoice";
        PeppolDocumentSender.TransmissionReceipt receipt = new PeppolDocumentSender.TransmissionReceipt("uuid:12312313", null, new Date());
        SendQueuedMessagesUseCase useCase = new SendQueuedMessagesUseCase(mockDocumentSender, mockMessageRepository, mockQueueRepository, mockEmailService, mockAccountRepository);
        //creates a mock message so that we can check the correct data is being inspected
        MessageMetaData mockMessage = EasyMock.createMock(MessageMetaData.class);

        //set up expectations for message
        expect(mockMessage.getMsgNo()).andStubReturn(msgNo);
        expect(mockMessage.getPeppolHeader()).andStubReturn(getSimplePeppolHeader());

        //set up expectations for messageRepo
        expect(mockQueueRepository.lockQueueItemForDelivery(queueId)).andStubReturn(true);
        expect(mockMessageRepository.findDocumentByMessageNoWithoutAccountCheck(msgNo)).andStubReturn(message);
        mockMessageRepository.updateOutBoundMessageDeliveryDateAndUuid(msgNo, receipt.getRemoteAccessPoint(), receipt.getMessageId(), receipt.getDate());
        expectLastCall();

        //set up expectations for documentSender
        expect(mockDocumentSender.sendDocument(mockMessage, message)).andStubReturn(receipt);

        replay(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage);

        //runs the test
        final boolean result = useCase.lockQueueRowAndSendMessage(mockMessage, queueId);
        assertTrue(result);

        //checks that all went well
        verify(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage);

    }


    @Test
    public void testSendDocumentViaPeppolLockedMessage() throws Exception {

        //Tests that  path when a message is already locked for sending a document
        final Long msgNo = 1L;
        final OutboundMessageQueueId queueId = new OutboundMessageQueueId(10);

        SendQueuedMessagesUseCase useCase = new SendQueuedMessagesUseCase(mockDocumentSender, mockMessageRepository, mockQueueRepository, mockEmailService, mockAccountRepository);
        //creates a mock message so that we can check the correct data is being inspected
        MessageMetaData mockMessage = EasyMock.createMock(MessageMetaData.class);

        //set up expectations for message
        expect(mockMessage.getMsgNo()).andStubReturn(msgNo);

        //set up expectations for messageRepo
        expect(mockQueueRepository.lockQueueItemForDelivery(queueId)).andStubReturn(false);

        replay(mockMessageRepository, mockDocumentSender, mockQueueRepository,  mockMessage);

        //runs the test
        final boolean result = useCase.lockQueueRowAndSendMessage(mockMessage, queueId);

        assertFalse(result);

        //checks that all went well
        verify(mockMessageRepository, mockDocumentSender, mockQueueRepository,  mockMessage);

    }

    @Test
    /**
     * Test that message is updated as well as the queue state
     */
    public void testHandleSingleMessageOk() throws Exception {

        //Tests that  path when a message is already locked for sending a document
        final MessageNumber msgNo = MessageNumber.create(1L);
        final OutboundMessageQueueId queueId = new OutboundMessageQueueId(10);
        QueuedOutboundMessage mockQueue = createMock("QueuedOutboundMessage", QueuedOutboundMessage.class);
        MessageMetaData mockMessage = EasyMock.createMock(MessageMetaData.class);


        SendQueuedMessagesUseCase useCase = new SendQueuedMessagesUseCase(mockDocumentSender, mockMessageRepository, mockQueueRepository, mockEmailService, mockAccountRepository);

        expectationsForQueueAndMessage(msgNo, queueId, mockQueue, mockMessage);

        String messageXml = "<xml>message</xml>";
        expect(mockMessageRepository.findDocumentByMessageNoWithoutAccountCheck(msgNo.toLong())).andStubReturn(messageXml);

        //set up expectations for messageRepo
        expect(mockQueueRepository.lockQueueItemForDelivery(queueId)).andStubReturn(true);

        // expect message to be sent by oxalis
        expect(mockDocumentSender.sendDocument(mockMessage, messageXml)).andStubReturn(new PeppolDocumentSender.TransmissionReceipt("uuid", new URL("http://ringo.domain.com/"), new Date()));

        // update message to delivered
        mockMessageRepository.updateOutBoundMessageDeliveryDateAndUuid(EasyMock.eq(msgNo.toLong()), EasyMock.eq("http://ringo.domain.com/"), EasyMock.eq("uuid"), isA(Date.class));
        expectLastCall();

        // update state to OK
        mockQueueRepository.changeQueuedMessageState(queueId, OutboundMessageQueueState.OK);


        replay(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage, mockQueue);

        //runs the test
        QueuedMessageSenderResult result = useCase.handleSingleQueuedMessage(queueId);

        String okResult = "<queued-messages-send-result>\n" +
                          "    <succeededCount>1</succeededCount>\n" +
                          "</queued-messages-send-result>";
        assertEquals(okResult, result.asXml());


        //checks that all went well
        verify(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage, mockQueue);
    }

    /**
     * Test that queue state is updated to AOD and new entry in error table is created + email is sent
     */
    @Test
    public void testHandleSingleMessageFailed() throws Exception {

        Account mockAccount = createMock(Account.class);

        //Tests that  path when a message is already locked for sending a document
        final MessageNumber msgNo = MessageNumber.create(1);
        final OutboundMessageQueueId queueId = new OutboundMessageQueueId(10);
        QueuedOutboundMessage mockQueue = createMock("QueuedOutboundMessage", QueuedOutboundMessage.class);
        MessageMetaData mockMessage = EasyMock.createMock(MessageMetaData.class);

        SendQueuedMessagesUseCase useCase = new SendQueuedMessagesUseCase(mockDocumentSender, mockMessageRepository, mockQueueRepository, mockEmailService, mockAccountRepository);

        expectationsForQueueAndMessage(msgNo, queueId, mockQueue, mockMessage);

        String messageXml = "<xml>message</xml>";
        expect(mockMessageRepository.findDocumentByMessageNoWithoutAccountCheck(msgNo.toLong())).andStubReturn(messageXml);

        //set up expectations for messageRepo
        expect(mockQueueRepository.lockQueueItemForDelivery(queueId)).andStubReturn(true);

        // expect message to be sent by oxalis
        expect(mockDocumentSender.sendDocument(mockMessage, messageXml)).andThrow(new IllegalStateException("Exception simulation"));

        // update state to AOD
        mockQueueRepository.changeQueuedMessageState(queueId, OutboundMessageQueueState.AOD);

        //send error notification
        expect(mockAccountRepository.findAccountAsOwnerOfMessage(eu.peppol.persistence.api.MessageNumber.create(msgNo.toLong()))).andReturn(mockAccount);
        expect(mockEmailService.sendProcessingErrorNotification(mockAccount, "Exception simulation", msgNo)).andReturn(null);

        // new entry in error table
        expect(mockQueueRepository.logOutboundError(isA(QueuedOutboundMessageError.class))).andReturn(new OutboundMessageQueueErrorId(1));


        replay(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage, mockQueue, mockEmailService, mockAccountRepository, mockAccount);

        //runs the test
        QueuedMessageSenderResult result = useCase.handleSingleQueuedMessage(queueId);

        String failedResult = "<queued-messages-send-result>\n" +
                "    <succeededCount>0</succeededCount>\n" +
                "    <failed>\n" +
                "        <message>\n" +
                "            <queueId>10</queueId>\n" +
                "            <errorMessage>Unable to process queue item 10 with messageNo 1 sent; Exception simulation</errorMessage>\n" +
                "        </message>\n" +
                "    </failed>\n" +
                "</queued-messages-send-result>";
        assertEquals(failedResult, result.asXml());


        //checks that all went well
        verify(mockMessageRepository, mockDocumentSender, mockQueueRepository, mockMessage, mockQueue, mockEmailService, mockAccountRepository);
    }


    private void expectationsForQueueAndMessage(MessageNumber msgNo, OutboundMessageQueueId queueId, QueuedOutboundMessage mockQueue, MessageMetaData mockMessage) {
        expect(mockQueue.getMessageNumber()).andStubReturn(msgNo);

        expect(mockQueueRepository.getQueuedMessageById(queueId)).andStubReturn(mockQueue);
        expect(mockMessageRepository.findMessageByMessageNo(msgNo)).andStubReturn(mockMessage);

        //set up expectations for message and queue
        expect(mockMessage.getMsgNo()).andStubReturn(msgNo.toLong());
        expect(mockMessage.getDelivered()).andStubReturn(null); // message cannot be delivered
        expect(mockMessage.getPeppolHeader()).andStubReturn(getSimplePeppolHeader());
        expect(mockQueue.getOutboundQueueId()).andStubReturn(queueId);
    }

    private PeppolHeader getSimplePeppolHeader() {
        PeppolHeader ph = new PeppolHeader();
        ph.setPeppolChannelId(new PeppolChannelId("TEST_ONLY")); // channel is used during queue processing and has to be set
        return ph;
    }

}

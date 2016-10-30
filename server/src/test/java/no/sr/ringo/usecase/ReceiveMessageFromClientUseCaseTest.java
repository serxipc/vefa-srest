package no.sr.ringo.usecase;

import eu.peppol.persistence.api.UserName;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountId;
import eu.peppol.persistence.api.account.Customer;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.message.OutboundPostParams;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.queue.QueueRepository;
import no.sr.ringo.resource.InvalidUserInputWebException;
import no.sr.ringo.smp.RingoSmpLookup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.easymock.EasyMock.*;

/**
 * User: Adam
 * Date: 4/2/13
 * Time: 2:17 PM
 */
public class ReceiveMessageFromClientUseCaseTest {

    private Account account;
    private PeppolMessageRepository mockMessageRepository;
    private QueueRepository mockQueueRepository;
    private RingoSmpLookup mockSmpLookup;
    private EmailService mockEmailService;

    @BeforeMethod
    public void setUp() throws Exception {
        this.account = new Account(createCustomer(), new UserName("test"), new Date(), "pass", new AccountId(1), true, true);
        this.mockMessageRepository = createStrictMock(PeppolMessageRepository.class);
        this.mockQueueRepository = createStrictMock(QueueRepository.class);
        this.mockSmpLookup = createStrictMock(RingoSmpLookup.class);
        this.mockEmailService = createStrictMock(EmailService.class);
    }

    @Test(expectedExceptions = InvalidUserInputWebException.class)
    public void testValidationErrorSendsNotification() {

        ReceiveMessageFromClientUseCase useCase = new ReceiveMessageFromClientUseCase(account, mockMessageRepository, mockQueueRepository, mockSmpLookup, mockEmailService);
        expect(mockEmailService.sendUploadErrorNotification(account, "Wrong recipientId value: invalidRecipientId", null)).andReturn(null);

        replay(mockEmailService, mockMessageRepository, mockQueueRepository, mockSmpLookup);
        useCase.handleMessage(createParams());
        verify(mockEmailService, mockMessageRepository, mockQueueRepository, mockSmpLookup);

    }

    private OutboundPostParams createParams() {
        return new OutboundPostParams.Builder()
                .channelId("channelId")
                .documentId("invalidDocumentId")
                .processId("invalidProcessId")
                .recipientId("invalidRecipientId")
                .senderId("invalidSenderId")
                .uploadMode(UploadMode.BATCH.name())
                .build();
    }

    private Customer createCustomer() {
        return new Customer(1, "name", null, null, "test@sendregning.no", null, null, null, null, null, null, null);
    }

}

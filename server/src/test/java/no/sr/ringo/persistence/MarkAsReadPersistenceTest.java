/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.Account;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.PeppolMessageNotFoundException;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Integration test verifying that messages can be filtered by various params
 *
 * @author Adam Mscisz adam@sendregning.no
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class MarkAsReadPersistenceTest {

    private final AccountRepository accountRepository;
    private final DatabaseHelper databaseHelper;
    private final PeppolMessageRepository peppolMessageRepository;
    private final DbmsTestHelper dbmsTestHelper;

    private Long messageNo;
    private String receiver1 = ObjectMother.getAdamsParticipantId().stringValue();
    private Account account;
    private ParticipantId sender;

    @Inject
    public MarkAsReadPersistenceTest(AccountRepository accountRepository, DatabaseHelper databaseHelper, PeppolMessageRepository peppolMessageRepository, DbmsTestHelper dbmsTestHelper) {
        this.accountRepository = accountRepository;
        this.databaseHelper = databaseHelper;
        this.peppolMessageRepository = peppolMessageRepository;
        this.dbmsTestHelper = dbmsTestHelper;
    }

    /**
     * This test_ must be run as last one, because it creates new message which would impact other tests
     */
    @Test(groups = {"persistence"})
    public void testMarkAsRead() throws PeppolMessageNotFoundException {

        //we just need to create the account object with an id to pass it to findMessageMethod
        //fetch the message and make sure that delivered is null
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, messageNo);
        assertNull(messageByMessageNo.getDelivered());

        //mark as read (update delivered)
        peppolMessageRepository.markMessageAsRead(messageNo);

        //fetch the message again and verify that delivered is not null
        messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(account, messageNo);
        assertNotNull(messageByMessageNo.getDelivered());
    }

    @BeforeMethod
    public void setUp() throws Exception {
        account = accountRepository.createAccount(ObjectMother.getAdamsAccount(), ObjectMother.getAdamsParticipantId());
        sender = ObjectMother.getAdamsParticipantId();
        messageNo = dbmsTestHelper.createMessage(account.getAccountId().toInteger(), TransferDirection.IN, ObjectMother.getAdamsParticipantId().stringValue(), receiver1, UUID.randomUUID().toString(), null);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        databaseHelper.deleteAllMessagesForAccount(account);
        accountRepository.deleteAccount(account.getAccountId());
    }

}

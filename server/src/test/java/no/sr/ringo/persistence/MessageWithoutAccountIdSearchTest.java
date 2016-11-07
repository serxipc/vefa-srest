/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.AccountRepository;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.PeppolMessageRepository;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Integration test verifying that messages can be filtered by various params
 *
 * @author Adam Mscisz adam@sendregning.no
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class MessageWithoutAccountIdSearchTest {

    final PeppolMessageRepository peppolMessageRepository;
    final DatabaseHelper databaseHelper;

    @Inject
    public MessageWithoutAccountIdSearchTest(AccountRepository accountRepository, PeppolMessageRepository peppolMessageRepository, DatabaseHelper databaseHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.databaseHelper = databaseHelper;
    }

    @Test(groups = {"persistence"})
    public void testFindAllMessages() {
        List<MessageMetaData> messagesWithoutAccountId = peppolMessageRepository.findMessagesWithoutAccountId();
        assertEquals(1, messagesWithoutAccountId.size());
    }

    @BeforeMethod
    public void setUp() throws Exception {
        databaseHelper.deleteAllMessagesWithoutAccountId();
        databaseHelper.createMessage(null, TransferDirection.IN, "sender", "receiver", "12345", null);

    }

    @AfterMethod
    public void tearDown() throws Exception {
        List<MessageMetaData> messagesWithoutAccountId = peppolMessageRepository.findMessagesWithoutAccountId();

        databaseHelper.deleteAllMessagesWithoutAccountId();
    }
}

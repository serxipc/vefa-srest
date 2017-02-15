/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.WellKnownParticipant;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
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
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class MessageWithoutAccountIdSearchTest {


    final PeppolMessageRepository peppolMessageRepository;
    final DatabaseHelper databaseHelper;
    private final DbmsTestHelper dbmsTestHelper;

    @Inject
    public MessageWithoutAccountIdSearchTest(PeppolMessageRepository peppolMessageRepository, DatabaseHelper databaseHelper, DbmsTestHelper dbmsTestHelper) {
        this.peppolMessageRepository = peppolMessageRepository;
        this.databaseHelper = databaseHelper;
        this.dbmsTestHelper = dbmsTestHelper;
    }

    @Test(groups = {"persistence"})
    public void testFindAllMessages() {
        List<MessageMetaData> messagesWithoutAccountId = peppolMessageRepository.findMessagesWithoutAccountId();
        assertEquals(messagesWithoutAccountId.size(),1);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        databaseHelper.deleteAllMessagesWithoutAccountId();
        dbmsTestHelper.createMessage(null, TransferDirection.IN, WellKnownParticipant.U4_TEST.stringValue()  ,WellKnownParticipant.U4_TEST.stringValue() , "12345", null);

    }

    @AfterMethod
    public void tearDown() throws Exception {
        List<MessageMetaData> messagesWithoutAccountId = peppolMessageRepository.findMessagesWithoutAccountId();

        databaseHelper.deleteAllMessagesWithoutAccountId();
    }
}

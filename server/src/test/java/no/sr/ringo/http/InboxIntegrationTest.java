package no.sr.ringo.http;

import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Message;
import no.sr.ringo.client.Messages;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.UUID;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class InboxIntegrationTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(InboxIntegrationTest.class);
    private Long messageId;

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    PeppolMessageRepository peppolMessageRepository;


    @Test(groups = {"integration"})
    public void helloWorld() {
        System.out.println("Hello world");;

    }

    /**
     * Retrieves messages from inbox
     *
     * @throws java.io.IOException
     * @throws java.net.URISyntaxException
     */
    @Test(groups = {"integration"})
    public void count() throws IOException, URISyntaxException {

        Inbox inbox = ringoRestClientImpl.getInbox();

        Integer count = inbox.getCount();
        assertNotNull(count);

        assertTrue(count >= 0);
    }

    @Test(groups = {"integration"})
    public void testGetContentsOfInbox() throws Exception {

        Inbox inbox = ringoRestClientImpl.getInbox();

        final Messages messages = inbox.getMessages();

        int count = 0;
        for (Message message : messages) {
            count++;
        }
        assertTrue(count > 0, "Expected more than 0 messages");

    }

    @BeforeMethod(groups = {"integration"})
    public void insertSample() throws SQLException {
        final Account account = ObjectMother.getTestAccount();
        messageId = databaseHelper.createMessage(1, TransferDirection.IN, ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), ObjectMother.getTestParticipantIdForSMPLookup().stringValue(), UUID.randomUUID().toString(), null);
        MessageMetaData messageByMessageNo = peppolMessageRepository.findMessageByMessageNo(MessageNumber.create(messageId));

    }

    @AfterMethod(groups = {"integration"})
    public void deleteSample() throws SQLException {
        databaseHelper.deleteMessage(messageId);
    }
}


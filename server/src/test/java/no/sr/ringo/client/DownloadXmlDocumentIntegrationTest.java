package no.sr.ringo.client;

import com.google.inject.Inject;
import eu.peppol.persistence.TransferDirection;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountRepository;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.http.AbstractHttpClientServerTest;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.persistence.DbmsTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import static org.testng.Assert.assertTrue;

/**
 * Tests downloading an xml document from the ringo server
 * Created by IntelliJ IDEA.
 * User: adam
 * Date: 1/24/12
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class DownloadXmlDocumentIntegrationTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(DownloadXmlDocumentIntegrationTest.class);

    @Inject
    AccountRepository accountRepository;
    
    @Inject
    PeppolMessageRepository peppolMessageRepository;

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    DbmsTestHelper dbmsTestHelper;

    private Long messageNumber;

    @BeforeMethod(groups = {"integration"})
    public void setUp() throws Exception {
        Account account = accountRepository.findAccountByParticipantId(ObjectMother.getTestParticipantId());
        String uuid = UUID.randomUUID().toString();
        String receiver1 = "9908:976098897";
        messageNumber = dbmsTestHelper.createMessage(account.getAccountId().toInteger(), TransferDirection.IN, ObjectMother.getTestParticipantId().stringValue(), receiver1, uuid, null);
    }


    /**
     * Tests downloading of messages from the inbox and marking them all as read.
     */
    @Test(groups = {"integration"})
    public void testDownloadDocument() throws Exception {

        //fetch the inbox
        Inbox inbox = ringoRestClientImpl.getInbox();

        //get all the messages
        Messages messages = inbox.getMessages();
        //mark all the messages as read
        File directory = new File(System.getProperty("java.io.tmpdir"));
        boolean found = false;
        int i = 0;
        for (Message message : messages) {
            i++;
            //saves the file to the directory provided
            File downloadedFile = message.saveToDirectory(directory);

            log.info("downloaded file here " + downloadedFile);
            assertTrue(downloadedFile.exists());

            //check that the file contains "<test />"
            BufferedReader utf8 = null;

            try {
                utf8 = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(downloadedFile), RingoConstants.DEFAULT_CHARACTER_SET));

                final String line = utf8.readLine();
                if (line.equals("<test>\u00E5</test>")) {
                    found = true;
                    break;
                }
            }
            finally {
                if(utf8 != null)
                utf8.close();
            }
        }
        
        assertTrue(found, String.format("Did not download document number of messages was '%s'", i));
    }
}

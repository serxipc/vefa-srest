package no.sr.ringo.standalone.parser;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static no.sr.ringo.exception.NotifyingException.NotificationType;
import static org.testng.Assert.*;

/**
 * Tests the parameters
 * User: adam
 * Date: 1/27/12
 * Time: 9:21 AM
 */
public class RingoClientCommandLineParserTest {

    private static Logger log = LoggerFactory.getLogger(RingoClientCommandLineParserTest.class);

    //specifies production environment
    private static final String PROD = "-t";

    private static final String ADDRESS = "-a";
    private static final String USERNAME = "-u";
    private static final String PASSWORD = "-p";

    //available operations
    private static final String UPLOAD = "-l";
    private static final String UPLOAD_SINGLE = "-n";
    private static final String DOWNLOAD = "-d";
    private static final String SMP = "-s";

    //parameters for download
    private static final String INBOX = "-i";

    //parameters for upload
    private static final String OUTBOX = "-o";
    private static final String ARCHIVE = "-v";
    private static final String CHANNEL_ID = "-c";
    private static final String SENDER_ID = "-x";
    private static final String RECIPIENT_ID = "-r";
    private static final String FILENAME = "-f";
    private static final String PROXY_ADDRESS = "-q";
    private static final String PROXY_PORT = "-e";

    //params for SMP lookup
    private static final String PARTICIPANT_ID = "-z";


    //values used in test
    private static final String DOWNLOAD_DIR = "/tmp/download";
    private static final String ARCHIVE_DIR = "/tmp/archive";
    private static final String UPLOAD_DIR = "/tmp/upload";
    private static final String UPLOAD_FILE = "invoice.xml";



    RingoClientCommandLineParser parser;

    @BeforeMethod
    public void setUp() throws IOException {
        parser = new RingoClientCommandLineParser();
        prepareFolders();
    }

    @AfterMethod
    public void tearDown() {
        removeDirs();
    }

    @Test
    /**
     * Tests that when no address given, default one for test environment used
     */
    public void testDefaultAddress() throws URISyntaxException {
        RingoClientConnectionParams params = null;
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", DOWNLOAD, INBOX, DOWNLOAD_DIR});
            params = parser.extractConnectionParams();
        } catch (CommandLineParserException e) {
            fail();
        }

        assertEquals(new URI(RingoClientCommandLineParser.ADDRESS_TEST), params.getAccessPointURI());

    }

    @Test
    /**
     * Tests that when no address given, default one for prod environment used
     */
    public void testProdAddress() throws URISyntaxException {
        RingoClientConnectionParams params = null;
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", DOWNLOAD, INBOX, DOWNLOAD_DIR, PROD});
            params = parser.extractConnectionParams();

        } catch (CommandLineParserException e) {
            fail();
        }

        assertEquals(new URI(RingoClientCommandLineParser.ADDRESS_PROD), params.getAccessPointURI());
    }

    /**
     * Tests valid command line arguments for downloading invoices
     * @throws Exception
     */
    @Test
    public void testProperDownloadCommandLine() throws URISyntaxException {

        RingoClientConnectionParams connectionParams = null;
        RingoClientParams params = null;
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR});
            connectionParams = parser.extractConnectionParams();
            params = parser.extractOperationParams();

        } catch (CommandLineParserException e) {
            fail();
        }

        assertEquals("adam", connectionParams.getUsername());
        assertEquals("superSecretPassword", connectionParams.getPassword());
        assertEquals(new URI("http://ringo.domain.com"), connectionParams.getAccessPointURI());

        assertEquals(RingoClientParams.ClientOperation.DOWNLOAD, params.getOperation());
        assertEquals(new File(DOWNLOAD_DIR), params.getInboxPath());

        assertNull(params.getOutboxPath());
        assertNull(params.getArchivePath());

    }

    /**
     * Tests valid command line arguments for uploading invoices
     * @throws Exception
     */
    @Test
    public void testProperUploadInvoicesCommandLine() throws URISyntaxException, CommandLineParserException {

        RingoClientConnectionParams connectionParams = null;
        RingoClientParams params = null;

        parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId"});
        connectionParams = parser.extractConnectionParams();
        params = parser.extractOperationParams();

        assertEquals("adam", connectionParams.getUsername());
        assertEquals("superSecretPassword", connectionParams.getPassword());
        assertEquals(new URI("http://ringo.domain.com"), connectionParams.getAccessPointURI());

        assertEquals(RingoClientParams.ClientOperation.UPLOAD, params.getOperation());
        assertEquals(new File(UPLOAD_DIR), params.getOutboxPath());
        assertEquals(new File(ARCHIVE_DIR), params.getArchivePath());

        assertNull(params.getInboxPath());

    }

    /**
     * Tests valid command line arguments for single uploading
     * @throws Exception
     */
    @Test
    public void testProperSingleUploadInvoicesCommandLine() throws URISyntaxException {

        RingoClientConnectionParams connectionParams = null;
        RingoClientParams params = null;

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897"});
            connectionParams = parser.extractConnectionParams();
            params = parser.extractOperationParams();


        } catch (CommandLineParserException e) {
            log.error(e.getMessage());
            fail();
        }

        assertEquals("adam", connectionParams.getUsername());
        assertEquals("superSecretPassword", connectionParams.getPassword());
        assertEquals(new URI(RingoClientCommandLineParser.ADDRESS_TEST), connectionParams.getAccessPointURI());

        assertEquals(RingoClientParams.ClientOperation.UPLOAD_SINGLE, params.getOperation());
        assertEquals(new File(UPLOAD_DIR+"/"+UPLOAD_FILE), params.getOutboxPath());
        assertEquals(new File(ARCHIVE_DIR), params.getArchivePath());

        assertNull(params.getInboxPath());

    }

    /**
     * Tests valid command line arguments for smp lookup
     * @throws Exception
     */
    @Test
    public void testProperSMPLookupCommandLine() throws URISyntaxException {

        RingoClientParams params = null;
        RingoClientConnectionParams connectionParams = null;

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897"});
            connectionParams = parser.extractConnectionParams();
            params = parser.extractOperationParams();
        } catch (CommandLineParserException e) {
            assertEquals("a", e.getMessage());
        }

        assertEquals("adam", connectionParams.getUsername());
        assertEquals("superSecretPassword", connectionParams.getPassword());
        assertEquals(new URI("http://ringo.domain.com"), connectionParams.getAccessPointURI());

        assertEquals(RingoClientParams.ClientOperation.SMP_LOOKUP, params.getOperation());
        assertEquals(new ParticipantId(SchemeId.NO_ORGNR,"976098897"), params.getParticipantId());

        assertNull(params.getOutboxPath());
        assertNull(params.getInboxPath());
        assertNull(params.getArchivePath());

    }

    /**
     * Tests username missing
     */
    @Test
    public void testUserNameMissing() {

        try {
            parser.parseCommandLine(new String[]{PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, "/tmp/download"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Username required (--username)", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests username missing
     */
    @Test
    public void testPasswordMissing() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, "/tmp/download"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Password required (--password)", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests address missing
     */
    @Test
    public void testAddressMissing() {

        RingoClientConnectionParams connectionParams = null;
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", DOWNLOAD, INBOX, "/tmp/download"});
            connectionParams = parser.extractConnectionParams();
            parser.extractOperationParams();
        } catch (CommandLineParserException e) {
            fail();
        }
        //default address
        assertEquals(connectionParams.getAccessPointURI().toString(), RingoClientCommandLineParser.ADDRESS_TEST);

    }

    /**
     * Tests operation missing
     */
    @Test
    public void testOperationMissing() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", INBOX, "/tmp/download"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("One of: '--upload', '--uploadSingle', --download' '--smp' option required.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests wrong sender's participantId format for file upload
     * Country prefix missing
     * @throws Exception
     */
    @Test
    public void testWrongSendersParticipantIdFormatForSingleUpload() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Invalid senderId '976098897', valid format is: <4 digit agency code>:<Organisation identifier>", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    @Test
    public void testWrongRecipientIdFormatForSingleUpload() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Invalid recipientId '976098897', valid format is: <4 digit agency code>:<Organisation identifier>", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests channelId missing for upload
     * @throws Exception
     */
    @Test
    public void testChannelIdMissing(){

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("ChannelId required for file upload required (--channelId)", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }

    }



    /**
     * Tests wrong download folder
     * @throws Exception
     */
    @Test
    public void testWrongDownloadFolder() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, "notExistingFolder"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Specified download path 'notExistingFolder' does not exist or is not a directory", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

    }

    /**
     * Tests participantId missing for SMP lookup
     * @throws Exception
     */
    @Test
    public void testParticipantIdMissingForSMPLookup() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("ParticipantId required for smp lookup (--participantId)", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests wrong participantId formatfor SMP lookup
     * Country prefix missing
     * @throws Exception
     */
    @Test
    public void testWrongParticipantIdFormatForSMPLookup() {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Invalid participantId '976098897', valid format is: <4 digit agency code>:<Organisation identifier>", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests recipientId option without value for single file upload
     * @throws Exception
     */
    @Test
    public void testRecipientIdValueMissingForSingleUpload() throws URISyntaxException {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, OUTBOX, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID });
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Missing argument for option:r", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests wrong recipientId value for single file upload
     * @throws Exception
     */
    @Test
    public void testWrongRecipientIdValueForSingleUpload() throws URISyntaxException {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "123:45678"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Invalid recipientId '123:45678', valid format is: <4 digit agency code>:<Organisation identifier>", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests wrong file for single file upload
     * @throws Exception
     */
    @Test
    public void testWrongFileForSingleUpload() throws URISyntaxException {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+"notExisting", ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Specified file (/tmp/upload/notExisting) for single file upload doesn't exist or is not a file", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    /**
     * Tests wrong file for single file upload (a directory)
     * @throws Exception
     */
    @Test
    public void testDirectoryAsFileForSingleUpload() throws URISyntaxException {

        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Specified file (/tmp/upload) for single file upload doesn't exist or is not a file", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

    }

    @Test
    public void testOutboxSameAsArchive() {
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, UPLOAD_DIR, CHANNEL_ID, "ChannelId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Outbox path cannot be the same as archive path", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }
    }

    @Test
    public void testRecipientIdNotAllowedForMultipleFileUpload(){
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("RecipientId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }
    }

    @Test
    public void testSenderIdNotAllowedForMultipleFileUpload(){
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("SenderId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }
    }


    /******************************************************
     *      TEST DISALLOWED OPTION COMBINATIONS           *
     ******************************************************/


    @Test
    public void testParticipantIdNotAllowedForDownloadAndUpload() throws URISyntaxException {

        //download
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, PARTICIPANT_ID, "9908:9760988"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("ParticipantId option allowed only for SMP lookup.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

        //upload
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId", PARTICIPANT_ID, "9908:9760988"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("ParticipantId option allowed only for SMP lookup.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());

        }

        //single upload
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897", PARTICIPANT_ID, "9908:9760988"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("ParticipantId option allowed only for SMP lookup.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }

    @Test
    public void testInboxNotAllowedForUploadAndSMP() throws URISyntaxException {

        //upload
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId", INBOX, "inboxPath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Inbox path option allowed only for download.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }

        //single upload
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", UPLOAD_SINGLE, FILENAME, UPLOAD_DIR+"/"+UPLOAD_FILE, ARCHIVE, ARCHIVE_DIR, SENDER_ID, "9908:976098897", CHANNEL_ID, "ChannelId", RECIPIENT_ID, "9908:976098897", INBOX, "inboxPath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Inbox path option allowed only for download.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

        //smp lookup
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897", INBOX, "inboxPath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Inbox path option allowed only for download.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }

    @Test
    public void testOutboxNotAllowedForDownloadAndSMP() throws URISyntaxException {

        //download
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, OUTBOX, "outboxPath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Outbox path option allowed only for upload.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

        //smp lookup
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897", OUTBOX, "outboxPath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Outbox path option allowed only for upload.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }

    @Test
    public void testArchiveNotAllowedForDownloadAndSMP() throws URISyntaxException {

        //download
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, ARCHIVE, "archivePath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Archive path option allowed only for upload.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

        //smp lookup
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897", ARCHIVE, "archivePath"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Archive path option allowed only for upload.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }

    @Test
    public void testsRecipientIdAndSenderIdNotAllowedForMultipleUploadDownloadAndSmp() {

        //download recipientId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, RECIPIENT_ID, "RecipientId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("RecipientId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

        //download senderId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, SENDER_ID, "SenderId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("SenderId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.DOWNLOAD, e.getNotificationType());
        }

        //multiple upload recipientId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId", RECIPIENT_ID, "recipientId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("RecipientId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }

        //multiple upload senderId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", UPLOAD, OUTBOX, UPLOAD_DIR, ARCHIVE, ARCHIVE_DIR, CHANNEL_ID, "ChannelId", SENDER_ID, "senderId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("SenderId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }

        //smp lookup - recipientId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897", RECIPIENT_ID, "recipientId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("RecipientId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

        //smp lookup - senderId
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", SMP, PARTICIPANT_ID,  "9908:976098897", SENDER_ID, "senderId"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("SenderId option allowed only for single upload only. For multiple upload it will be extracted from xml file.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }


    /**
    * Tests valid proxy settings
    * @throws Exception
    */
    @Test
    public void testProperProxySettings() throws URISyntaxException {


        RingoClientConnectionParams params = null;
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, PROXY_ADDRESS, "127.0.0.1", PROXY_PORT, "8080"});
            params = parser.extractConnectionParams();
            parser.extractOperationParams();
        } catch (CommandLineParserException e) {
            fail();
        }
        assertEquals("127.0.0.1", params.getProxySettings().getAddress());
        assertEquals(Integer.valueOf(8080), params.getProxySettings().getPort());

    }

    /**
    * Tests invalid proxy settings
    * @throws Exception
    */
    @Test
    public void testWrongProxySettings() throws URISyntaxException {


        //port missing
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, PROXY_ADDRESS, "127.0.0.1"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("When using proxy, both address and port need to be specified.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }

        //address missing
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, PROXY_PORT, "8080"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("When using proxy, both address and port need to be specified.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }


        //port not a number
        try {
            parser.parseCommandLine(new String[]{USERNAME, "adam", PASSWORD, "superSecretPassword", ADDRESS, "http://ringo.domain.com", DOWNLOAD, INBOX, DOWNLOAD_DIR, PROXY_ADDRESS, "127.0.0.1", PROXY_PORT, "xxx"});
            parser.extractConnectionParams();
            parser.extractOperationParams();
            fail();
        } catch (CommandLineParserException e) {
            assertEquals("Wrong proxy port: xxx.", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }
    }


    private void prepareFolders() throws IOException {
        File downloadDir = new File(DOWNLOAD_DIR);
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        }
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
        File uploadFile = new File(uploadDir, UPLOAD_FILE);
        if (!uploadFile.exists()) {
            uploadFile.createNewFile();
        }

        File archiveDir = new File(ARCHIVE_DIR);
        if (!archiveDir.exists()) {
            archiveDir.mkdir();
        }
    }
    
    private void removeDirs() {
        File downloadDir = new File(DOWNLOAD_DIR);
        if (downloadDir.exists()) {
            downloadDir.delete();
        }
        File uploadDir = new File(UPLOAD_DIR);
        if (uploadDir.exists()) {
            File uploadFile = new File(uploadDir, UPLOAD_FILE);
            if (uploadFile.exists()) {
                uploadFile.delete();
            }

            uploadDir.delete();
        }
        File archiveDir = new File(ARCHIVE_DIR);
        if (archiveDir.exists()) {
            archiveDir.delete();
        }
    }





}

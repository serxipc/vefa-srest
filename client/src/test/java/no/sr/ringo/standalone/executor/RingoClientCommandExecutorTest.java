package no.sr.ringo.standalone.executor;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Message;
import no.sr.ringo.client.Messages;
import no.sr.ringo.client.RingoClient;
import no.sr.ringo.common.FileHelper;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.standalone.parser.RingoClientParams;
import org.easymock.EasyMock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;


/**
 * Tests the client executor.
 *
 * User: adam
 * Date: 1/27/12
 * Time: 1:29 PM
 */
public class RingoClientCommandExecutorTest  {

    private static final String DOWNLOAD_DIR = "/tmp/download";
    private static final String ARCHIVE_DIR = "/tmp/archive";
    private static final String UPLOAD_DIR = "/tmp/upload";
    private static final String FILENAME = "testFile.xml";
    private static final String UPLOADED_FILENAME_2 = "testFile_1.xml";
    private static final String UPLOADED_FILENAME_3 = "testFile_2.xml";
    private static final String ERR_FILENAME = "testFile.err";
    private File uploadFile;
    private File errorFile;
    private Message mockUploadMessage;
    private Message mockDownloadMessage;
    private ReceptionId receptionId;

    private Messages mockMessages;
    private Inbox mockInbox;

    private RingoClient client;
    private PrintStream mockStream;

    //will be used to create folder
    ParticipantId participantId = new ParticipantId(SchemeId.AT_VAT, "111111111");


    @BeforeMethod
    public void setUp() throws Exception {
        prepareFolders();
        client = EasyMock.createStrictMock(RingoClient.class);
        mockStream = createStrictMock(PrintStream.class);
        mockUploadMessage = createStrictMock(Message.class);
        receptionId = new ReceptionId();
        mockInbox = createStrictMock(Inbox.class);
        mockMessages = createStrictMock(Messages.class);
        mockDownloadMessage = createStrictMock(Message.class);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        removeDirs();
    }


    @Test
    public void testSMPLookup() throws IOException, CommandLineExecutorException {
        RingoClientParams params = prepareParamsForSMPLookup();

        expect(client.isParticipantRegistered(params.getParticipantId())).andReturn(true);
        mockStream.println("Participant 9908:976098897 is registered");
        mockStream.close();

        EasyMock.replay(client, mockStream);
        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

    }

    @Test
    //UploadMode.SINGLE used
    public void testSingleUpload() throws IOException, CommandLineExecutorException {
        prepareUploadFile(FILENAME);
        RingoClientParams params = prepareParamsForOutboxSingle();

        expect(client.send(uploadFile, params.getChannelId(), params.getSenderId(), params.getRecipientId(), UploadMode.SINGLE)).andReturn(mockUploadMessage);
        expect(mockUploadMessage.getReceptionId()).andReturn(receptionId);

        expectedOutputMessage("testFile.xml", "testFile.xml");

        replay(client, mockStream, mockUploadMessage);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        //verify that file was moved to archive
        File archivedFile = new File(ARCHIVE_DIR, FILENAME);
        assertTrue(archivedFile.exists());

    }

    @Test
    public void testUpload() throws IOException, CommandLineExecutorException {
        prepareUploadFile(FILENAME);

        RingoClientParams params = prepareParamsForOutbox();

        expect(client.send(uploadFile, params.getChannelId(), params.getSenderId(), params.getRecipientId(), UploadMode.BATCH)).andReturn(mockUploadMessage);
        expect(mockUploadMessage.getReceptionId()).andReturn(receptionId);

        expectedOutputMessage("testFile.xml", "testFile.xml");

        replay(client, mockStream, mockUploadMessage);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        //verify that file was moved to archive
        File archivedFile = new File(ARCHIVE_DIR, FILENAME);
        assertTrue(archivedFile.exists());

    }

    @Test
    // Expect archived file to be appended with underscore and a number (e.g. _1, _2...)
    public void uploadSameFileMultipleTimes() throws IOException, CommandLineExecutorException {
        prepareUploadFile(FILENAME);

        RingoClientParams params = prepareParamsForOutbox();

        expectationsForUpload(params);
        expectedOutputMessage("testFile.xml", "testFile.xml");
        expectedOutputMessage("testFile.xml", "testFile_1.xml");
        expectedOutputMessage("testFile.xml", "testFile_2.xml");

        replay(client, mockStream, mockUploadMessage);

        //upload file
        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        //upload file again
        prepareUploadFile(FILENAME);
        executor.execute();

        //upload file again
        prepareUploadFile(FILENAME);
        executor.execute();

        //verify that file was moved to archive
        File archivedFile = new File(ARCHIVE_DIR, FILENAME);
        assertTrue(archivedFile.exists());

        //verify that file was moved to archive
        archivedFile = new File(ARCHIVE_DIR, UPLOADED_FILENAME_2);
        assertTrue(archivedFile.exists());

        //verify that file was moved to archive
        archivedFile = new File(ARCHIVE_DIR, UPLOADED_FILENAME_3);
        assertTrue(archivedFile.exists());

    }

    @Test
    public void testUploadSpecifyingSender() throws IOException, CommandLineExecutorException {
        prepareUploadFile(FILENAME);

        RingoClientParams params = prepareParamsForOutbox();
        params.setSenderId(ParticipantId.valueOf("9908:976098897"));

        assertTrue(uploadFile.toString().endsWith(".xml"),"Ooops dow we have a threading problem?");
        expect(client.send(uploadFile, params.getChannelId(), params.getSenderId(), params.getRecipientId(), UploadMode.BATCH)).andReturn(mockUploadMessage);
        expect(mockUploadMessage.getReceptionId()).andReturn(receptionId);

        expectedOutputMessage("testFile.xml", "testFile.xml");

        replay(client, mockStream, mockUploadMessage);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        //verify that file was moved to archive
        File archivedFile = new File(ARCHIVE_DIR, FILENAME);
        assertTrue(archivedFile.exists());

    }

    @Test
    /**
     * Verifies that when executor fails for file exception message is written to .err file and file is skipped
     */
    public void testUploadFailure() throws IOException, CommandLineExecutorException {
        prepareUploadFile(FILENAME);

        RingoClientParams params = prepareParamsForOutbox();

        String errorMessage = "Something went wrong";
        expect(client.send(uploadFile, params.getChannelId(), params.getSenderId(), params.getRecipientId(), UploadMode.BATCH)).andThrow(new IllegalStateException(errorMessage));
        expect(mockUploadMessage.getReceptionId()).andReturn(receptionId);

        mockStream.println("Upload failed for file: testFile.xml. Creating corresponding error file");

        File errorFile = new File(params.getOutboxPath(), FILENAME.replaceAll("(?i)" + ExecutorPathHelper.FILE_EXTENSION, ".err"));
        mockStream.println("Created error file: " + errorFile.toURI());
        mockStream.println("Uploaded 0 file(s).");
        mockStream.println("Skipped 1 file(s). Information in .err files");
        mockStream.close();

        replay(client, mockStream, mockUploadMessage);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        //verify that file was not to archive
        File archivedFile = new File(ARCHIVE_DIR, FILENAME);
        assertFalse(archivedFile.exists());

        //verify that error file was created
        this.errorFile = new File(UPLOAD_DIR, ERR_FILENAME);
        assertTrue(this.errorFile.exists());

        //check it's contents
        try (FileInputStream fstream = new FileInputStream(this.errorFile)) {

            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String message = br.readLine();
            assertEquals(errorMessage, message);
        }

        // Removes the error file to prevent other tests from failing
        errorFile.delete();
    }

    @Test
    /*
     * Amongst others tests that directory for participantId is created
     */
    @SuppressWarnings("unchecked")
    public void testGetInbox() throws IOException, CommandLineExecutorException {
        Iterator<Message> mockIterator = createStrictMock(Iterator.class);

        RingoClientParams params = prepareParamsForInbox();

        expect(client.getInbox()).andReturn(mockInbox);
        expect(mockInbox.getCount()).andReturn(10);
        expect(mockInbox.getMessages()).andReturn(mockMessages);
        expect(mockInbox.getCount()).andReturn(0);
        expect(mockMessages.iterator()).andReturn(mockIterator);

        expect(mockIterator.hasNext()).andReturn(true);
        expect(mockIterator.next()).andReturn(mockDownloadMessage);
        expect(mockIterator.hasNext()).andReturn(false);

        ReceptionId receptionId = new ReceptionId();
        expect(mockDownloadMessage.getReceiver()).andReturn(participantId);
        expect(mockDownloadMessage.getReceptionId()).andStubReturn(receptionId);

        File expectedDir = new File(params.getInboxPath(), FileHelper.formatForFileName(participantId.stringValue()));

        expect(mockDownloadMessage.saveToDirectory(expectedDir)).andReturn(new File(DOWNLOAD_DIR, "fileName"));
        expect(mockDownloadMessage.markAsRead()).andReturn(true);
        expectLastCall();

        mockStream.println(String.format("Downloading message with UUID: %s", receptionId.toString()));

        mockStream.println("Downloaded 1 files to directory " + new File(DOWNLOAD_DIR).toString());
        mockStream.close();

        replay(client, mockStream, mockInbox, mockDownloadMessage, mockMessages, mockIterator);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

        assertTrue(expectedDir.exists());
        assertTrue(expectedDir.isDirectory());
    }


    @Test
    /*
     * Tests that message is printed when recipient directory error occurs (in this case it exist and will be a file)
     */
    @SuppressWarnings("unchecked")
    public void testGetInboxRecipientDirectoryFailure() throws IOException, CommandLineExecutorException {

        RingoClientParams params = RingoClientCommandExecutorTest.prepareParamsForInbox();

        //create expected subdir that's a file and will cause failure
        File fileAsExpectedDir= new File(params.getInboxPath(), participantId.stringValue().replace(":", "_"));
        fileAsExpectedDir.createNewFile();

        Iterator<Message> mockIterator = createStrictMock(Iterator.class);

        expect(client.getInbox()).andReturn(mockInbox);
        expect(mockInbox.getCount()).andReturn(10);
        expect(mockInbox.getMessages()).andReturn(mockMessages);
        expect(mockInbox.getCount()).andReturn(0);

        expect(mockMessages.iterator()).andReturn(mockIterator);

        expect(mockIterator.hasNext()).andReturn(true);
        expect(mockIterator.next()).andReturn(mockDownloadMessage);
        expect(mockIterator.hasNext()).andReturn(false);

        expect(mockDownloadMessage.getReceiver()).andReturn(participantId);
        mockStream.println("File for receiver " + fileAsExpectedDir.toURI() + " exists but it's not a directory");
        mockStream.println("Downloaded 0 files to directory " + params.getInboxPath());
        mockStream.close();

        replay(client, mockStream, mockInbox, mockDownloadMessage, mockMessages, mockIterator);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

    }

    @Test
    /**
     * Tests that message without UUID will be skipped
     */
    @SuppressWarnings("unchecked")
    public void testGetInboxMessageWithoutUUID() throws IOException, CommandLineExecutorException {
        Iterator<Message> mockIterator = createStrictMock(Iterator.class);

        RingoClientParams params = prepareParamsForInbox();

        expect(client.getInbox()).andReturn(mockInbox);
        expect(mockInbox.getCount()).andReturn(10);
        expect(mockInbox.getMessages()).andReturn(mockMessages);
        expect(mockInbox.getCount()).andReturn(0);

        expect(mockMessages.iterator()).andReturn(mockIterator);

        expect(mockIterator.hasNext()).andReturn(true);
        expect(mockIterator.next()).andReturn(mockDownloadMessage);
        expect(mockIterator.hasNext()).andReturn(false);

        UUID messageUUID = UUID.randomUUID();
        expect(mockDownloadMessage.getReceiver()).andReturn(participantId);
        expect(mockDownloadMessage.getReceptionId()).andStubReturn(null);
        expect(mockDownloadMessage.getMessageSelfUri()).andReturn("http://ringo.domain.com/inbox/10");

        mockStream.println("Skipping message 'http://ringo.domain.com/inbox/10' because it has no receptionId");
        mockStream.println("Downloaded 0 files to directory " + params.getInboxPath());
        mockStream.close();

        replay(client, mockStream, mockInbox, mockDownloadMessage, mockMessages, mockIterator);

        RingoClientCommandExecutor executor = new RingoClientCommandExecutor(mockStream, params, client);
        executor.execute();

    }

    /**
     * Prepares params for upload
     *
     * @return
     */
    private RingoClientParams prepareParamsForOutbox() {
        RingoClientParams params = new RingoClientParams();
        params.setOperation(RingoClientParams.ClientOperation.UPLOAD);
        params.setArchivePath(new File(ARCHIVE_DIR));
        params.setOutboxPath(new File(UPLOAD_DIR));
        params.setChannelId(new PeppolChannelId("ChannelId"));

        return params;
    }

    private RingoClientParams prepareParamsForOutboxSingle() {
        RingoClientParams params = prepareParamsForOutbox();
        params.setOutboxPath(uploadFile);
        params.setOperation(RingoClientParams.ClientOperation.UPLOAD_SINGLE);
        return params;
    }


    /**
     * Prepares params for getting inbox
     */
    public static RingoClientParams prepareParamsForInbox() {
        RingoClientParams params = new RingoClientParams();
        params.setOperation(RingoClientParams.ClientOperation.DOWNLOAD);
        params.setInboxPath(new File(DOWNLOAD_DIR));

        return params;
    }

    /**
     * Prepares params for SMP lookup
     */
    private RingoClientParams prepareParamsForSMPLookup() {
        RingoClientParams params = new RingoClientParams();

        params.setOperation(RingoClientParams.ClientOperation.SMP_LOOKUP);
        params.setParticipantId(ParticipantId.valueOf("9908:976098897"));

        return params;
    }

    private void prepareFolders() throws IOException {
        File downloadDir = new File(DOWNLOAD_DIR);
        if (!downloadDir.exists()) {
            downloadDir.mkdir();
        } else {
            for (File f : downloadDir.listFiles()) {
                f.delete();
            }
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        } else {
            for (File f : uploadDir.listFiles()) {
                f.delete();
            }
        }

        File archiveDir = new File(ARCHIVE_DIR);
        if (!archiveDir.exists()) {
            archiveDir.mkdir();
        } else {
            for (File f : archiveDir.listFiles()) {
                f.delete();
            }
        }
    }


    //create a file in upload dir
    private void prepareUploadFile(String filename) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        uploadFile = new File(uploadDir, filename);
        uploadFile.createNewFile();
    }

    private void removeDirs() {
        File downloadDir = new File(DOWNLOAD_DIR);
        if (downloadDir.exists()) {
            //subdirs
            if (downloadDir.isDirectory()) {
                for (File subdir : downloadDir.listFiles()) {
                    //files
                    if (subdir.isDirectory()){
                        for (File file : subdir.listFiles()) {
                            file.delete();
                        }
                    }
                    subdir.delete();
                }
            }
            downloadDir.delete();
        }
        File uploadDir = new File(UPLOAD_DIR);
        if (uploadDir.exists()) {
            if (uploadFile != null && uploadFile.exists()) {
                uploadFile.delete();
            }
            errorFile = new File(ERR_FILENAME);
            if (errorFile != null && errorFile.exists()) {
                errorFile.delete();
            }
            uploadDir.delete();
        }
        File archiveDir = new File(ARCHIVE_DIR);
        if (archiveDir.exists()) {
            File archivedFile = new File(archiveDir, FILENAME);
            if (archivedFile.exists()) {
                archivedFile.delete();
            }
            archiveDir.delete();
        }
    }

    private void expectationsForUpload(RingoClientParams params) {
        expect(client.send(uploadFile, params.getChannelId(), params.getSenderId(), params.getRecipientId(), UploadMode.BATCH)).andStubReturn(mockUploadMessage);
        expect(mockUploadMessage.getReceptionId()).andStubReturn(receptionId);
    }

    private void expectedOutputMessage(final String fileToUpload, final String actualFileName) {
        mockStream.println("Uploaded " + fileToUpload + ". Archived to " + actualFileName);
        mockStream.println("Uploaded 1 file(s).");
        mockStream.close();
    }
}

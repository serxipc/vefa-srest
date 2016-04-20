package no.sr.ringo.standalone.executor;

import no.sr.ringo.exception.NotifyingException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * User: Adam
 * Date: 2/1/12
 * Time: 1:12 PM
 */
public class ExecutorPathHelperTest {

    private static final String CUSTOM_PATH = "customTestPath";
    private final String CUSTOM_FILE = "customFile";

    @BeforeMethod
    public void setUp() throws Exception {
        deleteDirs();

    }

    @AfterMethod
    public void tearDown() throws Exception {
        deleteDirs();
    }

    @Test
    /**
     * Tests that default outbox path exists
     */
    public void testGetDefaultOutboxPath() throws Exception {
        File outbox = new File(ExecutorPathHelper.DEFAULT_OUTBOX_PATH);
        outbox.mkdir();
        assertTrue(outbox.exists());

        File outboxPath = ExecutorPathHelper.getOutboxPath(outbox, false);
        assertTrue(outboxPath.exists());
        assertTrue(outboxPath.isDirectory());
    }

    @Test
    /**
     * Test not existing outbox path
     */
    public void testNotExistingCustomOutboxPath(){
        File customOutbox = new File(CUSTOM_PATH);

        try {
            ExecutorPathHelper.getOutboxPath(customOutbox, false);
        } catch (CommandLineExecutorException e) {
            assertEquals("Specified upload path: 'customTestPath' doesn't exist", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotifyingException.NotificationType.BATCH_UPLOAD, e.getNotificationType());

        }

    }

    @Test
    /**
     * Test existing outbox path which is a file
     */
    public void testCustomOutboxPathExistsButIsAFile() throws IOException {
        File customOutbox = new File(CUSTOM_FILE);
        customOutbox.createNewFile();

        try {
            ExecutorPathHelper.getOutboxPath(customOutbox, false);
        } catch (CommandLineExecutorException e) {
            assertEquals("Specified outbox path exists (customFile) but is not a directory", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotifyingException.NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }finally {
            customOutbox.delete();
        }
    }

    @Test
    /**
     * Test existing archive path which is a file
     */
    public void testCustomArchivePathExistsButIsAFile() throws IOException {
        File customArchive = new File(CUSTOM_FILE);
        customArchive.createNewFile();

        try {
            ExecutorPathHelper.getArchivePath(customArchive, false);
        } catch (CommandLineExecutorException e) {
            assertEquals("Specified archive path exists (customFile) but is not a directory", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotifyingException.NotificationType.BATCH_UPLOAD, e.getNotificationType());
        }finally {
            customArchive.delete();
        }
    }

    @Test
    /**
     * Test existing archive path which is a file
     */
    public void testCustomArchivePathExistsButIsAFileForSingleUpload() throws IOException {
        File customArchive = new File(CUSTOM_FILE);
        customArchive.createNewFile();

        try {
            ExecutorPathHelper.getArchivePath(customArchive, true);
        } catch (CommandLineExecutorException e) {
            assertEquals("Specified archive path exists (customFile) but is not a directory", e.getMessage());
            assertFalse(e.isNotify());
            assertNull(e.getNotificationType());
        }finally {
            customArchive.delete();
        }
    }

    @Test
    /**
     * Test existing inbox path which is a file
     */
    public void testCustomInboxPathExistsButIsAFile() throws IOException {
        File customInbox = new File(CUSTOM_FILE);
        customInbox.createNewFile();

        try {
            ExecutorPathHelper.getInboxPath(customInbox);
        } catch (CommandLineExecutorException e) {
            assertEquals("Specified inbox path exists (customFile) but is not a directory", e.getMessage());
            assertTrue(e.isNotify());
            assertEquals(NotifyingException.NotificationType.DOWNLOAD, e.getNotificationType());
        }finally {
            customInbox.delete();
        }
    }

    @Test
    /**
     * Tests that custom outbox path exists
     */
    public void testGetCustomOutboxPath() throws Exception {
        File customOutbox = new File(CUSTOM_PATH);
        customOutbox.mkdir();
        assertTrue(customOutbox.exists());

        File outboxPath = ExecutorPathHelper.getOutboxPath(customOutbox, false);

        assertTrue(outboxPath.exists());
        assertTrue(outboxPath.isDirectory());
    }


    @Test
    /**
     * Tests creation of default inbox path
     */
    public void testCreateDefaultInboxPath() throws Exception {
        ExecutorPathHelper.getInboxPath(null);
        File file = new File(ExecutorPathHelper.DEFAULT_INBOX_PATH);
        assertTrue(file.exists());
        assertTrue(file.isDirectory());
    }

    @Test
    /**
     * Tests that default inbox path exists
     */
    public void testGetDefaultInboxPath() throws Exception {
        File inbox = new File(ExecutorPathHelper.DEFAULT_INBOX_PATH);
        inbox.mkdir();
        assertTrue(inbox.exists());

        File inboxPath = ExecutorPathHelper.getInboxPath(inbox);
        assertTrue(inboxPath.exists());
        assertTrue(inboxPath.isDirectory());
    }

    @Test
    /**
     * Test creation of custom inbox path
     */
    public void testCreateCustomInboxPath() throws Exception {
        File customOutbox = new File(CUSTOM_PATH);
        File inboxPath = ExecutorPathHelper.getInboxPath(customOutbox);

        assertTrue(inboxPath.exists());
        assertTrue(inboxPath.isDirectory());
    }

    @Test
    /**
     * Tests that custom inbox path exists
     */
    public void testGetCustomInboxPath() throws Exception {
        File customOutbox = new File(CUSTOM_PATH);
        customOutbox.mkdir();
        assertTrue(customOutbox.exists());

        File inboxPath = ExecutorPathHelper.getInboxPath(customOutbox);

        assertTrue(inboxPath.exists());
        assertTrue(inboxPath.isDirectory());
    }


    @Test
    /**
     * Tests creation of default archive path
     */
    public void testCreateDefaultArchivePath() throws Exception {
        ExecutorPathHelper.getArchivePath(null, true);
        File file = new File(ExecutorPathHelper.DEFAULT_ARCHIVE_PATH);
        assertTrue(file.exists());
        assertTrue(file.isDirectory());
    }

    @Test
    /**
     * Tests that default archive path exists
     */
    public void testGetDefaultArchivePath() throws Exception {
        File archive = new File(ExecutorPathHelper.DEFAULT_ARCHIVE_PATH);
        archive.mkdir();
        assertTrue(archive.exists());

        File inboxPath = ExecutorPathHelper.getArchivePath(archive, true);
        assertTrue(archive.exists());
        assertTrue(archive.isDirectory());
    }

    @Test
    /**
     * Test creation of custom inbox path
     */
    public void testCreateCustominboxPath() throws Exception {
        File customOutbox = new File(CUSTOM_PATH);
        File archivePath = ExecutorPathHelper.getArchivePath(customOutbox, true);

        assertTrue(archivePath.exists());
        assertTrue(archivePath.isDirectory());
    }

    @Test
    /**
     * Tests that custom archive path exists
     */
    public void testGetCustomArchivePath() throws Exception {
        File customOutbox = new File(CUSTOM_PATH);
        customOutbox.mkdir();
        assertTrue(customOutbox.exists());

        File archivePath = ExecutorPathHelper.getArchivePath(customOutbox, true);

        assertTrue(archivePath.exists());
        assertTrue(archivePath.isDirectory());
    }

    /**
     * Helper method that makes sure we don't have any directories used in test before and after test execution
     */
    private void deleteDirs() {

        //delete default dirs if exist
        File archivePath = new File(ExecutorPathHelper.DEFAULT_ARCHIVE_PATH);
        if (archivePath.exists()) {
            archivePath.delete();
        }

        File inboxPath = new File(ExecutorPathHelper.DEFAULT_INBOX_PATH);
        if (inboxPath.exists()) {
            inboxPath.delete();
        }

        File uploadPath = new File(ExecutorPathHelper.DEFAULT_OUTBOX_PATH);
        if (uploadPath.exists()) {
            uploadPath.delete();
        }

        //delete custom dir
        File customPath = new File(CUSTOM_PATH);
        if (customPath.exists()) {
            customPath.delete();
        }

        assertFalse(archivePath.exists());

        assertFalse(inboxPath.exists());

        assertFalse(archivePath.exists());

        assertFalse(customPath.exists());

    }
}

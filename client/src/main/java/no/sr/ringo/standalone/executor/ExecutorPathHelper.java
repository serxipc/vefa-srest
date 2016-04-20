package no.sr.ringo.standalone.executor;

import no.sr.ringo.exception.NotifyingException;

import java.io.File;

/**
 * User: Adam
 * Date: 2/1/12
 * Time: 12:53 PM
 *
 * Helper class handling verification/creation of directories
 *
 * TODO: Refactor this class to get rid of code duplication
 */
public class ExecutorPathHelper {

    protected static final String DEFAULT_INBOX_PATH = "inbox";
    protected static final String DEFAULT_ARCHIVE_PATH = "archive";
    protected static final String DEFAULT_OUTBOX_PATH = "outbox";
    public static final String FILE_EXTENSION = ".xml";

    /**
     * If specified outbox path exists - validate it, create otherwise
     */
    public static File getOutboxPath(File outboxPath, boolean singleFileUpload) throws CommandLineExecutorException {
        //return path to single file
        if (singleFileUpload) {
            if (!outboxPath.exists() || !outboxPath.isFile()) {
                throw new CommandLineExecutorException(String.format("File '%s' doesn't exist or not a file", outboxPath));
            }
            return outboxPath;

        } else {

            //output directory not specified, create and return
            if (outboxPath == null) {

                File uploadDir = new File(DEFAULT_OUTBOX_PATH);

                if (uploadDir.exists()) {
                    if (!uploadDir.isDirectory()) {
                        throw new CommandLineExecutorException(String.format("Default upload path: '%s' exists, but is not a directory", uploadDir), NotifyingException.NotificationType.BATCH_UPLOAD);
                    }
                } else {
                    throw new CommandLineExecutorException(String.format("Default upload path: '%s' doesn't exist", uploadDir), NotifyingException.NotificationType.BATCH_UPLOAD);
                }

                return uploadDir;
            } else {

                // directory specified, let's create one if doesn't exist

                if (outboxPath.exists()) {
                    if (!outboxPath.isDirectory()) {
                        throw new CommandLineExecutorException(String.format("Specified outbox path exists (%s) but is not a directory", outboxPath), NotifyingException.NotificationType.BATCH_UPLOAD);
                    }
                } else {
                    throw new CommandLineExecutorException(String.format("Specified upload path: '%s' doesn't exist", outboxPath), NotifyingException.NotificationType.BATCH_UPLOAD);
                }

                return outboxPath;
            }
        }
    }

    /**
     * If specified archive path exists - validate it, create otherwise
     */
    public static File getArchivePath(File archivePath, boolean singleUpload) throws CommandLineExecutorException {

        NotifyingException.NotificationType notificationType = !singleUpload ? NotifyingException.NotificationType.BATCH_UPLOAD : null;
        //archive directory not specified, create and return
        if (archivePath == null) {

            File archiveDir = new File(DEFAULT_ARCHIVE_PATH);

            if (archiveDir.exists()) {
                if (!archiveDir.isDirectory()) {
                    throw new CommandLineExecutorException(String.format("Default archive path: '%s' exists, but is not a directory", archiveDir), notificationType);
                }
            } else {
                boolean defaultCreated = archiveDir.mkdir();
                if (!defaultCreated) {
                    throw new CommandLineExecutorException(String.format("Failed to create default archive directory (%s)", archiveDir), notificationType);
                }
            }

            return archiveDir;
        } else {

            // directory specified, let's create one if doesn't exist
            if (archivePath.exists()) {
                if (!archivePath.isDirectory()) {
                    throw new CommandLineExecutorException(String.format("Specified archive path exists (%s) but is not a directory", archivePath), notificationType);
                }
            } else {
                boolean directoryCreated = archivePath.mkdir();
                if (!directoryCreated) {
                    throw new CommandLineExecutorException(String.format("Failed to create archive directory (%s)", archivePath), notificationType);
                }
            }

            return archivePath;
        }
    }

    /**
     * If specified inbox path exists - validate it, create otherwise
     */
    public static File getInboxPath(File inboxPath) throws CommandLineExecutorException {

        // inbox directory not specified, create and return
        if (inboxPath == null) {

            File inboxDir = new File(DEFAULT_INBOX_PATH);

            if (inboxDir.exists()) {
                if (!inboxDir.isDirectory()) {
                    throw new CommandLineExecutorException(String.format("Default inbox path: '%s' exists, but is not a directory", inboxDir), NotifyingException.NotificationType.DOWNLOAD);
                }
            } else {
                boolean defaultCreated = inboxDir.mkdir();
                if (!defaultCreated) {
                    throw new CommandLineExecutorException(String.format("Failed to create default inbox directory (%s)", inboxDir), NotifyingException.NotificationType.DOWNLOAD);
                }
            }

            return inboxDir;
        } else {

            // directory specified, let's create one if doesn't exist
            if (inboxPath.exists()) {
                if (!inboxPath.isDirectory()) {
                    throw new CommandLineExecutorException(String.format("Specified inbox path exists (%s) but is not a directory", inboxPath), NotifyingException.NotificationType.DOWNLOAD);
                }
            } else {
                boolean directoryCreated = inboxPath.mkdir();
                if (!directoryCreated) {
                    throw new CommandLineExecutorException(String.format("Failed to create inbox directory (%s)", inboxPath), NotifyingException.NotificationType.DOWNLOAD);
                }
            }

            return inboxPath;
        }
    }

    /**
     * Prepares archive file appending _<number> if it already exists
     */
    public static File prepareArchiveFile(File archiveDir, File file) {
        if (file == null) {
            throw new IllegalStateException("File to be archived cannot be null");
        }

        File result = new File(archiveDir, file.getName());
        boolean exists = result.exists();

        int numberToAppend = 0;
        while (exists) {
            numberToAppend++;
            String[] fileWithoutExtension = file.getName().split(FILE_EXTENSION);
            if (fileWithoutExtension == null || fileWithoutExtension.length == 0) {
                throw new IllegalStateException(String.format("Cannot extract filename before %s extension.", FILE_EXTENSION));
            }

            String archiveFileName = fileWithoutExtension[0].concat("_").concat(String.valueOf(numberToAppend)).concat(FILE_EXTENSION);
            result = new File(archiveDir, archiveFileName);
            exists = result.exists();
        }

        return result;
    }
}

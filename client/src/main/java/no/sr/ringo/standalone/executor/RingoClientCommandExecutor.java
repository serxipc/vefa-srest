package no.sr.ringo.standalone.executor;

import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Message;
import no.sr.ringo.client.RingoClient;
import no.sr.ringo.common.FileHelper;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.exception.NotifyingException;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.standalone.parser.RingoClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static no.sr.ringo.exception.NotifyingException.NotificationType;

/**
 * Object which is responsible for execution of client commands
 * <p/>
 * User: adam
 * Date: 1/27/12
 * Time: 1:27 PM
 */
public class RingoClientCommandExecutor {

    private static Logger log = LoggerFactory.getLogger(RingoClientCommandExecutor.class);


    private final RingoClient client;
    private final RingoClientParams params;
    private final PrintStream printStream;
    //used to inform the server we're operating in batch mode
    private UploadMode uploadMode;

    public RingoClientCommandExecutor(PrintStream printStream, RingoClientParams params, RingoClient client) {
        this.printStream = printStream;
        this.params = params;
        this.client = client;
    }

    /**
     * Invokes proper client's method basing on the operation specified in the parameters.
     */
    public void execute() throws CommandLineExecutorException {

        switch (params.getOperation()) {

            //Try to upload all files from given path
            case UPLOAD:
                uploadMode = UploadMode.BATCH;
                List<Message> uploadedFiles = new ArrayList<Message>();

                File archiveDir = ExecutorPathHelper.getArchivePath(params.getArchivePath(), false);
                File outboxPath = ExecutorPathHelper.getOutboxPath(params.getOutboxPath(), false);

                if (archiveDir.equals(outboxPath)) {
                    throw new CommandLineExecutorException("Outbox path cannot be the same as archive path", NotificationType.BATCH_UPLOAD);
                }

                int skipped = 0;
                for (File file : outboxPath.listFiles()) {
                    if (file.isFile()) {
                        boolean result = handleFileUpload(file, uploadedFiles, archiveDir);
                        if (!result) {
                            skipped++;
                        }
                    } else {
                        log.warn(String.format("Not uploading %s, because it's a directory".toString(), file));
                    }
                }

                printStream.println(String.format("Uploaded %d file(s).", uploadedFiles.size()));
                if (skipped > 0) {
                    printStream.println(String.format("Skipped %d file(s). Information in .err files", skipped));
                }
                printStream.close();

                break;

            //upload single file
            case UPLOAD_SINGLE:

                uploadMode = UploadMode.SINGLE;
                uploadedFiles = new ArrayList<Message>();
                archiveDir = ExecutorPathHelper.getArchivePath(params.getArchivePath(), true);
                outboxPath = ExecutorPathHelper.getOutboxPath(params.getOutboxPath(), true);

                if (archiveDir.equals(outboxPath)) {
                    throw new CommandLineExecutorException("Outbox path cannot be the same as archive path");
                }

                handleFileUpload(outboxPath, uploadedFiles, archiveDir);
                break;


            //Try to download all xml files into specified path
            case DOWNLOAD:
                File inboxPath = ExecutorPathHelper.getInboxPath(params.getInboxPath());

                if (log.isDebugEnabled()) {
                    log.debug("Trying to download messages from inbox");
                }


                int success = 0;
                int failed = 0;
                Inbox inbox = client.getInbox();
                //Fetch all messages in the inbox and save them to disk
                // until there are no more messages in the inbox or the ones remaining have failed
                // to be downloaded/marked as read
                while(inbox.getCount() > failed){
                    for (Message message : inbox.getMessages()) {
                        if (handleMessageDownload(message,inboxPath)) {
                            success++;
                        }
                        else {
                            failed++;
                        }
                    }
                }
                printStream.println(String.format("Downloaded %d files to directory %s", success, inboxPath));

                break;

            //Perform SMP lookup
            case SMP_LOOKUP:
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Checking if participant %s is registered", params.getPeppolParticipantId()));
                }

                boolean participantRegistered = client.isParticipantRegistered(params.getPeppolParticipantId());

                if (participantRegistered) {
                    printStream.println(String.format("Participant %s is registered", params.getPeppolParticipantId().stringValue()));
                } else {
                    printStream.println(String.format("Participant %s is NOT registered", params.getPeppolParticipantId().stringValue()));
                }

                printStream.close();
                break;
        }
    }

    /**
     * Downloads the message into a folder with the name of the recipients peppol
     * participant id and marks it as read.
     *
     * @param message the message to download.
     * @param downloadPath the root to where all files are to be downloaded
     * @return tue if succeeded
     */
    private boolean handleMessageDownload(Message message, File downloadPath) {

        //finds the directory for the recipient
        File participantDirectory = getReceiverDirectory(message.getReceiver(), downloadPath);
        if (participantDirectory == null) {
            return false;
        }

        if (message.getMessageUUID() == null) {
            printStream.println(String.format("Skipping message '%s' because it has no messageID", message.getMessageSelfUri()));
            return false;
        }
        try {
            printStream.println(String.format("Downloading message with UUID: %s", message.getMessageUUID().toString()));
            message.saveToDirectory(participantDirectory);
            boolean marked = message.markAsRead();
            if (!marked) {
                printStream.println(String.format("Message with UUID %s successfully downloaded, but marking as read failed.", message.getMessageUUID().toString()));
            }
            return marked;
        } catch (IOException e) {
            printStream.println(String.format("Unable to download message with UUID %s to directory %s", message.getMessageUUID().toString(), participantDirectory));
            return false;
        }

    }

    /**
     * Checks if directory for participant files exists and creates one if doesn't
     * @param inboxPath
     * @return
     */
    private File getReceiverDirectory(PeppolParticipantId participantId, File inboxPath) {
        String directoryName = FileHelper.formatForFileName(participantId.stringValue());

        File result = new File(inboxPath, directoryName);

        if (result.exists()) {
            if (!result.isDirectory()) {
                printStream.println(String.format("File for receiver %s exists but it's not a directory", result.toURI()));
                return null;
            }
            if (!result.canWrite()) {
                printStream.println(String.format("File for receiver %s exists but it's not a directory", result.toURI()));
                return null;
            }
        } else {
            //create the directory
            boolean created = result.mkdir();
            if (!created) {
                printStream.println(String.format("Unable to create directory %s ", result.toURI()));
                return null;
            }
        }

        return result;
    }

    /**
     * Uploads file and moves it to archive directory
     *
     * @param file
     * @return true if succeeded
     */
    private boolean handleFileUpload(File file, List<Message> uploadedFiles, File archiveDir) throws CommandLineExecutorException {


        if (!file.getAbsoluteFile().getName().toLowerCase().endsWith(ExecutorPathHelper.FILE_EXTENSION)) {
            printStream.println(String.format("Skipping file which is not an xml file: %s", file.toURI()));
            return false;
        }

        Message message = null;

        try {
            message = client.send(file, params.getChannelId(), params.getSenderId(), params.getRecipientId(), uploadMode);

            archiveFile(archiveDir, file, uploadMode);

            uploadedFiles.add(message);
        } catch (Exception e) {
            printStream.println(String.format("Upload failed for file: %s. Creating corresponding error file", file.getName()));
            createErrorFile(file, e, printStream);
            return false;
        }

        return true;

    }

    /**
     * Moves the file to archive path
     */
    private void archiveFile(File archiveDir, File file, UploadMode uploadMode) throws CommandLineExecutorException {

        File archiveFile = ExecutorPathHelper.prepareArchiveFile(archiveDir, file);

        boolean archived = file.renameTo(archiveFile);

        if (archived) {
            printStream.println(String.format("Uploaded %s. Archived to %s", file.getName(), archiveFile.getName()));
        } else {
            NotificationType notificationType = UploadMode.BATCH.equals(uploadMode) ? NotificationType.BATCH_UPLOAD : null;
            throw new CommandLineExecutorException(String.format("Didn't manage to move file: %s to archive dir: %s", file.getName(), archiveDir.getPath()), notificationType);
        }
    }

    /**
     * Creates error file for given file with failure reason.
     * The file name will be the same but will end with .err
     *
     * @param file
     * @param printStream
     */
    private void createErrorFile(File file, Exception exception, PrintStream printStream) throws CommandLineExecutorException {
        String message = exception.getMessage();
        String originalName = file.getName();
        String parentPath = file.getParent();
        String errorFileName = originalName.replaceAll("(?i)"+ExecutorPathHelper.FILE_EXTENSION, ".err");

        File errorFile = new File(parentPath, errorFileName);
        if (errorFile.exists()) {
            errorFile.delete();
        }
        try {
            if (!errorFile.createNewFile()) {
                throw new CommandLineExecutorException("Unable to create error file: : " + errorFile.toURI());
            }
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(errorFile)));
            out.println(message);
            //print the stack trace if debug is enabled
            if (log.isDebugEnabled()) {
                exception.printStackTrace(out);
            }
            out.close();

        } catch (IOException e) {
            throw new CommandLineExecutorException("Unable to write to error file: : " + errorFile.toURI());
        }

        printStream.println(String.format("Created error file: %s", errorFile.toURI()));

    }


}

package no.sr.ringo.client;

import no.sr.ringo.common.FileHelper;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides access to
 *
 * User: andy
 * Date: 1/27/12
 * Time: 10:13 AM
 */
public class Message {

    private final RingoService executor;
    private final MessageWithLocations messageWithLocations;
    private final PeppolHeader peppolHeader;

    /**
     * Creates a new Message object.
     * @param executor required to make further requests against the RestServer e.g. markAsRead
     * @param messageWithLocations the message itself.
     */
    public Message(RingoService executor, MessageWithLocations messageWithLocations) {
        this.executor = executor;
        this.messageWithLocations = messageWithLocations;
        this.peppolHeader = messageWithLocations.getPeppolHeader();
    }


    /**
     * Used only for testing.
     * @return
     */
    MessageWithLocations getContents() {
        return messageWithLocations;
    }

    /**
     * Marks this message as read.
     *
     * @return true if the operation was successfull, false otherwise
     */
    public boolean markAsRead() {
        return executor.markAsRead(messageWithLocations);
    }

    /**
     * Saves the document associated with this message to the given directory.
     * The filename of the file will be &lt;UUID&gt;.xml or &lt;UUID&gt;_(d).xml if previous one existed
     *
     * @param directory the directory where to save the file.
     * @return the created file.
     * @throws IOException
     */
    public File saveToDirectory(File directory) throws IOException {
        if (!directory.exists() && !directory.canWrite()) {
            throw new IOException(String.format("Unable to write to directory %s", directory.toURI()));
        }

        //creates the file based on the uuid of the message.
        String filename = FileHelper.checkFile(directory, getFileName());

        File file = new File(directory, filename);

        if (!file.createNewFile()) {
            throw new IOException("Unable to create file: " + file.toURI());
        }
        //make the request and stream the response to the file provided.
        executor.downloadMessage(messageWithLocations, new FileOutputStream(file));

        return file;
    }

    public PeppolParticipantId getReceiver() {
        return messageWithLocations.getPeppolHeader().getReceiver();
    }

    public String getMessageUUID() {
        return messageWithLocations.getUuid();
    }

    public String getMessageSelfUri() {
        return messageWithLocations.getSelfURI().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!messageWithLocations.equals(message.messageWithLocations)) return false;
        if (peppolHeader != null ? !peppolHeader.equals(message.peppolHeader) : message.peppolHeader != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageWithLocations.hashCode();
        result = 31 * result + (peppolHeader != null ? peppolHeader.hashCode() : 0);
        return result;
    }

    private String getFileName() {
        return FileHelper.formatForFileName(messageWithLocations.getUuid()) + ".xml";
    }
}

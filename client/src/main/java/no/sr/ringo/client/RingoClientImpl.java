package no.sr.ringo.client;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.common.UploadMode;
import no.sr.ringo.document.ClientPeppolDocument;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 *
 * User: andy
 * Date: 3/6/12
 * Time: 2:33 PM
 */
public class RingoClientImpl implements RingoClient {

    static final Logger log = LoggerFactory.getLogger(RingoClientImpl.class);

    protected final RingoService ringoService;

    /**
     * Sets up the client with the given configuration.
     *
     * @param config the configuration to use for the client, allows customisation of timeouts etc.
     * @param userName the name to use when logging in.
     * @param password the password to use when logging in.
     */
    public RingoClientImpl(RingoConfig config, String userName, String password) {
        this.ringoService = new RingoServiceRestImpl(config, userName, password);
    }

    /**
     * Allows the implementation of the ringo service to be injected. This is useful when
     * testing the client and substituting the service with a mock.
     *
     * @param ringoService
     */
    RingoClientImpl(RingoService ringoService) {
        this.ringoService = ringoService;
    }


    /** Creates a new Inbox instance */
    public Inbox getInbox(){
        return new Inbox(ringoService);
    }

    /** Creates a new instance of Messagebox */
    public Messagebox getMessageBox(){
        return new Messagebox(ringoService);
    }

    /**
     * Can the participant receive Invoice documents
     * i.e. Documents of type "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"
     * @param peppolParticipantId
     * @return
     */
    public boolean isParticipantRegistered(ParticipantId peppolParticipantId) {
        return ringoService.isParticipantRegistered(peppolParticipantId);
    }

    /**
     * Sends the given file to the recipient contained within the Document.
     * The Invoice document type urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0
     * and the Invoice Only (urn:www.cenbii.eu:profile:bii04:ver1.0) process are assumed.
     *
     * @param file              The file to upload
     * @param peppolChannelId   The id of the channel
     * @param senderIdPeppol    The participant id of the document sender the document
     * @param recipientIdPeppol The participant id of the recipient
     * @return the Message indicating that the file was uploaded
     */
    public Message send(File file, PeppolChannelId peppolChannelId, ParticipantId senderIdPeppol, ParticipantId recipientIdPeppol, UploadMode uploadMode) {

        ClientPeppolDocumentFactory clientPeppolDocumentFactory = new ClientPeppolDocumentFactory(file);
        ClientPeppolDocument peppolDocument = clientPeppolDocumentFactory.createDocument();
        PeppolHeader peppolHeader = clientPeppolDocumentFactory.createPeppolHeader(peppolChannelId, senderIdPeppol, recipientIdPeppol);

        return ringoService.sendDocument(peppolDocument, peppolHeader, uploadMode);
    }

    /**
     * Sends the document contained within the inputStream.
     * @param inputStream The stream containing the contents of the document.
     * @param peppolHeader Must contain all values.
     * @return the Message indicating that the file was uploaded
     */
    public Message send(InputStream inputStream, PeppolHeader peppolHeader) {

        ClientPeppolDocumentFactory clientPeppolDocumentFactory = new ClientPeppolDocumentFactory(inputStream);
        ClientPeppolDocument peppolDocument = clientPeppolDocumentFactory.createDocument();

        return ringoService.sendDocument(peppolDocument,peppolHeader, UploadMode.SINGLE);
    }

    public List<AcceptedDocumentTransfer> fetchAcceptedDocumentTransfers(ParticipantId peppolParticipantId, LocalName localName) {
        return ringoService.fetchAcceptedDocumentTransfers(peppolParticipantId, localName);
    }

    public RingoService getRingoService() {
        return ringoService;
    }
}

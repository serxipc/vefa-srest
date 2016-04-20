package no.sr.ringo.client;

import no.sr.ringo.document.ClientPeppolDocument;
import no.sr.ringo.document.FileClientPeppolDocument;
import no.sr.ringo.document.InputStreamPeppolDocument;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Creates Peppol Document objects from either a File or an InputStream
 */
public class ClientPeppolDocumentFactory {

    static final Logger log = LoggerFactory.getLogger(ClientPeppolDocumentFactory.class);

    protected final ClientPeppolDocument clientPeppolDocument;

    public ClientPeppolDocumentFactory(File file) {
        clientPeppolDocument = new FileClientPeppolDocument(file);
    }

    public ClientPeppolDocumentFactory(InputStream inputStream) {
        clientPeppolDocument = new InputStreamPeppolDocument(inputStream);
    }

    public ClientPeppolDocument createDocument() {
        return clientPeppolDocument;
    }

    public PeppolHeader createPeppolHeader(PeppolChannelId peppolChannelId, PeppolParticipantId senderIdPeppol, PeppolParticipantId recipientIdPeppol) {
        PeppolHeader peppolHeader = new PeppolHeader();
        peppolHeader.setPeppolChannelId(peppolChannelId);
        peppolHeader.setSender(senderIdPeppol);
        peppolHeader.setReceiver(recipientIdPeppol);

        peppolHeader = clientPeppolDocument.populate(peppolHeader);

        return peppolHeader;
    }


}

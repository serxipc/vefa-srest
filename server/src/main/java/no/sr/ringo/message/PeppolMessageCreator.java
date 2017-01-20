package no.sr.ringo.message;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.ProfileIdResolver;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.resource.InvalidUserInputWebException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * @author Adam
 * @author thore
 */
public class PeppolMessageCreator {

    static final Logger log = LoggerFactory.getLogger(PeppolMessageCreator.class);

    Account account;
    PeppolMessage peppolMessage;
    OutboundPostParams postParams;

    public PeppolMessageCreator(Account account, OutboundPostParams postParams) {
        this.account = account;
        this.peppolMessage = new PeppolMessage();
        this.postParams = postParams;
    }

    public PeppolMessage extractHeader() {
        extractChannelId();
        extractRecipient();
        extractSender();
        extractDocumentTypeId();
        extractProcessId();
        return peppolMessage;
    }

    public PeppolMessage extractDocument() {
        try {
            Document document = parseInputStream(postParams.getInputStream());
            peppolMessage.setXmlMessage(document);
        } catch (Exception e) {
            throw new InvalidUserInputWebException("Unable to parse the XML document", e);
        }
        return peppolMessage;
    }

    private void extractProcessId() {
        if (StringUtils.isNotBlank(postParams.getProcessIdString())) {
            ProfileIdResolver processIdResolver = new ProfileIdResolver();
            try {
                ProfileId profileId = processIdResolver.resolve(postParams.getProcessIdString());
                peppolMessage.getPeppolHeader().setProfileId(profileId);
            } catch (Exception e) {
                log.warn(String.format("Wrong processId value: %s", postParams.getProcessIdString()));
            }
        }
    }

    private void extractDocumentTypeId() {
        if (StringUtils.isNotBlank(postParams.getDocumentIdString())) {
            try {
                PeppolDocumentTypeId documentTypeId = PeppolDocumentTypeId.valueOf( postParams.getDocumentIdString());
                peppolMessage.getPeppolHeader().setPeppolDocumentTypeId(documentTypeId);
            } catch (Exception e) {
                log.warn(String.format("Cannot extractHeader documentId for value: %s", postParams.getDocumentIdString()));
            }
        }
    }

    private void extractRecipient() {
        ParticipantId receiver = ParticipantId.valueOf(postParams.getRecipientIdString());
        peppolMessage.getPeppolHeader().setReceiver(receiver);
    }

    private void extractSender() {
        ParticipantId senderId = ParticipantId.valueOf(postParams.getSenderIdString());
        peppolMessage.getPeppolHeader().setSender(senderId);
    }

    private void extractChannelId() {
        peppolMessage.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(postParams.getChannelIdString()));
    }

    private Document parseInputStream(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        InputSource source = new InputSource(new InputStreamReader(checkForUtf8BOM(inputStream), RingoConstants.DEFAULT_CHARACTER_SET));
        return documentBuilderFactory.newDocumentBuilder().parse(source);
    }

    /**
     * Makes sure that there is no bom in the inputstream
     */
    private static InputStream checkForUtf8BOM(InputStream inputStream) {
        try {
            return removeUtf8Bom(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static InputStream removeUtf8Bom(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
            }
        }
        return pushbackInputStream;
    }

}

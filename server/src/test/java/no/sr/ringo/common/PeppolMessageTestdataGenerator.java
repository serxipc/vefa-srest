package no.sr.ringo.common;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.message.MessageMetaDataImpl;
import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.message.MessageWithLocationsImpl;
import no.sr.ringo.message.PeppolMessage;
import no.sr.ringo.peppol.ChannelProtocol;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolProcessIdAcronym;
import no.sr.ringo.response.OutboxQueryResponse;
import no.sr.ringo.transport.TransferDirection;
import no.sr.ringo.usecase.ReceiveMessageFromClientUseCaseIntegrationTest;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * Produces sample XML responses as they would be generated from the Ringo server.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @author Thore Holmberg Johnsen thore@sendregning.no
 */
public class PeppolMessageTestdataGenerator {

    public static final String EHF_TEST_SEND_REGNING_HELSE_VEST2_XML = "ehf-test-SendRegning-HelseVest2.xml";
    public static final String EHF_TEST_USING_DEFAULT_NAMESPACE_XML = "ehf-test-dakantus_using_default_namespace.xml";

    protected static final ParticipantId SR_PPID = ParticipantId.valueOf("9908:976098897");

    public static MessageWithLocationsImpl outboundMesssageNotSent() {
        return sampleMessage(TransferDirection.OUT);
    }

    private static MessageWithLocationsImpl outboundMessageSent() {
        return sampleMessage(TransferDirection.OUT, new ResponseMessageCallBackModifier() {
            @Override
            public void modify(MessageMetaDataImpl messageMetaData) {
                messageMetaData.setDelivered(new Date());
            }
        });
    }

    // Simple helper method, prevents us from littering the code with second parameter being null.
    private static MessageWithLocationsImpl sampleMessage(TransferDirection transferDirection) {
        return sampleMessage(transferDirection, null);
    }

    private static MessageWithLocationsImpl sampleMessage(TransferDirection transferDirection, ResponseMessageCallBackModifier modifier) {
        MessageWithLocationsImpl m = new MessageWithLocationsImpl();

        MessageMetaDataImpl metaData = new MessageMetaDataImpl();
        metaData.setMsgNo(42L);
        metaData.setReceived(new Date());
        metaData.setTransferDirection(transferDirection);
        metaData.setAccountId(new AccountId(1));

        m.setMessageMetaData(metaData);

        m.getPeppolHeader().setPeppolChannelId(new PeppolChannelId("TEST"));
        m.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.EHF_INVOICE);
        m.getPeppolHeader().setProfileId(new ProfileId(PeppolProcessIdAcronym.INVOICE_ONLY.stringValue()));
        m.getPeppolHeader().setReceiver(SR_PPID);
        m.getPeppolHeader().setSender(ParticipantId.valueOf("9908:976098897"));

        String path = null;

        switch (transferDirection) {
            case OUT:
                path = "outbox";
                break;
            case IN:
                path = "inbox";
                break;
        }

        try {
            m.setSelf(new URI("https://localhost/peppol/" + path + "/" + m.getMsgNo()));
            m.setXmlDocument(new URI("https://localhost/peppol/" + path + "/" + m.getMsgNo() + "/xml-document"));
        } catch (URISyntaxException e) {
            /* ignore */
        }

        // Invokes the callback handler
        if (modifier != null) {
            modifier.modify(metaData);
        }

        return m;
    }

    public static PeppolMessage outboxPostRequest() {

        PeppolMessage peppolMessage = new PeppolMessage();
        peppolMessage.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(ChannelProtocol.SREST.name()));
        peppolMessage.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.EHF_INVOICE);
        peppolMessage.getPeppolHeader().setProfileId(new ProfileId(PeppolProcessIdAcronym.INVOICE_ONLY.stringValue()));
        peppolMessage.getPeppolHeader().setReceiver(SR_PPID);
        peppolMessage.getPeppolHeader().setSender(SR_PPID);

        // Retrieves the sample XML document, parses it and shoves it into the message.
        InputStream is = ReceiveMessageFromClientUseCaseIntegrationTest.class.getClassLoader().getResourceAsStream(EHF_TEST_SEND_REGNING_HELSE_VEST2_XML);
        assertNotNull(is, "Unable to find " + EHF_TEST_SEND_REGNING_HELSE_VEST2_XML + " in class path");
        Document document;
        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            document = dbf.newDocumentBuilder().parse(is);

        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse " + EHF_TEST_SEND_REGNING_HELSE_VEST2_XML + "; " + e, e);
        }
        peppolMessage.setXmlMessage(document); // Shove it into the message

        return peppolMessage;
    }

    public static PeppolMessage outboxPostRequestOfXmlUsingDefaultNamespaces() {
        PeppolMessage peppolMessage = new PeppolMessage();
        peppolMessage.getPeppolHeader().setPeppolChannelId(new PeppolChannelId("CH1"));
        peppolMessage.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.EHF_INVOICE);
        peppolMessage.getPeppolHeader().setProfileId(new ProfileId(PeppolProcessIdAcronym.INVOICE_ONLY.stringValue()));
        peppolMessage.getPeppolHeader().setReceiver(SR_PPID);
        peppolMessage.getPeppolHeader().setSender(SR_PPID);
        InputStream is = ReceiveMessageFromClientUseCaseIntegrationTest.class.getClassLoader().getResourceAsStream(EHF_TEST_USING_DEFAULT_NAMESPACE_XML);
        assertNotNull(is, "Unable to find " + EHF_TEST_USING_DEFAULT_NAMESPACE_XML + " in class path");
        Document document;
        try {

            //java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            //System.out.println(s.next());

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            document = dbf.newDocumentBuilder().parse(is);

        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse " + EHF_TEST_USING_DEFAULT_NAMESPACE_XML + "; " + e, e);
        }
        peppolMessage.setXmlMessage(document);
        return peppolMessage;
    }

    public static MessageWithLocationsImpl inBoundMesssageNotSent() {
        return sampleMessage(TransferDirection.IN);
    }

    public static OutboxQueryResponse outboxQueryResponseWithOneMessageNotSent() {
        // Creates a sample outbox query response as it would appear if we invoked the Ringo server
        return outboxQueryResponseFromMessage(outboundMesssageNotSent());
    }

    public static OutboxQueryResponse outboxQueryResponseWithOneMessageSent() {
        MessageWithLocationsImpl messageWithLocations = outboundMessageSent();
        return outboxQueryResponseFromMessage(messageWithLocations);
    }

    private static OutboxQueryResponse outboxQueryResponseFromMessage(MessageWithLocationsImpl messageWithLocations) {
        List<MessageWithLocations> list = new ArrayList<MessageWithLocations>();
        list.add(messageWithLocations);
        return new OutboxQueryResponse(list);
    }

    public interface ResponseMessageCallBackModifier {
        void modify(MessageMetaDataImpl messageMetaData);
    }

}

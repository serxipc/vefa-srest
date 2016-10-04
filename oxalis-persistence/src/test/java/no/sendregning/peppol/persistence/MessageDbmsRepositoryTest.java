package no.sendregning.peppol.persistence;

import eu.peppol.PeppolMessageMetaData;
import eu.peppol.identifier.*;
import eu.peppol.persistence.OxalisMessagePersistenceException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Steinar Overbeck Cook
 * @author Thore Holmberg Johnsen
 */
public class MessageDbmsRepositoryTest {

    private static final String MESSAGE_COMMENT = "This is a simple JUnit test";

    private MessageDbmsRepository messageDbmsRepository;

    @BeforeMethod
    public void setUp() {
        messageDbmsRepository = new MessageDbmsRepository();
    }

    @AfterClass
    public void cleanUp() throws SQLException {
        DataSource dataSource = messageDbmsRepository.getDataSource();
        Connection con = dataSource.getConnection();
        try {
            PreparedStatement delete = con.prepareStatement("delete from message where xml_message like '%" + MESSAGE_COMMENT + "%'");
            delete.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("failed " + e, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    throw new IllegalStateException("Unable to close connection " + e, e);
                }
            }
        }
    }

    @Test
    public void findAccountByParticipantId() {
        Integer accountId = messageDbmsRepository.srAccountIdForReceiver(new ParticipantId("9908:976098897"));
        assertEquals(accountId, Integer.valueOf(1));
    }

    @Test
    public void testSaveInboundMessage() throws Exception {
        PeppolMessageMetaData PeppolMessageMetaData = sampleMessageHeader();
        messageDbmsRepository.saveInboundMessage(PeppolMessageMetaData, sampeXmlDocumentAsInputStream());
    }

    /**
     * The Participant ID does not exist in the database, and will thus cause the account_id to be null
     * when persisting the message
     */
    @Test
    public void testSaveInboundMessageWithoutAccountId() throws Exception {
        PeppolMessageMetaData PeppolMessageMetaData = sampleMessageHeader();
        PeppolMessageMetaData.setRecipientId(new ParticipantId("9908:098765490"));
        messageDbmsRepository.saveInboundMessage(PeppolMessageMetaData, sampeXmlDocumentAsInputStream());
    }

    /**
     * Sending a message without the participant id of the sender is absolutely illegal.
     *
     * @throws ParserConfigurationException
     */
    @Test(expectedExceptions = OxalisMessagePersistenceException.class)
    public void testSaveInboundMessageWithoutSenderNull() throws ParserConfigurationException, OxalisMessagePersistenceException {
        PeppolMessageMetaData h = sampleMessageHeader();
        h.setSenderId(null);
        messageDbmsRepository.saveInboundMessage(h, sampeXmlDocumentAsInputStream());
    }

    /**
     * Sending a message without specifying the Participant ID of the receiver is an error.
     *
     * @throws ParserConfigurationException
     */
    @Test(expectedExceptions = OxalisMessagePersistenceException.class)
    public void testSaveInboundMessageWithoutRecipientIdNull() throws ParserConfigurationException, OxalisMessagePersistenceException {
        PeppolMessageMetaData h = sampleMessageHeader();
        h.setRecipientId(null);
        messageDbmsRepository.saveInboundMessage(h, sampeXmlDocumentAsInputStream());
    }

    @Test
    public void testSaveInboundMessageWithoutProcessId() throws ParserConfigurationException, OxalisMessagePersistenceException {
        PeppolMessageMetaData h = sampleMessageHeader();
        h.setProfileTypeIdentifier(null);
        messageDbmsRepository.saveInboundMessage(h, sampeXmlDocumentAsInputStream());
    }


    @Test(expectedExceptions = OxalisMessagePersistenceException.class)
    public void testSaveInboundMessageWithoutDocumentId() throws ParserConfigurationException, OxalisMessagePersistenceException {
        PeppolMessageMetaData h = sampleMessageHeader();
        h.setDocumentTypeIdentifier(null);
        messageDbmsRepository.saveInboundMessage(h, sampeXmlDocumentAsInputStream());
        fail("Expected exception to be thrown here");
    }

    @Test
    public void testTransformDocumentWithLSSerializer() throws Exception {
        byte[] bytes = transformDocumentWithLSSerializer(sampleXmlDocument());
        String result = new String(bytes);
        assertEquals(result, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<sr-invoice><!--This is a simple JUnit test--><person>Steinar O. Cook</person></sr-invoice>");
    }

    private Document sampleXmlDocument() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document sampleDocument = documentBuilder.newDocument();
        Element root = sampleDocument.createElement("sr-invoice");
        sampleDocument.appendChild(root);
        Comment comment = sampleDocument.createComment(MESSAGE_COMMENT);
        root.appendChild(comment);
        Element person = sampleDocument.createElement("person");
        root.appendChild(person);
        person.appendChild(sampleDocument.createTextNode("Steinar O. Cook"));
        return sampleDocument;
    }

    private InputStream sampeXmlDocumentAsInputStream() throws ParserConfigurationException {
        return new ByteArrayInputStream(transformDocumentWithLSSerializer(sampleXmlDocument()));
    }

    private PeppolMessageMetaData sampleMessageHeader() {
        PeppolMessageMetaData PeppolMessageMetaData = new PeppolMessageMetaData();
        PeppolMessageMetaData.setDocumentTypeIdentifier(PeppolDocumentTypeIdAcronym.INVOICE.getDocumentTypeIdentifier());
        PeppolMessageMetaData.setTransmissionId(new TransmissionId(UUID.randomUUID().toString()));
        PeppolMessageMetaData.setProfileTypeIdentifier(PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId());
        PeppolMessageMetaData.setRecipientId(new ParticipantId("9908:976098897"));
        PeppolMessageMetaData.setSenderId(new ParticipantId("9908:976098897"));
        PeppolMessageMetaData.setSendingAccessPoint(new AccessPointIdentifier("AP_TEST"));
        PeppolMessageMetaData.setSendingAccessPointPrincipal(new Principal() {
            @Override
            public String getName() {
                return "CN=APP_1000000001, O=SendRegning, C=NO";
            }
        });
        return PeppolMessageMetaData;
    }

    private byte[] transformDocumentWithLSSerializer(Document document) {
        DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplementationLS.createLSSerializer();
        LSOutput lsOutput = domImplementationLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        lsOutput.setByteStream(stream);
        serializer.write(document, lsOutput);
        return stream.toByteArray();
    }

}

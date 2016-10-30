package no.sr.ringo.usecase;

import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.email.EmailService;
import no.sr.ringo.client.ClientObjectMother;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.PeppolMessageRepository;
import no.sr.ringo.queue.QueueRepository;
import no.sr.ringo.smp.RingoSmpLookup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;

import static org.easymock.EasyMock.createStrictMock;
import static org.testng.Assert.assertEquals;

/**
 * @author mariusherring
 * @author thore
 */
public class ExtractInvoiceNoFromDocumentTest {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private String documentString;
    private ReceiveMessageFromClientUseCase receiveMessageFromClientUseCase;
    private Document document;

    @BeforeMethod
    public void setUp() throws Exception {
        this.documentString = readTestDocumentToFile();
        Account mockRingoAccount = createStrictMock(Account.class);
        RingoSmpLookup mockRingoSmpLookup = createStrictMock(RingoSmpLookup.class);
        QueueRepository mockQueueRepository = createStrictMock(QueueRepository.class);
        PeppolMessageRepository mockPeppolMessageRepository = createStrictMock(PeppolMessageRepository.class);
        EmailService mockEmailService = createStrictMock(EmailService.class);
        this.receiveMessageFromClientUseCase = new ReceiveMessageFromClientUseCase(mockRingoAccount, mockPeppolMessageRepository, mockQueueRepository, mockRingoSmpLookup, mockEmailService);
        this.document = parseInputStream(getStream());
    }

    @Test
    public void testExtractInvoiceNoFromDocument() throws Exception {
        String result = receiveMessageFromClientUseCase.extractInvoiceNoFromDocument(document);
        assertEquals(result, "test_invoice_no");
    }

    private String readTestDocumentToFile() throws URISyntaxException, IOException {
        File testFile = ClientObjectMother.getTestInvoice();
        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        return stringBuilder.toString();
    }

    private InputStream getStream() {
        try {
            return new ByteArrayInputStream(documentString.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Exception occurred when creating stream", e);
        }
    }

    private Document parseInputStream(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        InputSource source = new InputSource(new InputStreamReader(inputStream, RingoConstants.DEFAULT_CHARACTER_SET));
        return documentBuilderFactory.newDocumentBuilder().parse(source);
    }

}

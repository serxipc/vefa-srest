package no.sr.ringo.xml;

import no.sr.ringo.client.ClientObjectMother;
import no.sr.ringo.common.RingoConstants;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @author Adam
 * @author thore
 */
public class EhfEntityExtractorTest {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private Document document;
    private String documentString;

    private EhfEntityExtractor ehfEntityExtractor;

    public void setUpForDefaultNamespace() throws Exception {
        documentString = readTestDocumentToFile(ClientObjectMother.getTestInvoice());
        document = parseInputStream(getStream());
        ehfEntityExtractor = new EhfEntityExtractor(document);
    }

    public void setUpForLowerCaseCbcId() throws Exception {
        documentString = readTestDocumentToFile(ClientObjectMother.getTestInvoiceWithLowercaseCbcId());
        document = parseInputStream(getStream());
        ehfEntityExtractor = new EhfEntityExtractor(document);
    }

    public void setUpForDifferentNamespace() throws Exception {
        documentString = readTestDocumentToFile(ClientObjectMother.getTestInvoiceWithDifferentNamespace());
        document = parseInputStream(getStream());
        ehfEntityExtractor = new EhfEntityExtractor(document);
    }

    @Test
    public void testExtractEntity() throws Exception {
        setUpForDefaultNamespace();
        String invoiceNo = ehfEntityExtractor.extractInvoiceNo();
        assertEquals("test_invoice_no", invoiceNo);
    }

    @Test
    public void testExtractEntityWithLowercaseCbcId() throws Exception {
        setUpForLowerCaseCbcId();
        String invoiceNo = ehfEntityExtractor.extractInvoiceNo();
        assertEquals("test_invoice_no", invoiceNo);
    }

    @Test
    public void testExtractEntityWithDifferentNamespace() throws Exception {
        setUpForDifferentNamespace();
        String invoiceNo = ehfEntityExtractor.extractInvoiceNo();
        assertEquals("9090115226", invoiceNo);
    }

    private String readTestDocumentToFile(File testFile) throws URISyntaxException, IOException {
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

    private Document parseInputStream(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        InputSource source = new InputSource(new InputStreamReader(inputStream, RingoConstants.DEFAULT_CHARACTER_SET));
        return documentBuilderFactory.newDocumentBuilder().parse(source);
    }

    private InputStream getStream() {
        try {
            return new ByteArrayInputStream(documentString.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Exception occurred when creating stream", e);

        }
    }

}

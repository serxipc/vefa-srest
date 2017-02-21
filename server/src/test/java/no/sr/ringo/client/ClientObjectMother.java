package no.sr.ringo.client;

import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.account.Account;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.http.AbstractHttpClientServerTest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.testng.Assert;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 3:47 PM
 */
public class ClientObjectMother {

    /**
     * Creates a Http post for the given account
     * @param account sender of document
     * @param receiver person that shall receiver document
     * @return
     */
    public static HttpPost createOutboxPostRequest(Account account, ParticipantIdentifier receiver, ParticipantIdentifier sender) {
        try {
            HttpPost httpPost = new HttpPost(AbstractHttpClientServerTest.PEPPOL_BASE_REST_URL + "/outbox/");
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            final File file = getTestInvoice();

            multipartEntity.addPart("file", new FileBody(file, "application/xml"));
            multipartEntity.addPart("RecipientID", new StringBody(receiver.getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("SenderID", new StringBody(sender.getIdentifier(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ChannelID", new StringBody("CHTEST", "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("ProcessID", new StringBody(PeppolProcessTypeIdAcronym.INVOICE_ONLY.name(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));
            multipartEntity.addPart("DocumentID", new StringBody(PeppolDocumentTypeIdAcronym.INVOICE.name(), "text/plain", Charset.forName(RingoConstants.DEFAULT_CHARACTER_SET)));

            httpPost.setEntity(multipartEntity);
            return httpPost;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create outbox post request", e);
        }
    }


    public static File getTestInvoice() throws URISyntaxException {
        URL url = OutboxIntegrationTest.class.getClassLoader().getResource("ehf-test-sendregning.xml");
        Assert.assertNotNull(url, "Test file ehf-test-SendRegning.xml not found in class path");
        return new File(url.toURI());
    }

    public static File getTestInvoiceWithDifferentNamespace() throws URISyntaxException {
        URL url = OutboxIntegrationTest.class.getClassLoader().getResource("ehf-test-sendregning-ns3.xml");
        Assert.assertNotNull(url, "Test file ehf-test-SendRegning.xml not found in class path");
        return new File(url.toURI());
    }

    public static File getTestInvoiceInvalid() throws URISyntaxException {
        URL url = OutboxIntegrationTest.class.getClassLoader().getResource("ehf-test-sendregning-invalid.xml");
        Assert.assertNotNull(url, "Test file ehf-test-SendRegning-invalid.xml not found in class path");
        return new File(url.toURI());
    }

    public static File getTestCreditNote() throws URISyntaxException {
        URL url = OutboxIntegrationTest.class.getClassLoader().getResource("ehf-test-SendRegning-creditNote.xml");
        Assert.assertNotNull(url, "Test file ehf-test-SendRegning-creditNote.xml not found in class path");
        return new File(url.toURI());
    }

    public static File getTestInvoiceWithLowercaseCbcId() throws URISyntaxException {
        URL url = OutboxIntegrationTest.class.getClassLoader().getResource("ehf-test-sendregning_lowercase_cbc_id.xml");
        Assert.assertNotNull(url, "Test file ehf-test-SendRegning.xml not found in class path");
        return new File(url.toURI());
    }

}

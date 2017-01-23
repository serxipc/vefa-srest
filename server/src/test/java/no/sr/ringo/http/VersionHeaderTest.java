package no.sr.ringo.http;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import no.sr.ringo.client.ClientObjectMother;
import no.sr.ringo.client.Inbox;
import no.sr.ringo.client.Messagebox;
import no.sr.ringo.client.RingoServiceRestImpl;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.RingoLoggingStream;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URISyntaxException;

import static org.easymock.EasyMock.*;

/**
 * @author adam
 */
//@Guice(modules = {TestDataSourceModule.class,RingoServiceModule.class})
@Guice(moduleFactory = TestModuleFactory.class)

public class VersionHeaderTest extends AbstractHttpClientServerTest {

    static final Logger log = LoggerFactory.getLogger(VersionHeaderTest.class);
    private PeppolDocumentTypeId INVOICE;
    private RingoLoggingStream mockOutputStream;

    @BeforeMethod
    protected void setUp() throws Exception {
        INVOICE = PeppolDocumentTypeId.EHF_INVOICE;
        mockOutputStream = createLoggingStream();
        config.setRingoLoggingStream(mockOutputStream);
        expectRingoClientIsOutOfDate();
        setUpUserAgentOnHttpClient();
    }

    @Test(groups = {"integration"})
    public void testOutOfDateInbox() throws IOException, URISyntaxException {

        Inbox inbox = (Inbox) ringoRestClientImpl.getInbox();
        Integer count = inbox.getCount();

        verify(mockOutputStream);
    }


    @Test(groups = {"integration"})
    public void testOutOfDateMessages() throws IOException, URISyntaxException {

        Messagebox messages = ringoRestClientImpl.getMessageBox();
        messages.getMessages();

        verify(mockOutputStream);
    }

    /**
     * Tests that when the client is out of date a message will be printed to the OutputStream providing the link to the download.
     */
    @Test(groups = {"integration"})
    public void testOutOfDateOutbox() throws IOException, URISyntaxException {

        ParticipantId participantId = new ParticipantId(SchemeId.NO_ORGNR, "976098897");

        final ReaderInputStream readerInputStream = getTestInvoiceAsStream();
        ringoRestClientImpl.send(readerInputStream, PeppolHeader.forDocumentType(INVOICE, participantId, participantId));

        verify(mockOutputStream);
    }


    private ReaderInputStream getTestInvoiceAsStream() throws URISyntaxException, UnsupportedEncodingException, FileNotFoundException {
        File file = ClientObjectMother.getTestInvoice();
        return new ReaderInputStream(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    }

    private RingoLoggingStream expectRingoClientIsOutOfDate() {
        mockOutputStream.println("New version of ringo client is available for download at: " + RingoConstants.CLIENT_DOWNLOAD_URL);
        expectLastCall();
        replay(mockOutputStream);
        return mockOutputStream;
    }

    private void setUpUserAgentOnHttpClient() {
        RingoServiceRestImpl ringoService = (RingoServiceRestImpl) ringoRestClientImpl.getRingoService();
        final HttpClient httpClient = ringoService.getHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpProtocolParams.setUserAgent(params, String.format("%s (Version: 0.0.1)", RingoConstants.USER_AGENT));
    }

    private RingoLoggingStream createLoggingStream() {
        RingoLoggingStream mockOutputStream = EasyMock.createStrictMock(RingoLoggingStream.class);
        config.setRingoLoggingStream(mockOutputStream);
        return mockOutputStream;
    }

}


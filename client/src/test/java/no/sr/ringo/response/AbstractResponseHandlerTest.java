package no.sr.ringo.response;

import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.RingoConstants;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicStatusLine;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.easymock.EasyMock.expect;
import static org.testng.Assert.assertNotNull;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 12:55 PM
 */
public class AbstractResponseHandlerTest {
    protected HttpResponse mockResponse;
    protected HttpRequestBase mockRequest;
    protected RingoService mockRingoService;
    protected BasicStatusLine okStatus = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "OK");
    protected BasicStatusLine okCreatedStatus = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 201, "CREATED");
    protected BasicStatusLine notOkStatus = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 500, "Internal server error");
    protected BasicStatusLine badRequestStatus = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 400, "Bad request");
    protected BasicStatusLine noContentStatus = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 204, "NO_CONTENT");

    @BeforeMethod
    public void setUp() throws Exception {
        mockRequest = EasyMock.createStrictMock(HttpGet.class);
        mockResponse = EasyMock.createStrictMock(HttpResponse.class);
        mockRingoService = EasyMock.createStrictMock(RingoService.class);
    }

    protected void createOkResponseWithTestFile(String fileLocation) throws URISyntaxException {
        FileEntity entity = getTestFile(fileLocation);
        expect(mockResponse.getStatusLine()).andStubReturn(okStatus);
        expect(mockResponse.getEntity()).andStubReturn(entity);
    }

    protected FileEntity getTestFile(String fileLocation) throws URISyntaxException {
        URL testResultFileUri = MessageListResponseHandlerTest.class.getResource(fileLocation);
        assertNotNull(testResultFileUri);
        final File testFile = new File(testResultFileUri.toURI());
        return new FileEntity(testFile, RingoConstants.DEFAULT_CHARACTER_SET);
    }
}

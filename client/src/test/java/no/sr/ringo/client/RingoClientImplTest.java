package no.sr.ringo.client;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import no.sr.ringo.standalone.DefaultRingoConfig;
import org.apache.http.params.HttpProtocolParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 2/24/12
 * Time: 2:53 PM
 */
public class RingoClientImplTest {

    static final Logger log = LoggerFactory.getLogger(RingoClientImplTest.class);
    private RingoService mockRingoService;

    @BeforeMethod
    public void setUp() throws Exception {
        mockRingoService = createStrictMock(RingoService.class);
    }

    @Test
    public void testUserAgent() throws Exception {
        RingoServiceRestImpl impl = new RingoServiceRestImpl(new DefaultRingoConfig("http://blahh", null), "test", "password");
        final String userAgent = HttpProtocolParams.getUserAgent(impl.getHttpClient().getParams());
        Pattern pattern = Pattern.compile("SendRegning ringo client \\(Version:\\s?([^\\)]+)\\)");
        final Matcher matcher = pattern.matcher(userAgent);
        assertTrue(matcher.matches());
        final String versionNumber = matcher.group(1);
        assertNotNull(versionNumber);
        log.debug("Found: " + versionNumber);
    }

    @Test
    public void testFetchAcceptedDocumentTransfers() {
        RingoClientImpl ringoClient = new RingoClientImpl(mockRingoService);
        ParticipantId peppolParticipantId = ParticipantId.valueOf("NO976098897");
        LocalName localName = LocalName.Invoice;
        expect(mockRingoService.fetchAcceptedDocumentTransfers(peppolParticipantId, localName)).andReturn(Collections.<AcceptedDocumentTransfer>emptyList());
        replay(mockRingoService);
        List<AcceptedDocumentTransfer> result = ringoClient.fetchAcceptedDocumentTransfers(peppolParticipantId, localName);
        assertEquals(result,Collections.emptyList());
        verify(mockRingoService);
    }

}

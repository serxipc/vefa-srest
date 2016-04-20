package no.sr.ringo.response;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.*;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.testng.annotations.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

public class AcceptedDocumentTransfersResponseHandlerTest extends AbstractResponseHandlerTest {

    @Test
    public void testFetchAcceptedDocumentTransfersResponseHandler() throws Exception {

        createOkResponseWithTestFile("/acceptedDocumentTransfers.xml");

        AcceptedDocumentTransfersRingoResponseHandler handler = new AcceptedDocumentTransfersRingoResponseHandler(mockRingoService);

        replayAllMocks();

        final List<AcceptedDocumentTransfer> list = handler.handleResponse(mockResponse);

        verifyAllMocks();

        PeppolDocumentTypeId peppolDocumentTypeId = new PeppolDocumentTypeId(
                RootNameSpace.INVOICE,
                LocalName.Invoice,
                CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T010_INVOICE_V1 + ":#" + ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY + "#" + ProfileId.Predefined.EHF_INVOICE),
                "2.0");
        ProfileId profileId = ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY;

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(peppolDocumentTypeId, list.get(0).getDocumentTypeId());
        assertEquals(profileId, list.get(0).getProfileId());
    }

    @Test
    public void testFetchAcceptedDocumentTransfersResponseHandlerFailure() throws Exception {

        AcceptedDocumentTransfersRingoResponseHandler handler = new AcceptedDocumentTransfersRingoResponseHandler(mockRingoService);

        expect(mockResponse.getStatusLine()).andStubReturn(noContentStatus);
        //when no content is returned from the server the entity is in fact null.
        expect(mockResponse.getEntity()).andStubReturn(null);

        replay(mockRequest, mockResponse);

        List<AcceptedDocumentTransfer> acceptedDocumentTransfers = handler.handleResponse(mockResponse);
        assertTrue(acceptedDocumentTransfers.isEmpty());

    }


    private void verifyAllMocks() {
        verify(mockRequest, mockResponse, mockRingoService);
    }

    private void replayAllMocks() {
        replay(mockRequest, mockResponse, mockRingoService);
    }


}

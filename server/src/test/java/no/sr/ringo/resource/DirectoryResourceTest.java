package no.sr.ringo.resource;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.SmpLookupResult;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.*;

@Guice(moduleFactory = TestModuleFactory.class)
public class DirectoryResourceTest {

    private RingoSmpLookup mockRingoSmpLookup;

    DirectoryResource directoryResource;
    ParticipantId participantId = ObjectMother.getTestParticipantIdForSMPLookup();

    @BeforeMethod
    public void setUp() throws Exception {
        mockRingoSmpLookup = EasyMock.createStrictMock(RingoSmpLookup.class);
        directoryResource = new DirectoryResource(mockRingoSmpLookup, ObjectMother.getTestAccount());
    }

    @Test
    public void testNoMetaDataReturns204() throws Exception {

        List<PeppolDocumentTypeId> acceptedDocuments = Collections.emptyList();
        prepareMetaData(acceptedDocuments);

        Response result = directoryResource.getDocumentTypes(null, participantId.stringValue(), "Invoice");

        assertEquals(result.getStatus(), 204);

        verify(mockRingoSmpLookup);

    }

    @Test
    public void testGetSmpMetadataReturnsXML() throws Exception {

        List<PeppolDocumentTypeId> acceptedDocuments = new ArrayList<PeppolDocumentTypeId>();
        String invoiceDocumentType = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0";
        acceptedDocuments.add(PeppolDocumentTypeId.valueOf(invoiceDocumentType));

        prepareMetaData(acceptedDocuments);

        Response result = directoryResource.getDocumentTypes(null, participantId.stringValue(), "Invoice");
        assertEquals(result.getStatus(), 200);

        String responseXml = "<directory-response version=\"1.0\">\n" +
                "  <accepted-document-transfer>\n" +
                "     <DocumentID>urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0</DocumentID>\n" +
                "     <ProcessID>urn:www.cenbii.eu:profile:bii04:ver1.0</ProcessID>\n" +
                "  </accepted-document-transfer>\n" +
                "</directory-response>";
        assertEquals(result.getEntity().toString(), responseXml);

        verify(mockRingoSmpLookup);

    }

    private void prepareMetaData(List<PeppolDocumentTypeId> acceptedDocuments) {
        SmpLookupResult smpMetaDataResponse = new SmpLookupResult(acceptedDocuments);
        expect(mockRingoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf(participantId.stringValue()), LocalName.Invoice)).andReturn(smpMetaDataResponse);
        replay(mockRingoSmpLookup);
    }

}

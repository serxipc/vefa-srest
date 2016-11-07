package no.sr.ringo.peppol;

import no.sr.ringo.ObjectMother;
import no.sr.ringo.smp.RingoSmpLookup;
import no.sr.ringo.smp.SmpLookupResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: Adam
 * Date: 10/29/12
 * Time: 8:30 AM
 */
public class DocumentTypeIdResolverTest {

    private RingoSmpLookup mockRingoSmpLookup;
    PeppolParticipantId peppolParticipantId;

    @BeforeMethod
    public void setUp() throws Exception {
        peppolParticipantId = PeppolParticipantId.valueFor(ObjectMother.getTestParticipantIdForSMPLookup().stringValue());
        mockRingoSmpLookup = createStrictMock(RingoSmpLookup.class);
    }

    @Test
    public void testResolveProperDocumentTypeId() throws Exception {
        String documentTypeIdString = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0";
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);

        PeppolDocumentTypeId documentTypeId = resolver.resolve(peppolParticipantId, documentTypeIdString);
        assertNotNull(documentTypeId);
    }

    @Test
    public void testResolveDocumentTypeIdFromInvoiceAcronym() throws Exception {
        String documentTypeIdString = PeppolDocumentIdAcronym.INVOICE.name();
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);

        SmpLookupResult testSmpLookupResult = prepareTestResultForInvoice();
        expect(mockRingoSmpLookup.fetchSmpMetaData(peppolParticipantId, LocalName.Invoice)).andReturn(testSmpLookupResult);
        replay(mockRingoSmpLookup);

        PeppolDocumentTypeId documentTypeId = resolver.resolve(peppolParticipantId, documentTypeIdString);
        assertNotNull(documentTypeId);
        assertEquals(testSmpLookupResult.getAcceptedDocumentTypes().get(0), documentTypeId);

        verify(mockRingoSmpLookup);
    }

    @Test
    public void testResolveDocumentTypeIdFromCreditNoteAcronym() throws Exception {
        String documentTypeIdString = PeppolDocumentIdAcronym.CREDIT_NOTE.name();
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);

        SmpLookupResult testSmpLookupResult = prepareTestResultForCreditNote();
        expect(mockRingoSmpLookup.fetchSmpMetaData(peppolParticipantId, LocalName.CreditNote)).andReturn(testSmpLookupResult);
        replay(mockRingoSmpLookup);

        PeppolDocumentTypeId documentTypeId = resolver.resolve(peppolParticipantId, documentTypeIdString);
        assertNotNull(documentTypeId);
        assertEquals(testSmpLookupResult.getAcceptedDocumentTypes().get(0), documentTypeId);

        verify(mockRingoSmpLookup);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testResolveFromWrongAcronymName() throws Exception {

        String wrongAcronym = "wrongAcronym";
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);

        PeppolDocumentTypeId documentTypeId = resolver.resolve(peppolParticipantId, wrongAcronym);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testResolveFromWrongDocumentTypeIdString() throws Exception {

        String wrongDocumentTypeIdString = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##";
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);

        PeppolDocumentTypeId documentTypeId = resolver.resolve(peppolParticipantId, wrongDocumentTypeIdString);
    }

    @Test
    public void testResolveFromEhf2DocumentTypeIdString() throws Exception {

        String documentId = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixx:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0::2.1";
        PeppolParticipantId participantId = PeppolParticipantId.valueFor("9908:910667831");

        //RingoSmpLookup smpLookup = new RingoSmpLookupImpl(SmpLookupManagerFactory.getSmpLookupManager());
        DocumentTypeIdResolver resolver = new DocumentTypeIdResolver(mockRingoSmpLookup);
        PeppolDocumentTypeId documentTypeId = resolver.resolve(participantId, documentId);
        assertNotNull(documentTypeId);

        PeppolHeader ph = new PeppolHeader();
        ph.setPeppolDocumentTypeId(documentTypeId);

        assertEquals(documentTypeId.getLocalName().toString(), "CreditNote");

    }

    private SmpLookupResult prepareTestResultForInvoice() {
        List<PeppolDocumentTypeId> list = new ArrayList<PeppolDocumentTypeId>();
        list.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0"));
        return new SmpLookupResult(list);
    }

    private SmpLookupResult prepareTestResultForCreditNote() {
        List<PeppolDocumentTypeId> list = new ArrayList<PeppolDocumentTypeId>();
        list.add(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0"));
        return new SmpLookupResult(list);
    }

}

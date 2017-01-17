package no.sr.ringo.smp;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.smp.ParticipantNotRegisteredException;
import eu.peppol.smp.SmpLookupException;
import eu.peppol.smp.SmpLookupManager;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.peppol.LocalName;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

/**
 * User: Adam
 * Date: 10/10/12
 * Time: 8:46 AM
 */
public class RingoSmpLookupImplTest {

    // invoice
    private final String EHFinvoiceOnly = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0";
    private final String invoiceOnly = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0";
    private final String billingInvoice = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0";
    private final String procurementInvoice = "urn:oasis:names:specification:ubl:schema:xsd:Invoice- 2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis: peppol6a:ver1.0::2.0";

    // credit note
    private final String EHFCreditNote = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0";
    private final String billingCreditNote = "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0";

    // tendering
    private final String tendering = "urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1";
    private final String tenderingReceipt = "urn:oasis:names:specification:ubl:schema:xsd:TenderReceipt-2::TenderReceipt##urn:www.cenbii.eu:transaction:biitrdm045:ver3.0::2.1";
    private final String callForTendering = "urn:oasis:names:specification:ubl:schema:xsd:CallForTenders::CallForTenders##urn:www.cenbii.eu:transaction:biitrdm083:ver3.0::2.1";

    LookupClient mockLookupClient;

    ParticipantIdentifier registeredParticipantId;
    ParticipantIdentifier notRegisteredParticipantId;

    List<PeppolDocumentTypeId> list;

    @BeforeMethod
    public void setUp() throws Exception {
        setUpTestData();
        setUpMocks();
    }

    @Test
    public void testIsRegistered() throws Exception {

        ParticipantId registeredParticipantId = ObjectMother.getTestParticipantIdForSMPLookup();

        ParticipantIdentifier r = ParticipantIdentifier.of(registeredParticipantId.stringValue());

        boolean registered = isParticipantRegistered(r, list);
        assertTrue(registered);

        verify(mockLookupClient);

    }

    @Test
    public void testIsNotRegistered() throws Exception {

        expect(mockLookupClient.getDocumentIdentifiers(notRegisteredParticipantId)).andThrow(new ParticipantNotRegisteredException(notRegisteredParticipantId));
        replay(mockLookupClient);

        RingoSmpLookupImpl ringoSmpLookup = new RingoSmpLookupImpl(mockLookupClient);
        boolean registered = ringoSmpLookup.isRegistered(ParticipantId.valueOf(notRegisteredParticipantId.stringValue()));
        assertFalse(registered);

        verify(mockLookupClient);
    }


    @Test
    public void testFetchSmpMetaDataForInvoice() throws Exception, SmpLookupException {

        SmpLookupResult smpLookupResult = fetchSmpDataForLocalName(LocalName.Invoice);

        //out of 6 documentTypes 4 are invoice
        assertEquals(4, smpLookupResult.getAcceptedDocumentTypes().size());

        verify(mockLookupClient);
    }

    @Test
    public void testFetchSmpMetaDataForCreditNote() throws Exception, SmpLookupException {

        SmpLookupResult smpLookupResult = fetchSmpDataForLocalName(LocalName.CreditNote);

        //out of 6 documentTypes 2 are creditNote
        assertEquals(2, smpLookupResult.getAcceptedDocumentTypes().size());

        verify(mockLookupClient);
    }

    @Test
    public void testFetchSmpMetaDataForOrder() throws Exception, SmpLookupException {

        SmpLookupResult smpLookupResult = fetchSmpDataForLocalName(LocalName.Order);
        assertNotNull(smpLookupResult);

        //no accepted documentType for order
        assertEquals(0, smpLookupResult.getAcceptedDocumentTypes().size());

        verify(mockLookupClient);
    }

    @Test
    public void testFetchSmpMetaDataForTender() throws Exception, SmpLookupException {

        SmpLookupResult smpLookupResult = fetchSmpDataForLocalName(LocalName.Tender);

        assertNotNull(smpLookupResult);
        assertEquals(smpLookupResult.getAcceptedDocumentTypes().size(), 1);

        System.out.println(smpLookupResult.getAcceptedDocumentTypes().get(0));
        assertEquals(smpLookupResult.getAcceptedDocumentTypes().get(0).toString(), tendering);

        verify(mockLookupClient);

    }


    @Test
    public void testIsNotAcceptable() throws SmpLookupException, ParticipantNotRegisteredException {

        boolean acceptable = isAcceptable(registeredParticipantId, Collections.<PeppolDocumentTypeId>emptyList());

        assertFalse(acceptable);

        verify(mockLookupClient);
    }

    @Test
    public void testIsAcceptable() throws SmpLookupException, ParticipantNotRegisteredException {

        boolean acceptable = isAcceptable(registeredParticipantId, list);

        assertTrue(acceptable);

        verify(mockLookupClient);
    }

    private void setUpMocks() {
        mockLookupClient = createStrictMock(SmpLookupManager.class);
    }

    private void setUpTestData() {
        list = prepareAcceptedDocumentTypes();
        registeredParticipantId = ObjectMother.getTestParticipantIdForSMPLookup();
        registeredParticipantId = ParticipantId.valueOf(registeredParticipantId.stringValue());
        notRegisteredParticipantId = ObjectMother.getAdamsParticipantId();
    }

    private boolean isParticipantRegistered(ParticipantIdentifier participantId, List<PeppolDocumentTypeId> acceptedDocumentTypes) {

        expect(mockLookupClient.getServiceGroups(participantId)).andReturn(acceptedDocumentTypes);
        replay(mockLookupClient);

        RingoSmpLookupImpl ringoSmpLookup = new RingoSmpLookupImpl(mockLookupClient);
        return ringoSmpLookup.isRegistered(ParticipantId.valueOf(participantId.stringValue()));
    }

    private SmpLookupResult fetchSmpDataForLocalName(LocalName localName) throws SmpLookupException, ParticipantNotRegisteredException {
        expect(mockLookupClient.getServiceGroups(registeredParticipantId)).andReturn(list);
        replay(mockLookupClient);

        RingoSmpLookupImpl ringoSmpLookup = new RingoSmpLookupImpl(mockLookupClient);
        return ringoSmpLookup.fetchSmpMetaData(registeredParticipantId, localName);
    }

    private List<PeppolDocumentTypeId> prepareAcceptedDocumentTypes() {

        List<PeppolDocumentTypeId> list = new ArrayList<PeppolDocumentTypeId>();
        list.add(PeppolDocumentTypeId.valueOf(EHFinvoiceOnly));
        list.add(PeppolDocumentTypeId.valueOf(invoiceOnly));
        list.add(PeppolDocumentTypeId.valueOf(billingInvoice));
        list.add(PeppolDocumentTypeId.valueOf(procurementInvoice));

        list.add(PeppolDocumentTypeId.valueOf(EHFCreditNote));
        list.add(PeppolDocumentTypeId.valueOf(billingCreditNote));

        list.add(PeppolDocumentTypeId.valueOf(tendering));
        list.add(PeppolDocumentTypeId.valueOf(tenderingReceipt));
        list.add(PeppolDocumentTypeId.valueOf(callForTendering));

        return list;
    }

    private boolean isAcceptable(ParticipantId participantId, List<PeppolDocumentTypeId> acceptedDocumentTypes) throws SmpLookupException, ParticipantNotRegisteredException {
        expect(mockLookupClient.getServiceGroups(participantId)).andReturn(acceptedDocumentTypes);
        replay(mockLookupClient);

        RingoSmpLookupImpl ringoSmpLookup = new RingoSmpLookupImpl(mockLookupClient);
        return ringoSmpLookup.isAcceptable(registeredParticipantId, no.sr.ringo.peppol.PeppolDocumentTypeId.valueOf(invoiceOnly));
    }

}

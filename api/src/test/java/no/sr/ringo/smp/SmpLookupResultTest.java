package no.sr.ringo.smp;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.CustomizationIdentifier;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.RootNameSpace;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;

import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.*;
import static no.sr.ringo.peppol.TransactionIdentifier.Predefined.T010_INVOICE_V1;
import static no.sr.ringo.peppol.TransactionIdentifier.Predefined.T014_CREDIT_NOTE_V1;

public class SmpLookupResultTest {

    // TODO these should be updated - need EHF 2.0 variants and BIS 2.0 variants
    PeppolDocumentTypeId D1_EHF_INVOICE_ONLY = new PeppolDocumentTypeId(RootNameSpace.INVOICE, LocalName.Invoice, CustomizationIdentifier.valueOf(T010_INVOICE_V1 + ":#" + PEPPOL_4A_INVOICE_ONLY + "#" + EHF_INVOICE), "2.0");
    PeppolDocumentTypeId D2_EHF_CREDIT_NOTE_ONLY = new PeppolDocumentTypeId(RootNameSpace.CREDIT, LocalName.CreditNote, CustomizationIdentifier.valueOf(T014_CREDIT_NOTE_V1 + ":#" + PROPOSED_BII_XX + "#" + EHF_CREDIT_NOTE), "2.0");
    PeppolDocumentTypeId D3_EHF_CREDIT_NOTE_XY = new PeppolDocumentTypeId(RootNameSpace.CREDIT, LocalName.CreditNote, CustomizationIdentifier.valueOf(T014_CREDIT_NOTE_V1 + ":#" + PROPOSED_BII_XY + "#" + EHF_CREDIT_NOTE), "2.0");
    PeppolDocumentTypeId D4_PEPPOL_INVOICE = new PeppolDocumentTypeId(RootNameSpace.INVOICE, LocalName.Invoice, CustomizationIdentifier.valueOf(T010_INVOICE_V1 + ":#" + PEPPOL_4A_INVOICE_ONLY), "2.0");
    PeppolDocumentTypeId D5_PEPPOL_INVOICE_5A = new PeppolDocumentTypeId(RootNameSpace.INVOICE, LocalName.Invoice, CustomizationIdentifier.valueOf(T010_INVOICE_V1 + ":#" + PEPPOL_5A_BILLING), "2.0");
    PeppolDocumentTypeId D6_CREDIT_NOTE_5A = new PeppolDocumentTypeId(RootNameSpace.CREDIT, LocalName.CreditNote, CustomizationIdentifier.valueOf(T014_CREDIT_NOTE_V1+ ":#" + PEPPOL_5A_BILLING), "2.0");
    PeppolDocumentTypeId D7_PEPPOL_INVOICE_6A = new PeppolDocumentTypeId(RootNameSpace.INVOICE, LocalName.Invoice, CustomizationIdentifier.valueOf(T010_INVOICE_V1 + ":#" + PEPPOL_6A_PROCUREMENT), "2.0");


    @Test
    public void makeSureWeCanHandleTenderDocuments() throws Exception {
        SmpLookupResult smpLookupResult = new SmpLookupResult(null);
        PeppolDocumentTypeId did = PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Tender-2::Tender##urn:www.cenbii.eu:transaction:biitrdm090:ver3.0::2.1");
        ProfileId pid = smpLookupResult.profileIdFor(did);
        assertEquals(pid.stringValue(), "urn:www.cenbii.eu:profile:bii54:ver3.0");
    }

    @Test
    public void makeSureWeCanHandleTenderReceiptDocuments() throws Exception {
        SmpLookupResult smpLookupResult = new SmpLookupResult(null);
        PeppolDocumentTypeId did = PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:TenderReceipt-2::TenderReceipt##urn:www.cenbii.eu:transaction:biitrdm045:ver3.0::2.1");
        ProfileId pid = smpLookupResult.profileIdFor(did);
        assertEquals(pid.stringValue(), "urn:www.cenbii.eu:profile:bii54:ver3.0");
    }

    @Test
    public void makeSureWeCanHandleCallForTendersDocuments() throws Exception {
        SmpLookupResult smpLookupResult = new SmpLookupResult(null);
        PeppolDocumentTypeId did = PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:CallForTenders::CallForTenders##urn:www.cenbii.eu:transaction:biitrdm083:ver3.0::2.1");
        ProfileId pid = smpLookupResult.profileIdFor(did);
        assertEquals(pid.stringValue(), "urn:www.cenbii.eu:profile:bii47:ver3.0");
    }

    @Test
    public void testEhfInvoiceComesBeforeInvoiceOnly() throws Exception {
        List<PeppolDocumentTypeId> acceptedDocuments = new ArrayList<PeppolDocumentTypeId>();
        acceptedDocuments.add(D4_PEPPOL_INVOICE);
        acceptedDocuments.add(D1_EHF_INVOICE_ONLY);
        SmpLookupResult smpLookupResult = new SmpLookupResult(acceptedDocuments);

        List<PeppolDocumentTypeId> orderedAcceptedDocumentTypes = smpLookupResult.getAcceptedDocumentTypes();
        assertEquals(D1_EHF_INVOICE_ONLY, orderedAcceptedDocumentTypes.get(0));
        assertEquals(D4_PEPPOL_INVOICE, orderedAcceptedDocumentTypes.get(1));
    }

    @Test
    public void testProcurementComesLast() throws Exception {
        List<PeppolDocumentTypeId> acceptedDocuments = new ArrayList<PeppolDocumentTypeId>();
        acceptedDocuments.add(D5_PEPPOL_INVOICE_5A);
        acceptedDocuments.add(D3_EHF_CREDIT_NOTE_XY);
        SmpLookupResult smpLookupResult = new SmpLookupResult(acceptedDocuments);

        List<PeppolDocumentTypeId> orderedAcceptedDocumentTypes = smpLookupResult.getAcceptedDocumentTypes();
        assertEquals(D5_PEPPOL_INVOICE_5A, orderedAcceptedDocumentTypes.get(1));
    }


    /**
     * We expect documentTypes to be ordered by these rules:
     * - EHF specific first
     * - EHF genericr
     * - then 5a
     * - 6a as the last one
     */
    @Test
    public void testGetOrderedAcceptedDocumentTypes() throws Exception {
        List<PeppolDocumentTypeId> acceptedDocuments = prepareAllAcceptedDocumentTypes();

        SmpLookupResult smpLookupResult = new SmpLookupResult(acceptedDocuments);

        List<PeppolDocumentTypeId> orderedAcceptedDocumentTypes = smpLookupResult.getAcceptedDocumentTypes();
        assertEquals(D1_EHF_INVOICE_ONLY, orderedAcceptedDocumentTypes.get(0));
        assertEquals(D2_EHF_CREDIT_NOTE_ONLY, orderedAcceptedDocumentTypes.get(1));
        assertEquals(D3_EHF_CREDIT_NOTE_XY, orderedAcceptedDocumentTypes.get(2));
        assertEquals(D4_PEPPOL_INVOICE, orderedAcceptedDocumentTypes.get(3));
        assertEquals(D5_PEPPOL_INVOICE_5A, orderedAcceptedDocumentTypes.get(4));
        assertEquals(D6_CREDIT_NOTE_5A, orderedAcceptedDocumentTypes.get(5));
        assertEquals(D7_PEPPOL_INVOICE_6A, orderedAcceptedDocumentTypes.get(6));

    }

    @Test
    public void testOptimalDocumentTypeForLocalName() {
        List<PeppolDocumentTypeId> acceptedDocuments = prepareAllAcceptedDocumentTypes();

        SmpLookupResult smpLookupResult = new SmpLookupResult(acceptedDocuments);
        PeppolDocumentTypeId optimalDocType = smpLookupResult.optimalDocumentTypeFor(LocalName.valueOf("Invoice"));

        assertNotNull(optimalDocType, "No optimal PEPPOL document type found for Invoice");
        assertEquals(optimalDocType.getLocalName(), LocalName.Invoice);
        assertEquals(optimalDocType, D1_EHF_INVOICE_ONLY);
    }

    /** A CEN/BII Profile should be computed given a Document type. */
    @Test
    public void testComputeAssociatedProfileID() {
        List<PeppolDocumentTypeId> documentTypes = prepareAllAcceptedDocumentTypes();
        SmpLookupResult smpLookupResult = new SmpLookupResult(documentTypes);

        for (PeppolDocumentTypeId documentType : documentTypes) {
            ProfileId profileId = smpLookupResult.profileIdFor(documentType);
            assertNotNull(profileId);
            assertTrue(profileId.isInCenBiiNameSpace());
        }
    }

    private List<PeppolDocumentTypeId> prepareAllAcceptedDocumentTypes() {
        List<PeppolDocumentTypeId> acceptedDocuments = new ArrayList<PeppolDocumentTypeId>();
        acceptedDocuments.add(D2_EHF_CREDIT_NOTE_ONLY);
        acceptedDocuments.add(D4_PEPPOL_INVOICE);
        acceptedDocuments.add(D1_EHF_INVOICE_ONLY);
        acceptedDocuments.add(D5_PEPPOL_INVOICE_5A);
        acceptedDocuments.add(D6_CREDIT_NOTE_5A);
        acceptedDocuments.add(D7_PEPPOL_INVOICE_6A);
        acceptedDocuments.add(D3_EHF_CREDIT_NOTE_XY);
        return acceptedDocuments;
    }

}

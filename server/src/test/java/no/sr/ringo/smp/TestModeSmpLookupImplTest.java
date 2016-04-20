package no.sr.ringo.smp;

import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Making sure the test server implementation returns results as expected
 *
 * @author thore
 */
public class TestModeSmpLookupImplTest {

    TestModeSmpLookupImpl smpLookup = new TestModeSmpLookupImpl();
    PeppolParticipantId unit4agresso = PeppolParticipantId.valueFor("9908:961329310");

    private void verifySmpType(LocalName localName) {
        //System.out.println("Testing : " + localName.toString());
        SmpLookupResult result = smpLookup.fetchSmpMetaData(unit4agresso, localName);
        assertNotNull(result);
        assertNotNull(result.getAcceptedDocumentTypes());
        assertTrue(result.getAcceptedDocumentTypes().size() > 0);
        for (PeppolDocumentTypeId did : result.acceptedDocumentTypes) {
            assertEquals(did.getLocalName(), localName);
        }
    }

    @Test
    public void testFetchSmpMetaData() {
        verifySmpType(LocalName.Invoice);
        verifySmpType(LocalName.CreditNote);
        verifySmpType(LocalName.Order);
        verifySmpType(LocalName.Catalogue);
        verifySmpType(LocalName.OrderResponse);
        verifySmpType(LocalName.ApplicationResponse);
        verifySmpType(LocalName.Reminder);
    }

}
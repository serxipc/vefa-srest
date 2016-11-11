package no.sr.ringo.smp;

import com.google.inject.Inject;
import eu.peppol.smp.SmpLookupException;
import eu.peppol.smp.SmpLookupManager;
import eu.peppol.identifier.PeppolDocumentTypeId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.common.SmpLookupManagerFactory;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

import static org.testng.Assert.*;

/**
 * This test proves that SMP lookup using the Oxalis outbound library works
 *
 * <p/>
 * User: andy
 * Date: 1/25/12
 * Time: 10:47 AM
 */
@Guice(moduleFactory = TestModuleFactory.class)
public class SmpTest {

    static final Logger log = LoggerFactory.getLogger(SmpTest.class);

    final RingoSmpLookup ringoSmpLookup;

    @Inject
    public SmpTest(RingoSmpLookup ringoSmpLookup) {
        this.ringoSmpLookup = ringoSmpLookup;
    }

    @Test(groups = {"external"})
    public void testLookUp() throws Exception {
        SmpLookupManager manager = SmpLookupManagerFactory.getSmpLookupManager();
        final URL endpointAddress = manager.getEndpointAddress(ObjectMother.getTestParticipantIdForSMPLookup(), ObjectMother.getDocumentIdForBisInvoice());
        log.info(endpointAddress.toExternalForm());
        assertNotNull(endpointAddress);
    }


    @Test(groups = {"external"}, expectedExceptions = RuntimeException.class)
    public void testFailedLookUp() throws Exception {
        SmpLookupManager manager = SmpLookupManagerFactory.getSmpLookupManager();
        final URL endpointAddress = manager.getEndpointAddress(ObjectMother.getAdamsParticipantId(), ObjectMother.getDocumentIdForBisInvoice());
        log.info(endpointAddress.toExternalForm());
        assertNotNull(endpointAddress);
    }

    @Test(groups = {"external"})
    public void testLookUpUsingRingoSmpLookup() throws Exception {
        assertTrue(ringoSmpLookup.isRegistered(PeppolParticipantId.valueOf(ObjectMother.getTestParticipantIdForSMPLookup().stringValue())));
    }

    @Test(groups = {"external"})
    public void testFailedLookUpUsingRingoSmpLookup() throws Exception {
        assertFalse(ringoSmpLookup.isRegistered(PeppolParticipantId.valueOf(ObjectMother.getAdamsParticipantId().stringValue())));
    }

    @Test(groups = {"external"})
    public void testLookupManagerServiceGroups() throws Exception, SmpLookupException {
        List<PeppolDocumentTypeId> serviceGroups = SmpLookupManagerFactory.getSmpLookupManager().getServiceGroups(ObjectMother.getTestParticipantIdForSMPLookup());
        assertNotNull(serviceGroups);
        assertTrue(serviceGroups.size() > 0);
    }

    @Test(groups = {"external"})
    public void testRingoSmpLookupServiceGroups() throws Exception {
        SmpLookupResult lookupResult = ringoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf(ObjectMother.getTestParticipantIdForSMPLookup().stringValue()), LocalName.Invoice);
        assertNotNull(lookupResult);
        assertNotNull(lookupResult.getAcceptedDocumentTypes());
        assertFalse(lookupResult.getAcceptedDocumentTypes().isEmpty());
    }

    @Test(groups = {"external"})
    public void testFailedRingoSmpLookupServiceGroups() throws Exception {
        SmpLookupResult smpLookupResult = ringoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf(ObjectMother.getAdamsParticipantId().stringValue()), LocalName.Invoice);
        assertNotNull(smpLookupResult);
        assertTrue(smpLookupResult.getAcceptedDocumentTypes().isEmpty());
    }

    //@Test(groups = {"external"})
    public void testRingoSmpLookupProblematicNumber() throws Exception {
        SmpLookupResult lookupResult = ringoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf("9908:994496093"), LocalName.Invoice);
        assertNotNull(lookupResult);
        assertNotNull(lookupResult.getAcceptedDocumentTypes());
        assertFalse(lookupResult.getAcceptedDocumentTypes().isEmpty());
    }

    @Test(groups = {"external"})
    public void testRingoSmpLookupRegularDifiElma() throws Exception {
        SmpLookupResult lookupResult = ringoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf("9908:889640782"), LocalName.Invoice);
        assertNotNull(lookupResult);
        assertNotNull(lookupResult.getAcceptedDocumentTypes());
        assertFalse(lookupResult.getAcceptedDocumentTypes().isEmpty());
    }

    //@Test(groups = {"external"})
    public void testRingoSmpLookupTenderFormats() throws Exception {
        SmpLookupResult lookupResult = ringoSmpLookup.fetchSmpMetaData(PeppolParticipantId.valueOf("9908:990638942"), LocalName.Tender);
        assertNotNull(lookupResult);
        assertNotNull(lookupResult.getAcceptedDocumentTypes());
        assertFalse(lookupResult.getAcceptedDocumentTypes().isEmpty());     // expected to fail, since this receiver is only available in test environment
    }

}

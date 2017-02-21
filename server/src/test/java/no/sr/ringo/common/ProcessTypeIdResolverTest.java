package no.sr.ringo.common;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolProcessIdAcronym;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: Adam
 * Date: 10/29/12
 * Time: 10:55 AM
 */
public class ProcessTypeIdResolverTest {

    ProcessIdentifier expected = ProfileId.Predefined.BII04_INVOICE_ONLY.toVefa();

    @Test
    public void testProperFullProcessIdValue() {
        String processId = "urn:www.cenbii.eu:profile:bii04:ver1.0";
        ProfileIdResolver resolver = new ProfileIdResolver();

        ProcessIdentifier peppolProcessTypeId = resolver.resolve(processId);
        assertNotNull(peppolProcessTypeId);
        assertEquals(expected, peppolProcessTypeId);
    }

    @Test
    public void testProcessIdFromAcronym() {
        String processId = PeppolProcessIdAcronym.INVOICE_ONLY.name();
        ProfileIdResolver resolver = new ProfileIdResolver();

        ProcessIdentifier peppolProcessTypeId = resolver.resolve(processId);
        assertNotNull(peppolProcessTypeId);
        assertEquals(expected, peppolProcessTypeId);

    }

    @Test()
    public void testWrongValue() {

        String processId = "wrongValue";
        ProfileIdResolver resolver = new ProfileIdResolver();

        // Should work as a charm
        resolver.resolve(processId);
    }

}

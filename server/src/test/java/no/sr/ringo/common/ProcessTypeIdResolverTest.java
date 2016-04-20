package no.sr.ringo.common;

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

    ProfileId expected = ProfileId.Predefined.BII04_INVOICE_ONLY;

    @Test
    public void testProperFullProcessIdValue() {
        String processId = "urn:www.cenbii.eu:profile:bii04:ver1.0";
        ProfileIdResolver resolver = new ProfileIdResolver();

        ProfileId peppolProcessTypeId = resolver.resolve(processId);
        assertNotNull(peppolProcessTypeId);
        assertEquals(expected, peppolProcessTypeId);
    }

    @Test
    public void testProcessIdFromAcronym() {
        String processId = PeppolProcessIdAcronym.INVOICE_ONLY.name();
        ProfileIdResolver resolver = new ProfileIdResolver();

        ProfileId peppolProcessTypeId = resolver.resolve(processId);
        assertNotNull(peppolProcessTypeId);
        assertEquals(expected, peppolProcessTypeId);

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testWrongValue() {

        String processId = "wrongValue";
        ProfileIdResolver resolver = new ProfileIdResolver();

        resolver.resolve(processId);
    }

}

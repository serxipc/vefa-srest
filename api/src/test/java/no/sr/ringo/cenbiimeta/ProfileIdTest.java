package no.sr.ringo.cenbiimeta;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: steinar
 * Date: 06.11.12
 * Time: 14:33
 */
public class ProfileIdTest {
    @Test
    public void testToString() throws Exception {

        ProfileId profileId1 = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProfileId profileId2 = new ProfileId(profileId1.toString());
        assertEquals(profileId1, profileId2);
    }

    @Test
    public void testValueOf() throws Exception {
        ProfileId profileId = ProfileId.valueOf("urn:www.cenbii.eu:profile:bii04:ver1.0");
        assertNotNull(profileId);
    }


    @Test
    public void testEquals() throws Exception {
        ProfileId profileId1 = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProfileId profileId2 = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProfileId profileId3 = new ProfileId("urn:www.cenbii.eu:profile:bii05:ver1.0");

        assertNotEquals(profileId1, profileId3);
        assertEquals(profileId1,profileId2);
    }


    @Test
    public void testHashCode() throws Exception {
        ProfileId profileId1 = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0");
        ProfileId profileId2 = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0");

        assertEquals(profileId1.hashCode(), profileId2.hashCode());
    }


    @Test
    public void testPredefined() {
        ProfileId p = ProfileId.Predefined.BII04_INVOICE_ONLY;
        assertNotNull(p);
    }

    @Test
    public void testIsInCenBiiNameSpace() {
        assertTrue(ProfileId.Predefined.BII04_INVOICE_ONLY.isInCenBiiNameSpace());
        assertFalse(ProfileId.Predefined.EHF_INVOICE.isInCenBiiNameSpace());
    }
}

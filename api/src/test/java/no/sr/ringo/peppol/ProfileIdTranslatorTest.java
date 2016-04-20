package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: steinar
 * Date: 07.11.12
 * Time: 23:07
 */
public class ProfileIdTranslatorTest {

    @Test
    public void testTranslateToCenBiiProfile() throws Exception {
        ProfileIdTranslator translator = new ProfileIdTranslator();
        ProfileId from = ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY;
        assertFalse(from.isInCenBiiNameSpace());

        ProfileId to = translator.translateToCenBiiProfile(from);
        assertTrue(to.isInCenBiiNameSpace());
        assertEquals(ProfileId.Predefined.BII04_INVOICE_ONLY, to);
    }

    @Test
    public void testTranslateProfileWithUnknownCenBiiAssociation() {
        ProfileIdTranslator translator = new ProfileIdTranslator();
        ProfileId from = ProfileId.Predefined.EHF_INVOICE;
        ProfileId to = translator.translateToCenBiiProfile(from);
        assertTrue(to instanceof ProfileId.UnknownCenBIIProfileId);
    }
}

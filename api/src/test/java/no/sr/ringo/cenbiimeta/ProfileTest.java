package no.sr.ringo.cenbiimeta;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * User: steinar
 * Date: 06.11.12
 * Time: 14:25
 */
public class ProfileTest {

    public static final String URN = "urn:www.cenbii.eu:profile:bii04:ver1.0";
    public static final ProfileId PROFILE_ID = new ProfileId(URN);
    public static final String BII_04 = "BII04";
    public static final String INVOICE_ONLY = "Invoice only";
    private Profile bii04;

    @BeforeTest
    public void setUp() throws URISyntaxException {
        bii04 = new Profile(BII_04, INVOICE_ONLY, PROFILE_ID);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals(bii04.getName(),BII_04);
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(bii04.getDescription(),INVOICE_ONLY);
    }

    @Test
    public void testGetProfileId() throws Exception {
        assertEquals(bii04.getProfileId().toString(), URN);
    }

    @Test
    public void testEqualsAndHashCode() {
        Profile p = new Profile("BII04","Invoice only", PROFILE_ID);
        assertEquals(bii04, p);

        assertEquals(p.hashCode(), bii04.hashCode());
    }

}

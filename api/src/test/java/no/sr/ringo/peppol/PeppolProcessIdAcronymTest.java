package no.sr.ringo.peppol;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 7/12/12
 * Time: 10:34 AM
 */
public class PeppolProcessIdAcronymTest {
    @Test
    public void testValueFor() throws Exception {
        assertEquals(PeppolProcessIdAcronym.UNKNOWN, PeppolProcessIdAcronym.valueFor("rubbish"));
        assertEquals(PeppolProcessIdAcronym.PROCUREMENT, PeppolProcessIdAcronym.valueFor("urn:www.cenbii.eu:profile:bii06:ver1.0"));
        assertEquals(PeppolProcessIdAcronym.INVOICE_ONLY, PeppolProcessIdAcronym.valueFor("urn:www.cenbii.eu:profile:bii04:ver1.0"));
        assertEquals(PeppolProcessIdAcronym.ORDER_ONLY, PeppolProcessIdAcronym.valueFor("urn:www.cenbii.eu:profile:bii03:ver1.0"));
    }

    @Test
    public void testSafeValueOf() throws Exception {
        assertEquals(PeppolProcessIdAcronym.UNKNOWN, PeppolProcessIdAcronym.safeValueOf("rubbish"));
        assertEquals(PeppolProcessIdAcronym.PROCUREMENT, PeppolProcessIdAcronym.safeValueOf("PROCUREMENT"));
        assertEquals(PeppolProcessIdAcronym.INVOICE_ONLY, PeppolProcessIdAcronym.safeValueOf("INVOICE_ONLY"));
        assertEquals(PeppolProcessIdAcronym.ORDER_ONLY, PeppolProcessIdAcronym.safeValueOf("ORDER_ONLY"));
    }
}

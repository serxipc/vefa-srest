package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 10/26/12
 * Time: 1:52 PM
 */
public class PeppolHeaderTest {


    @Test
    public void testPeppolProcessIdCreatedWithinPeppolProcessItTypeIdCreation() throws Exception {
        PeppolHeader peppolHeader = new PeppolHeader();
        peppolHeader.setProfileId(ProfileId.Predefined.BII04_INVOICE_ONLY);
        assertEquals(peppolHeader.getPeppolProcessIdAcronym(), PeppolProcessIdAcronym.INVOICE_ONLY);
    }

    @Test
    public void testPeppolDocumentIdCreatedWithinPeppolDocumentItTypeIdCreation() throws Exception {
        PeppolHeader peppolHeader = new PeppolHeader();
        peppolHeader.setPeppolDocumentTypeId(PeppolDocumentTypeId.valueOf("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0"));
        assertEquals(peppolHeader.getPeppolDocumentIdAcronym(), PeppolDocumentIdAcronym.INVOICE);
    }
}

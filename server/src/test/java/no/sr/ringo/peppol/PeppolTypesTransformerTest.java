package no.sr.ringo.peppol;

import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.sr.ringo.cenbiimeta.ProfileId;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andy
 * Date: 10/26/12
 * Time: 2:50 PM
 */
public class PeppolTypesTransformerTest {


    @Test
    public void testConvertDocumentTypeId() throws Exception {

        eu.peppol.identifier.PeppolDocumentTypeId expected = PeppolDocumentTypeIdAcronym.EHF_INVOICE.getDocumentTypeIdentifier();

        assertEquals(PeppolTypesTransformer.convert(PeppolDocumentTypeId.EHF_INVOICE),expected);

    }

    @Test
    public void testConvertProcessTypeId() throws Exception {

        eu.peppol.identifier.PeppolProcessTypeId expected = PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId();
        ProfileId peppolProcessTypeId = ProfileId.Predefined.BII04_INVOICE_ONLY;

        assertEquals(PeppolTypesTransformer.convert(peppolProcessTypeId), expected);

    }
}

package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;

/**
 * User: andy
 * Date: 10/26/12
 * Time: 2:50 PM
 */
public class PeppolTypesTransformer {

    public static eu.peppol.identifier.PeppolDocumentTypeId convert(PeppolDocumentTypeId peppolDocumentTypeId) {
        return eu.peppol.identifier.PeppolDocumentTypeId.valueOf(peppolDocumentTypeId.stringValue());
    }

    public static eu.peppol.identifier.PeppolProcessTypeId convert(ProfileId profileId) {
        return eu.peppol.identifier.PeppolProcessTypeId.valueOf(profileId.toString());
    }

}

package no.sr.ringo.common;

import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.peppol.PeppolProcessIdAcronym;

/**
 * Resolve process id passed as string into proper VO from acronym of full value.
 * User: Adam
 * Date: 10/29/12
 * Time: 8:24 AM
 */
public class ProfileIdResolver {

    /**
     * Checks if peppolIdString as an acronym, if so creates ProfileId from it, try full name otherwise
     * Returns PeppolDocumentTypeId
     */
    public ProfileId resolve(String processIdString) {

        PeppolProcessIdAcronym acronym = PeppolProcessIdAcronym.safeValueOf(processIdString);

        if (validAcronymn(acronym)) {
            return new ProfileId(acronym.stringValue());
        } else {
            //this throws an exception if full string is wrong
            return new ProfileId(processIdString);
        }
    }

    private boolean validAcronymn(PeppolProcessIdAcronym acronym) {
        return !PeppolProcessIdAcronym.UNKNOWN.equals(acronym);
    }
}

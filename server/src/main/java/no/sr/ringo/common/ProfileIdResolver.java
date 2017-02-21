package no.sr.ringo.common;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
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
    public ProcessIdentifier resolve(String processIdString) {

        PeppolProcessIdAcronym acronym = PeppolProcessIdAcronym.safeValueOf(processIdString);

        if (validAcronymn(acronym)) {
            return ProcessIdentifier.of(acronym.stringValue());
        } else {
            // Just use whatever value given
            return ProcessIdentifier.of(processIdString);
        }
    }

    private boolean validAcronymn(PeppolProcessIdAcronym acronym) {
        return !PeppolProcessIdAcronym.UNKNOWN.equals(acronym);
    }
}

package no.sr.ringo.peppol;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;

/**
 * @author Steinar Overbeck Cook
 *
 *         Created by
 *         User: steinar
 *         Date: 04.12.11
 *         Time: 19:18
 */
public enum PeppolProcessIdAcronym {


    ORDER_ONLY("urn:www.cenbii.eu:profile:bii03:ver1.0"),
    INVOICE_ONLY("urn:www.cenbii.eu:profile:bii04:ver1.0"),
    PROCUREMENT("urn:www.cenbii.eu:profile:bii06:ver1.0"),
    UNKNOWN("urn:");


    private static final String scheme = "cenbiimeta-procid-ubl";

    private String profileId = null;


    private PeppolProcessIdAcronym(String profileId) {
        this.profileId = profileId;
    }

    public static String getScheme() {
        return scheme;
    }

    public String getProfileId() {
        return profileId;
    }


    /** Creates the corresponding ProcessId based upon the supplied Peppol Process identifier */
    public static PeppolProcessIdAcronym valueFor(String identifier) {
        for (PeppolProcessIdAcronym peppolProcessId : values()) {
            if (peppolProcessId.profileId.equals(identifier)) {
                return peppolProcessId;
            }
        }
        return UNKNOWN;
    }

    public  ProcessIdentifier toVefa() {
        return ProcessIdentifier.of(stringValue());
    }

    /** Creates the corresponding ProcessId based upon the supplied local name without throwing exception
     *
     * @return the PeppolProcessId or Unknown if not found.
     **/
    public static PeppolProcessIdAcronym safeValueOf(String localName) {
        for (PeppolProcessIdAcronym peppolProcessId : values()) {
            if (peppolProcessId.name().equals(localName)) {
                return peppolProcessId;
            }
        }
        return UNKNOWN;
    }

    public String stringValue() {
        return profileId;
    }

    @Override
    public String toString() {
        return stringValue();
    }
}

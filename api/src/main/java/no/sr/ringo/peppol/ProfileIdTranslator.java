package no.sr.ringo.peppol;

import no.sr.ringo.cenbiimeta.ProfileId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static no.sr.ringo.cenbiimeta.ProfileId.Predefined.*;

/**
 * Updated with latest MLR from various sources, mostly based on the
 * DIFI document called "EHF profiler og dokumenttyper v1.1"
 *
 * @author steinar
 * @author thore
 */
public class ProfileIdTranslator {

    static final Logger log = LoggerFactory.getLogger(ProfileIdTranslator.class);

    Map<ProfileId, ProfileId> profileIdMap = new HashMap<ProfileId, ProfileId>();

    public ProfileIdTranslator() {

        // these are for EHF 1.0 and old UBL 2.0 variants
        profileIdMap.put(PEPPOL_1A_CATALOGUE, BII01_CATALOGUE);
        profileIdMap.put(PEPPOL_3A_ORDER_ONLY, BII03_ORDER_ONLY);
        profileIdMap.put(PEPPOL_4A_INVOICE_ONLY, BII04_INVOICE_ONLY);
        profileIdMap.put(PEPPOL_5A_BILLING, BII05_BILLING);
        profileIdMap.put(PEPPOL_6A_PROCUREMENT, BII06_PROCUREMENT);
        profileIdMap.put(PROPOSED_BII_XX, PROPOSED_BII_XX); // CENBII and PEPPOL are the same
        profileIdMap.put(PROPOSED_BII_XY, PROPOSED_BII_XY); // CENBII and PEPPOL are the same

        // these are for EHF 2.0 and new UBL 2.1 variants
        profileIdMap.put(PEPPOL_V2_1A_CATALOGUE, BII01_V2_CATALOGUE);
        profileIdMap.put(PEPPOL_V2_3A_ORDER_ONLY, BII03_V2_ORDER_ONLY);
        profileIdMap.put(PEPPOL_V2_4A_INVOICE_ONLY, BII04_V2_INVOICE_ONLY);
        profileIdMap.put(PEPPOL_V2_5A_BILLING, BII05_V2_BILLING);
        profileIdMap.put(PEPPOL_V2_6A_PROCUREMENT, BII06_V2_PROCUREMENT);
        profileIdMap.put(BIIXX_V2_CREDITNOTE_ONLY, BIIXX_V2_CREDITNOTE_ONLY); // CENBII and PEPPOL are the same
        profileIdMap.put(BIIXY_V2_INVOICE_CREDITNOTE_REMINDER, BIIXY_V2_INVOICE_CREDITNOTE_REMINDER); // CENBII and PEPPOL are the same

        // tendering
        profileIdMap.put(ProfileId.valueOf("urn:www.cenbii.eu:transaction:biitrdm090:ver3.0"), ProfileId.valueOf("urn:www.cenbii.eu:profile:bii54:ver3.0"));
        profileIdMap.put(ProfileId.valueOf("urn:www.cenbii.eu:transaction:biitrdm045:ver3.0"), ProfileId.valueOf("urn:www.cenbii.eu:profile:bii54:ver3.0"));
        profileIdMap.put(ProfileId.valueOf("urn:www.cenbii.eu:transaction:biitrdm083:ver3.0"), ProfileId.valueOf("urn:www.cenbii.eu:profile:bii47:ver3.0"));

        // other variants
        profileIdMap.put(PEPPOL_28A_ORDERING, BII28_V2_ORDERING);
        profileIdMap.put(PEPPOL_30A_DESPATCH_ADVICE, BII30_V2_DESPATCH_ADVICE);
        profileIdMap.put(PEPPOL_36A_MESSAGE_LEVEL_RESPONSE, BII36_V2_MESSAGE_LEVEL_RESPONSE);

    }

    public ProfileId translateToCenBiiProfile(ProfileId profileIdToBeTranslated) {
        if (profileIdToBeTranslated.isInCenBiiNameSpace()) {
            return profileIdToBeTranslated;
        }
        ProfileId cenBiiProfileId = profileIdMap.get(profileIdToBeTranslated);
        if (cenBiiProfileId == null) {
            log.error(String.format("Unable to translate profileId into corresponding CEN/BII profile using %s", profileIdToBeTranslated));
            return new ProfileId.UnknownCenBIIProfileId(profileIdToBeTranslated.toString());
        } else
            return cenBiiProfileId;
    }

}

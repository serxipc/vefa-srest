package no.sr.ringo.cenbiimeta;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;

/**
 * Represents the unique identification of a Profile according to the CEN/BII meta model.
 *
 * One may of course argue that this class should have an extension named for instance PeppolProfileId, which
 * in turn should have an extension named EhfProfileID. However; that is a complication which presently is not called for.
 *
 * User: steinar
 * Date: 06.11.12
 * Time: 14:29
 */
public class ProfileId {

    protected final String profileId;

    public ProfileId(String profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId must contain a value");
        }
        if (!profileId.startsWith("urn:")) {
            throw new IllegalArgumentException("Illegal profile string: " + profileId + ", value must start with urn:");
        }
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return profileId;
    }

    public String stringValue() {
        return profileId;
    }

    public static ProfileId valueOf(String profileId) {
        return new ProfileId(profileId);
    }

    public ProcessIdentifier toVefa() {
        return ProcessIdentifier.of(stringValue());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileId profileId1 = (ProfileId) o;
        if (profileId != null ? !profileId.equals(profileId1.profileId) : profileId1.profileId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return profileId != null ? profileId.hashCode() : 0;
    }

    public boolean isInCenBiiNameSpace() {
        return profileId.startsWith("urn:www.cenbii.eu:profile") ? true : false;
    }

    @Deprecated
    public static class Predefined {

        // CEN BII ProfileID (BIS v3)
        public static final ProfileId BII01_CATALOGUE       = new ProfileId("urn:www.cenbii.eu:profile:bii01:ver1.0"); // BIS01 Catalogue
        public static final ProfileId BII03_ORDER_ONLY      = new ProfileId("urn:www.cenbii.eu:profile:bii03:ver1.0"); // BIS03 Order only
        public static final ProfileId BII04_INVOICE_ONLY    = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver1.0"); // BIS04 Invoice only
        public static final ProfileId BII05_BILLING         = new ProfileId("urn:www.cenbii.eu:profile:bii05:ver1.0"); // BIS05 Billing
        public static final ProfileId BII06_PROCUREMENT     = new ProfileId("urn:www.cenbii.eu:profile:bii06:ver1.0"); // BIS06 Procurement
        public static final ProfileId PROPOSED_BII_XX       = new ProfileId("urn:www.cenbii.eu:profile:biixx:ver1.0"); // DIFI EHF extensions
        public static final ProfileId PROPOSED_BII_XY       = new ProfileId("urn:www.cenbii.eu:profile:biixy:ver1.0"); // DIFI EHF extensions

        // PEPPOL BIS v1.0
        public static final ProfileId PEPPOL_1A_CATALOGUE               = new ProfileId("urn:www.peppol.eu:bis:peppol1a:ver1.0");
        public static final ProfileId PEPPOL_3A_ORDER_ONLY              = new ProfileId("urn:www.peppol.eu:bis:peppol3a:ver1.0");
        public static final ProfileId PEPPOL_4A_INVOICE_ONLY            = new ProfileId("urn:www.peppol.eu:bis:peppol4a:ver1.0");
        public static final ProfileId PEPPOL_5A_BILLING                 = new ProfileId("urn:www.peppol.eu:bis:peppol5a:ver1.0");
        public static final ProfileId PEPPOL_6A_PROCUREMENT             = new ProfileId("urn:www.peppol.eu:bis:peppol6a:ver1.0");
        // PROPOSED_BII_XX
        // PROPOSED_BII_XY
        public static final ProfileId PEPPOL_28A_ORDERING               = new ProfileId("urn:www.peppol.eu:bis:peppol28a:ver1.0");
        public static final ProfileId PEPPOL_30A_DESPATCH_ADVICE        = new ProfileId("urn:www.peppol.eu:bis:peppol30a:ver1.0");
        public static final ProfileId PEPPOL_36A_MESSAGE_LEVEL_RESPONSE = new ProfileId("urn:www.peppol.eu:bis:peppol36a:ver1.0");

        // CEN BII ProfileID (BIS v4)
        public static final ProfileId BII01_V2_CATALOGUE                   = new ProfileId("urn:www.cenbii.eu:profile:bii01:ver2.0"); // EHF_CATALOGUE / EHF_CATALOGUE_RESPONSE
        public static final ProfileId BII03_V2_ORDER_ONLY                  = new ProfileId("urn:www.cenbii.eu:profile:bii03:ver2.0"); // BIS03 Order only
        public static final ProfileId BII04_V2_INVOICE_ONLY                = new ProfileId("urn:www.cenbii.eu:profile:bii04:ver2.0"); // EHF Kun faktura
        public static final ProfileId BII05_V2_BILLING                     = new ProfileId("urn:www.cenbii.eu:profile:bii05:ver2.0"); // EHF Faktura og kreditnota
        public static final ProfileId BII06_V2_PROCUREMENT                 = new ProfileId("urn:www.cenbii.eu:profile:bii06:ver2.0"); // BIS06 Procurement
        public static final ProfileId BIIXX_V2_CREDITNOTE_ONLY             = new ProfileId("urn:www.cenbii.eu:profile:biixx:ver2.0"); // EHF Kun kreditnota
        public static final ProfileId BIIXY_V2_INVOICE_CREDITNOTE_REMINDER = new ProfileId("urn:www.cenbii.eu:profile:biixy:ver2.0"); // EHF Faktura, kreditnota og purring
        public static final ProfileId BII28_V2_ORDERING                    = new ProfileId("urn:www.cenbii.eu:profile:bii28:ver2.0"); // BIS28 Ordering
        public static final ProfileId BII30_V2_DESPATCH_ADVICE             = new ProfileId("urn:www.cenbii.eu:profile:bii30:ver2.0"); // BIS30 Despatch advice only
        public static final ProfileId BII36_V2_MESSAGE_LEVEL_RESPONSE      = new ProfileId("urn:www.cenbii.eu:profile:bii36:ver2.0"); // BIS36 Message Level Response

        // PEPPOL BIS v2
        public static final ProfileId PEPPOL_V2_1A_CATALOGUE               = new ProfileId("urn:www.peppol.eu:bis:peppol1a:ver2.0");
        public static final ProfileId PEPPOL_V2_3A_ORDER_ONLY              = new ProfileId("urn:www.peppol.eu:bis:peppol3a:ver2.0");
        public static final ProfileId PEPPOL_V2_4A_INVOICE_ONLY            = new ProfileId("urn:www.peppol.eu:bis:peppol4a:ver2.0");
        public static final ProfileId PEPPOL_V2_5A_BILLING                 = new ProfileId("urn:www.peppol.eu:bis:peppol5a:ver2.0");
        public static final ProfileId PEPPOL_V2_6A_PROCUREMENT             = new ProfileId("urn:www.peppol.eu:bis:peppol6a:ver2.0");
        // BIIXX_V2_CREDITNOTE_ONLY
        // BIIXY_V2_INVOICE_CREDITNOTE_REMINDER

        // These are not real profiles but extensions used as constants the customization part
        public static final ProfileId EHF_INVOICE = new ProfileId("urn:www.difi.no:ehf:faktura:ver1");
        public static final ProfileId EHF_CREDIT_NOTE = new ProfileId("urn:www.difi.no:ehf:kreditnota:ver1");

    }

    public static final class UnknownCenBIIProfileId extends ProfileId {

        public UnknownCenBIIProfileId(String profileId) {
            super(profileId);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            UnknownCenBIIProfileId that = (UnknownCenBIIProfileId) o;
            if (super.profileId != null ? !super.profileId.equals(that.profileId) : that.profileId != null) return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (super.profileId != null ? super.profileId.hashCode() : 0);
            return result;
        }

    }

}

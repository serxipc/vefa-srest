package no.sr.ringo.peppol;

import org.slf4j.Logger;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Value object for a Peppol Participant id.
 * Date: 1/31/12
 * Time: 2:32 PM
 *
 * @deprecated use the ParticipantId from oxalis-api or vefa-peppol
 */
public class PeppolParticipantId implements Serializable {

    private static final long serialVersionUID = 20122L;

    static final Logger log = org.slf4j.LoggerFactory.getLogger(PeppolParticipantId.class);

    // The weight array obtained from Br\u00F8nn\u00F8ysund, used to validate a norwegian org no
    public static final Integer[] ORG_NO_WEIGHT = new Integer[]{3, 2, 7, 6, 5, 4, 3, 2};

    //max length for international organisation number
    static final int INTERNATION_ORG_ID_MAX_LENGTH = 35;

    static final int MODULUS_11 = 11;

    static final Pattern ISO6523_PATTERN = Pattern.compile("^(\\d{4}):([^\\s]+)$");
    static final Pattern NO_ORG_NUM_PATTERN = Pattern.compile("^(?:NO)?\\s*(\\d{9})\\s*(?:MVA)?$");

    // The full ISO6523 organisation id including the scheme ICD
    final String peppolParticipantId;

    // The scheme id
    final SchemeId schemeId;

    // Just the organisation id
    final String organisationId;

    /**
     * Creates a PeppolParticipantId from the provided party id and organisationId
     * @param schemeId
     * @param organisationId
     */
    public PeppolParticipantId(SchemeId schemeId, String organisationId) {
        if(schemeId == null) {
            throw new IllegalArgumentException("SchemeId must be specified with a a valid ISO6523 code.");
        }

        if (organisationId == null) {
            throw new IllegalArgumentException("The organisation id must be specified.");
        }

        if (organisationId.length() > INTERNATION_ORG_ID_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Invalid organisation id. '%s' is longer than %d characters", organisationId, INTERNATION_ORG_ID_MAX_LENGTH));
        }

        //remove any white spaces etc..
        String orgNo = schemeId.formatOrganisationId(organisationId);
        if (schemeId == SchemeId.NO_ORGNR || schemeId == SchemeId.NO_VAT) {

            if (!isValidNorwegianOrgNum(orgNo)) {
                throw new IllegalArgumentException(String.format("Organisation number '%s' is not a valid norwegian organisation number",organisationId));
            }
        }

        this.schemeId = schemeId;
        this.organisationId = orgNo;
        this.peppolParticipantId = String.format("%s:%s", schemeId.numericISO6523Code, orgNo);
    }

    /**
     * Parses participant ids of the form xxxx:yyyyyyyyyy
     * and norwegian organisation numbers of the form NO123456789MVA, where both NO and MVA are optional.
     *
     * The NO:ORGNR scheme will be used by default for all norwegian schemes
     *
     * @param text
     * @return null if not able to parse, A PeppolParticipantId with iso code set if parsing is successful.
     */
    public static PeppolParticipantId valueOf(String text) {
        String id = text == null ? null : text.trim().replaceAll("\\s","");
        if (id == null || id.length() == 0) {
            return null;
        }
        
        try {
    
            //Case 1 of the form 1234:123456789
            Matcher matcher = ISO6523_PATTERN.matcher(id);
            if (matcher.matches()) {
                final SchemeId schemeId = SchemeId.fromISO6523(matcher.group(1));
                return schemeId == null ?  null : new PeppolParticipantId(schemeId,matcher.group(2));
            }
    
            //If we are parsing then we always choose with MVA as default (i.e. NO_VAT party id)
            //Case 2 of the form NO123456789MVA or 123456789MVA
            //Case 3 of the form NO123456789 or 123456789
            matcher = NO_ORG_NUM_PATTERN.matcher(id);
            if (matcher.matches()) {
                return new PeppolParticipantId(SchemeId.NO_ORGNR,matcher.group(1));
            }

        }
        catch (IllegalArgumentException e) {
            log.warn("Unable to parse peppol participant id " + text);
        }
        return null;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    /**
     * Provides the PEPPOL participant ID on the form xxxx:yyyyy, in which the
     * xxxx represents the ISO623 ICD prefix and the stuff after the ':' represents
     * the part of the organisation number to be used for PEPPOL addressing.
     *
     * @return
     */
    public String stringValue() {
        return peppolParticipantId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PeppolParticipantId");
        sb.append("{id='").append(peppolParticipantId).append('\'');
        sb.append(", partyId=").append(schemeId);
        sb.append('}');
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeppolParticipantId that = (PeppolParticipantId) o;

        if (peppolParticipantId != null ? !peppolParticipantId.equals(that.peppolParticipantId) : that.peppolParticipantId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return peppolParticipantId != null ? peppolParticipantId.hashCode() : 0;
    }

    public static boolean isValidNorwegianOrgNum(String orgNo) {
        if (orgNo == null || orgNo.length() < 9) {
            return false;
        }

        /** converting the accountNo into a char array */
        final char[] number = orgNo.toCharArray();

        // the last number is a control number, so save that
        final int controlNumber = number[number.length - 1] - '0';
        final int modulus = generateOrgNumModulus11(orgNo.substring(0, 8));


        /** don't subtract from length if modulus is 0 */
        if ((modulus == 0) && (controlNumber == 0)) {
            return true;
        }

        // subtracting modelus from 11 and compares it with the controlNumber (the last number in the orgNo)
        if ((MODULUS_11 - modulus) == controlNumber) {
            return true;
        } else {
            return false;
        }
    }

    private static int generateOrgNumModulus11(String first8Digits) {
        /** holds the value of number[i] * weigth[i] */
        int sum = 0;

        final char[] number = first8Digits.toCharArray();
        /** Iterating the basis and multiplies with the weight array, not the last one */
        for (int i = 0; i < (number.length); i++) {
            sum += ((number[i] - '0') * ORG_NO_WEIGHT[i]);
        }

        /** finding the modulus of the sum */
        return sum % MODULUS_11;
    }

}

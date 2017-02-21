package no.sr.ringo.peppol;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;

/**
 * @author steinar
 *         Date: 21.02.2017
 *         Time: 18.27
 */
public class Iso6523Util {

    public static ParticipantIdentifier participantIdentifierWithSchemeName(String schemeName, String orgNo) {
        final SchemeId parsed = SchemeId.parse(schemeName);

        String s = parsed.getIso6523Icd() + ":" + orgNo;
        return ParticipantIdentifier.of(s);
    }
}

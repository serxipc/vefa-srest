/* Created by steinar on 03.01.12 at 14:10 */
package no.sr.ringo.message;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class PeppolMessageNotFoundException extends IllegalArgumentException {
    public PeppolMessageNotFoundException(Integer messageNo) {
        super("PEPPOL message number " + messageNo + " not found");
    }
}

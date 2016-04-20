/* Created by steinar on 01.01.12 at 18:01 */
package no.sr.ringo.message;

import no.sr.ringo.peppol.PeppolHeader;
import org.w3c.dom.Document;

/**
 * Represents a xmlMessage to be transferred between two parties in the PEPPOL network.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 *
 */
public class PeppolMessage {

    private Integer msgNo;
    private PeppolHeader peppolHeader;
    private Document xmlMessage;

    public PeppolMessage() {
        peppolHeader = new PeppolHeader();
    }

    public PeppolHeader getPeppolHeader() {
        return peppolHeader;
    }

    /** Message number assigned by this Access Point */
    public Integer getMsgNo() {
        return msgNo;
    }

    public Document getXmlMessage() {
        return xmlMessage;
    }

    public void setXmlMessage(Document xmlMessage) {
        this.xmlMessage = xmlMessage;
    }
}

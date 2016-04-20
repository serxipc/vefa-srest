package no.sr.ringo.document;

import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import org.apache.http.entity.mime.content.ContentBody;

/**
 * User: andy
 * Date: 10/29/12
 * Time: 12:31 PM
 */
public abstract class ClientPeppolDocument {

    public PeppolHeader populate(PeppolHeader header) {
        if (header.getPeppolChannelId() == null) {
            header.setPeppolChannelId(new PeppolChannelId("SendRegning"));
        }
        return header;
    }

    public abstract ContentBody getContentBody();

}

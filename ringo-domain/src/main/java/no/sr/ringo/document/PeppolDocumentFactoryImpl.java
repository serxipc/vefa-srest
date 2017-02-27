package no.sr.ringo.document;

import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;

/**
 * User: andy
 * Date: 10/3/12
 * Time: 10:32 AM
 */
public class PeppolDocumentFactoryImpl implements PeppolDocumentFactory {

    public PeppolDocument makePeppolDocument(PeppolDocumentTypeId peppolDocumentTypeId, String contentsAsXml) {

        // This needs to be removed
        if (peppolDocumentTypeId.getLocalName().equals(LocalName.Invoice)) {
                return new EhfInvoice(contentsAsXml);
        } else if (peppolDocumentTypeId.getLocalName().equals(LocalName.CreditNote)){
                return new EhfCreditInvoice(contentsAsXml);
        }

        return new DefaultPeppolDocument(contentsAsXml);
    }

}

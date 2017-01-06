package no.sr.ringo.document;

import eu.peppol.persistence.MessageNumber;
import eu.peppol.persistence.api.account.Account;

public interface DocumentRepository {

    /**
     * Locates the PEPPOL XML document message in a string format.
     * @param account the owner of the document
     * @param msgNo the message number of the message for which the XML document should be retrieved.
     *                  messageNo is the primary key in message table
     * @return PeppolDocumentObject encapsulating the contents of the XML document
     */
    PeppolDocument getPeppolDocument(Account account, MessageNumber msgNo);
}

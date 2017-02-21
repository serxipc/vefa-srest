package no.sr.ringo.document;

import no.sr.ringo.account.Account;
import no.sr.ringo.message.MessageNumber;

import javax.inject.Inject;

/**
 * Fetches the PeppolDocument associated with a message.  Can return
 * plain document (as received from customer) or a decorated version
 * with added XSLT stylesheet reference if the PeppolDocument is a type
 * that is known to us. e.g. Invoice, CreditInvoice. The stylesheet chosen
 * depends on the type of the PeppolDocument.
 */
public class FetchDocumentUseCase {

    private final DocumentRepository documentRepository;

    @Inject
    public FetchDocumentUseCase(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public PeppolDocument execute(Account account, MessageNumber msgNo) {
        return documentRepository.getPeppolDocument(account, msgNo);
    }
}

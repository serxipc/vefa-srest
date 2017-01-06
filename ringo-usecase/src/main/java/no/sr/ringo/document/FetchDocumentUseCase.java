package no.sr.ringo.document;

import eu.peppol.persistence.MessageNumber;
import eu.peppol.persistence.api.account.Account;

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
    private final PeppolDocumentDecoratorFactory peppolDocumentDecoratorFactory;

    @Inject
    public FetchDocumentUseCase(DocumentRepository documentRepository, PeppolDocumentDecoratorFactory peppolDocumentDecoratorFactory) {
        this.documentRepository = documentRepository;
        this.peppolDocumentDecoratorFactory = peppolDocumentDecoratorFactory;
    }

    public PeppolDocument execute(Account account, MessageNumber msgNo) {
        return documentRepository.getPeppolDocument(account, msgNo);
    }

    public PeppolDocument executeWithDecoration(Account account, MessageNumber msgNo) {
        PeppolDocument document = documentRepository.getPeppolDocument(account, msgNo);
        return peppolDocumentDecoratorFactory.decorateWithStyleSheet(document);
    }

}

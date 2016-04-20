package no.sr.ringo.document;

import no.sr.ringo.account.RingoAccount;
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
    private final PeppolDocumentDecoratorFactory peppolDocumentDecoratorFactory;

    @Inject
    public FetchDocumentUseCase(DocumentRepository documentRepository, PeppolDocumentDecoratorFactory peppolDocumentDecoratorFactory) {
        this.documentRepository = documentRepository;
        this.peppolDocumentDecoratorFactory = peppolDocumentDecoratorFactory;
    }

    public PeppolDocument execute(RingoAccount ringoAccount, MessageNumber msgNo) {
        return documentRepository.getPeppolDocument(ringoAccount, msgNo);
    }

    public PeppolDocument executeWithDecoration(RingoAccount ringoAccount, MessageNumber msgNo) {
        PeppolDocument document = documentRepository.getPeppolDocument(ringoAccount, msgNo);
        return peppolDocumentDecoratorFactory.decorateWithStyleSheet(document);
    }

}

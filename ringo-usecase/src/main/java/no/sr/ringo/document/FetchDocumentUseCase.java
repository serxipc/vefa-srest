package no.sr.ringo.document;

import no.sr.ringo.account.Account;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.message.PeppolMessageRepository;

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
    private final PeppolMessageRepository peppolMessageRepository;

    @Inject
    public FetchDocumentUseCase(DocumentRepository documentRepository, PeppolMessageRepository peppolMessageRepository) {
        this.documentRepository = documentRepository;
        this.peppolMessageRepository = peppolMessageRepository;
    }

    public PeppolDocument execute(Account account, MessageNumber msgNo) {

        final MessageMetaData messageMetaData = peppolMessageRepository.findMessageByMessageNo(account, msgNo);

        if (messageMetaData.getPayloadUri().getScheme().startsWith("file")) {
            return documentRepository.getPeppolDocument(account, msgNo);
        } else {
            throw new UnsupportedOperationException("Can only handle file urls for now");
        }

    }
}

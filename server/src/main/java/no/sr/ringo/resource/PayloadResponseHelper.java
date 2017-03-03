package no.sr.ringo.resource;

import no.sr.ringo.account.Account;
import no.sr.ringo.document.FetchDocumentResult;
import no.sr.ringo.document.FetchDocumentResultVisitor;
import no.sr.ringo.document.FetchDocumentUseCase;
import no.sr.ringo.message.MessageNumber;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Helper class to assist when retrieving payloads from the database and creating appropriate REST response.
 *
 * This class simply fetches the meta data and invokes a visitor in order to create a proper REST response.
 *
 * The payload can reside either in a) the file system, accessible to this instance of the REST service or b)
 * somewhere on the internet accessible via a URL.
 *
 * In alternative b), the URL obtained from the database most likely needs to be modified in order add an access token.
 * I.e. the payload URL should only be accessible for a short period of time and needs to have some credentials added
 * to it.
 *
 *
 * I.e. some payloads might need to be rewritten.
 *
 * @author steinar
 *         Date: 02.03.2017
 *         Time: 18.51
 */
public class PayloadResponseHelper {


    private final FetchDocumentResultVisitor<Response> fetchDocumentResultVisitor;

    /**
     *
     * @param fetchDocumentResultVisitor the visitor responsible for determining how to handle the
     * {@link FetchDocumentResult} from the {@link FetchDocumentUseCase#findDocument(Account, MessageNumber)}
     *
     */
    @Inject
    public PayloadResponseHelper(FetchDocumentResultVisitor<Response> fetchDocumentResultVisitor ) {
        this.fetchDocumentResultVisitor = fetchDocumentResultVisitor;
    }

    /**
     * Retrieves the payload URL from the database in the representation of a {@link FetchDocumentResult}
     * 
     * @param fetchDocumentUseCase
     * @param account
     * @param msgNo
     * @return
     */
    Response fetchPayloadAndProduceResponse(FetchDocumentUseCase fetchDocumentUseCase, Account account, MessageNumber msgNo) {

        // Retrieves either the document itself or a reference to where it is located
        FetchDocumentResult fetchDocumentResult = fetchDocumentUseCase.findDocument(account, msgNo);

        // Uses the visitor pattern to produce a Response, which will either contain
        // the xml text or an http 303 (see other) response.
        return fetchDocumentResult.accept(fetchDocumentResultVisitor);
    }

}

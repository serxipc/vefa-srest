package no.sr.ringo.document;

/**
 * @author steinar
 *         Date: 28.02.2017
 *         Time: 17.49
 */
public interface FetchDocumentResult {

    <T> T accept(FetcdocumentResultVisitor<T> frv);
}

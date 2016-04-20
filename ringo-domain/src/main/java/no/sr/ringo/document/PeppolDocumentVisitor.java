package no.sr.ringo.document;

/**
 * User: andy
 * Date: 10/29/12
 * Time: 10:03 AM
 */
public interface PeppolDocumentVisitor<T> {

    T visit(EhfInvoice ehfInvoice);

    T visit(EhfCreditInvoice ehfCreditInvoice);

    T visit(DefaultPeppolDocument defaultPeppolDocument);
}

package no.sr.ringo.document;

/**
 * Factory which is able to make decorators for peppolDocuments
 */
public interface PeppolDocumentDecoratorFactory {

    /**
     * Makes a decorator that inserts an XSLT stylesheet into the peppolDocument
     * so that when the raw xml is served via http a Human readable version is available in the web browser.
     *
     * @param peppolDocument
     * @return a decorated PeppolDocument
     */
    PeppolDocument decorateWithStyleSheet(PeppolDocument peppolDocument);

}

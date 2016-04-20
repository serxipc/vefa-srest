package no.sr.ringo.document;

/**
 * User: andy
 * Date: 10/29/12
 * Time: 10:50 AM
 */
public class PeppolDocumentDecoratorFactoryImpl implements PeppolDocumentDecoratorFactory {

    @Override
    public PeppolDocumentDecorator decorateWithStyleSheet(PeppolDocument peppolDocument) {
        return new PeppolDocumentXmlStyleSheetDecorator(peppolDocument);
    }

}

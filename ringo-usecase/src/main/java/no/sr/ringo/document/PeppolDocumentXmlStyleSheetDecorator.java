package no.sr.ringo.document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Changes the contents of a PeppolDocument by appending a xslt stylesheet.
 *
 * This means that when the document is viewed in a web browser it will
 * be displayed in a human readable way.
 *
 * Currently only EhfInvoice and EhfCreditInvoice will be decorated with the stylesheet.
 *
 * This class uses the visitor pattern to fetch the correct file name for the document type.
 *
 */
public class PeppolDocumentXmlStyleSheetDecorator extends PeppolDocumentDecorator {

    public PeppolDocumentXmlStyleSheetDecorator(PeppolDocument peppolDocument) {
        super(peppolDocument);
    }

    @Override
    public String getXml() {
        String styleSheet = getStyleSheetFileName();
        return decorateWith(styleSheet);
    }

    protected String getStyleSheetFileName() {
        return peppolDocument.acceptVisitor(new StyleSheetFileNameVisitor());
    }

    private String decorateWith(String styleSheet) {
        return peppolDocument.acceptVisitor(new DecorateDocumentVisitor(styleSheet));
    }

    /**
     * Visitor which selects the name of a stylesheet based on the PeppolDocument type
     */
    public static class StyleSheetFileNameVisitor implements PeppolDocumentVisitor<String> {

        public static final String EHF_INVOICE_STYLESHEET= "EHF-faktura_smaa.xslt";
        public static final String EHF_CREDIT_INVOICE_STYLESHEET = "EHF-kreditnota_smaa.xslt";

        @Override
        public String visit(EhfInvoice ehfInvoice) {
            return EHF_INVOICE_STYLESHEET;
        }

        @Override
        public String visit(EhfCreditInvoice ehfCreditInvoice) {
            return EHF_CREDIT_INVOICE_STYLESHEET;
        }

        @Override
        public String visit(DefaultPeppolDocument defaultPeppolDocument) {
            return "";
        }
    }

    /**
     * Visitor which decorates peppolDocuments with the given style sheet
     */
    public static class DecorateDocumentVisitor implements PeppolDocumentVisitor<String> {

        private static final Pattern xmlHeading = Pattern.compile("<\\?xml[^>]+encoding=\"([^\"]+)\"[^>]+>(:?\\s*<\\?xml-stylesheet[^>]+>)?");
        private String styleSheet;

        public DecorateDocumentVisitor(String styleSheet) {
            this.styleSheet = styleSheet;
        }

        @Override
        public String visit(EhfInvoice ehfInvoice) {
            return decorate(ehfInvoice.getXml());
        }

        @Override
        public String visit(EhfCreditInvoice ehfCreditInvoice) {
            return decorate(ehfCreditInvoice.getXml());
        }

        @Override
        public String visit(DefaultPeppolDocument defaultPeppolDocument) {
            //do not style the default peppol document
            return defaultPeppolDocument.getXml();
        }

        /**
         * Uses a regular expression to search for the xml header, which is replaced with a new header and the style sheet
         */
        public String decorate(String xml){
            Matcher matcher = xmlHeading.matcher(xml);
            String result = xml;
            if (matcher.lookingAt()) {
                result = matcher.replaceFirst("<?xml version=\"1.0\" encoding=\"$1\" ?>\n" + appendStyleSheet(styleSheet));
            }
            return result;
        }

        protected String appendStyleSheet(String styleSheet) {
            return String.format("<?xml-stylesheet type=\"text/xsl\" href=\"/xslt/%s\"?>\n", styleSheet);
        }

    }

}

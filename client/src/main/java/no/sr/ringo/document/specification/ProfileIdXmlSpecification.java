package no.sr.ringo.document.specification;

import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.jdom.Element;

public class ProfileIdXmlSpecification extends PeppolDocumentSpecification<ProcessIdentifier> {

    /**
     * The xpath expression used to select node/nodes
     */
    public String getXPath() {
        return "//cbc:ProfileID";
    }

    public ProcessIdentifier extractEntity(Element element) throws Exception {
        return ProcessIdentifier.of(element.getText());
    }
}

package no.sr.ringo.document.specification;

import no.sr.ringo.peppol.CustomizationIdentifier;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.RootNameSpace;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.Collections;
import java.util.List;

public class PeppolDocumentTypeIdXmlSpecification extends PeppolDocumentSpecification<PeppolDocumentTypeId> {

    /**
     * The xpath expression used to select node/nodes
     */
    public String getXPath() {
        return "/*";
    }

    public PeppolDocumentTypeId extractEntity(Element element) throws Exception {

        String rootNamespace = element.getNamespace().getURI();
        LocalName localName = LocalName.valueOf(element.getName());

        Namespace cbcNamespace = Namespace.getNamespace("cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        String version = element.getChild("UBLVersionID", cbcNamespace).getTextTrim();
        String customisationId = element.getChild("CustomizationID", cbcNamespace).getTextTrim();

        return new PeppolDocumentTypeId(new RootNameSpace(rootNamespace),localName, CustomizationIdentifier.valueOf(customisationId),version);
    }

    @Override
    public List<Namespace> getNamespaces() {
        return Collections.emptyList();
    }
}

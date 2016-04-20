package no.sr.ringo.document.specification;

import no.sr.ringo.cenbiimeta.ProfileId;
import org.jdom.Element;

public class ProfileIdXmlSpecification extends PeppolDocumentSpecification<ProfileId> {

    /**
     * The xpath expression used to select node/nodes
     */
    public String getXPath() {
        return "//cbc:ProfileID";
    }

    public ProfileId extractEntity(Element element) throws Exception {
        return new ProfileId(element.getText());
    }
}

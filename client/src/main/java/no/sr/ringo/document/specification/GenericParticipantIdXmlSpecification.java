package no.sr.ringo.document.specification;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.peppol.Iso6523Util;
import org.jdom.Element;

/**
 * Finds the ParticipantIdentifier from the xpath given by the constructor.
 * Generic version of the RecipientParticipantIdXmlSpecification / SenderParticipantIdXmlSpecification
 */
public class GenericParticipantIdXmlSpecification extends PeppolDocumentSpecification<ParticipantIdentifier> {

    private final String xpath;

    public GenericParticipantIdXmlSpecification(String xpath) {
        this.xpath = xpath;
    }

    public String getXPath() {
        return xpath;
    }

    public ParticipantIdentifier extractEntity(Element element) throws Exception {
        String schemeIdName = element.getAttributeValue("schemeID");

        if (schemeIdName == null) {
            return ParticipantIdentifier.of(element.getText());
        }
        else {
            return Iso6523Util.participantIdentifierWithSchemeName(schemeIdName, element.getText());
        }
    }

}

package no.sr.ringo.document.specification;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import org.jdom.Element;

/**
 * Finds the ParticipantId from the xpath given by the constructor.
 * Generic version of the RecipientParticipantIdXmlSpecification / SenderParticipantIdXmlSpecification
 */
public class GenericParticipantIdXmlSpecification extends PeppolDocumentSpecification<ParticipantId> {

    private final String xpath;

    public GenericParticipantIdXmlSpecification(String xpath) {
        this.xpath = xpath;
    }

    public String getXPath() {
        return xpath;
    }

    public ParticipantId extractEntity(Element element) throws Exception {
        String schemeId = element.getAttributeValue("schemeID");
        final SchemeId partyId = SchemeId.parse(schemeId);
        if (partyId == null) {
            return ParticipantId.valueOf(element.getText());
        }
        else {
            return new ParticipantId(partyId, element.getText());
        }
    }

}

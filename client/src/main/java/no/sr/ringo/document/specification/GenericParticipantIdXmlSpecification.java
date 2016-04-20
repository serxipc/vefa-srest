package no.sr.ringo.document.specification;

import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.peppol.SchemeId;
import org.jdom.Element;

/**
 * Finds the PeppolParticipantId from the xpath given by the constructor.
 * Generic version of the RecipientParticipantIdXmlSpecification / SenderParticipantIdXmlSpecification
 */
public class GenericParticipantIdXmlSpecification extends PeppolDocumentSpecification<PeppolParticipantId> {

    private final String xpath;

    public GenericParticipantIdXmlSpecification(String xpath) {
        this.xpath = xpath;
    }

    public String getXPath() {
        return xpath;
    }

    public PeppolParticipantId extractEntity(Element element) throws Exception {
        String schemeId = element.getAttributeValue("schemeID");
        final SchemeId partyId = SchemeId.parse(schemeId);
        if (partyId == null) {
            return PeppolParticipantId.valueFor(element.getText());
        }
        else {
            return new PeppolParticipantId(partyId, element.getText());
        }
    }

}

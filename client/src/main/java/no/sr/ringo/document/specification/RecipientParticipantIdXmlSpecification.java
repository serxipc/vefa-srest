package no.sr.ringo.document.specification;

import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.peppol.SchemeId;
import org.jdom.Element;

/**
 * Finds the PeppolParticipantId from the file
 * <p/>
 * The file needs to contain an entity which can be fetched by xpath at this location
 * <p/>
 * cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
 * <p/>
 * Company ids in the form of NO&lt;ORG_NO&gt;MVA, &lt;ORG_NO&gt;MVA, NO&lt;ORG_NO&gt;  &lt;ORG_NO&gt; are supported.
 * This means that only norwegian ParticipantIds can be automatically fetched.
 */
public class RecipientParticipantIdXmlSpecification extends PeppolDocumentSpecification<PeppolParticipantId> {

    /**
     * The xpath expression used to select node/nodes
     */
    public String getXPath() {
        return "//cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
    }

    /**
     * First checks that the element contains the schemeID attribute, which is used to
     * specify the numeric ISO652 code. If a valid code is found then the contents of
     * the companyID element are used to create the PeppolParticipantId object.
     * <p/>
     *
     * @param element
     * @return
     * @throws Exception if a problem occurs during parsing.
     */
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

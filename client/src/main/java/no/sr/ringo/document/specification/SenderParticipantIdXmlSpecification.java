package no.sr.ringo.document.specification;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.peppol.Iso6523Util;
import org.jdom.Element;

/**
 * Finds the ParticipantIdentifier from the file
 * <p/>
 * The file needs to contain an entity which can be fetched by xpath at this location
 * <p/>
 * cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID
 * <p/>
 * Company ids in the form of NO&lt;ORG_NO&gt;MVA, &lt;ORG_NO&gt;MVA, NO&lt;ORG_NO&gt;  &lt;ORG_NO&gt; are supported.
 * This means that only norwegian ParticipantIds can be automatically fetched.
 */
public class SenderParticipantIdXmlSpecification extends PeppolDocumentSpecification<ParticipantIdentifier> {

    /**
     * The xpath expression used to select node/nodes
     */
    public String getXPath() {
        return "//cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cbc:CompanyID";
    }

    /**
     * First checks that the element contains the schemeID attribute, which is used to
     * specify the numeric ISO652 code. If a valid code is found then the contents of
     * the companyID element are used to of the ParticipantIdentifier object.
     * <p/>
     *
     * @param element
     * @return
     * @throws Exception if a problem occurs during parsing.
     */
    public ParticipantIdentifier extractEntity(Element element) throws Exception {

        String schemeId = element.getAttributeValue("schemeID");

        if (schemeId == null) {
            return ParticipantIdentifier.of(element.getText());
        }
        else {
            return Iso6523Util.participantIdentifierWithSchemeName(schemeId, element.getText());
        }

    }
}

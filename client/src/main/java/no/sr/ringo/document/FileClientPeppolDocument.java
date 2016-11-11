package no.sr.ringo.document;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.document.specification.*;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.File;

/**
 * ClientPeppolDocument which represents a PeppolDocument backed by a java.io.File object
 * Provides a method for automatically extracting a valid PeppolHeader from the contents
 * of the file.
 */
public class FileClientPeppolDocument extends ClientPeppolDocument {

    static final SAXBuilder saxBuilder = new SAXBuilder();
    private final File file;
    private Document document;

    public FileClientPeppolDocument(File file) {
        super();
        this.file = file;
    }

    @Override
    public ContentBody getContentBody() {
        return new FileBody(file,"application/xml");
    }

    /**
     * Automatically populates the peppol header with values
     * from the document if the specific value is not already set.
     *
     * e.g. if the receiver is not set then it is fetched from the peppol document
     *
     * @param header the peppol header which may or may not contain values already.
     * @return a peppolHeader that is fully populated with values
     */
    public PeppolHeader populate(PeppolHeader header) {
        PeppolHeader peppolHeader = super.populate(header);

        // first we have to decode the document id (used to identify documents later)
        if (peppolHeader.getPeppolDocumentTypeId() == null) {
            peppolHeader.setPeppolDocumentTypeId(findDocumentType());
        }

        if (peppolHeader.getProfileId() == null) {
            peppolHeader.setProfileId(findProfileId());
        }

        if ((peppolHeader.getSender() == null) || (peppolHeader.getReceiver() == null)) {
            makeSureWeOnlyDecodeKnownDocumentTypes(peppolHeader);
        }

        // check endpointid first, then try to decode the other options
        if (peppolHeader.getSender() == null) {
            peppolHeader.setSender(findGenericParticipantId("//cac:AccountingSupplierParty/cac:Party/cbc:EndpointID"));
        }
        if (peppolHeader.getSender() == null) {
            peppolHeader.setSender(findSender());
        }

        // check endpointid first, then try to decode the other options
        if (peppolHeader.getReceiver() == null) {
            peppolHeader.setReceiver(findGenericParticipantId("//cac:AccountingCustomerParty/cac:Party/cbc:EndpointID"));
        }
        if (peppolHeader.getReceiver() == null) {
            peppolHeader.setReceiver(findReceiver());
        }

        return peppolHeader;
    }

    private void makeSureWeOnlyDecodeKnownDocumentTypes(PeppolHeader peppolHeader) {
        if (peppolHeader.getPeppolDocumentTypeId() != null) {
            String type = peppolHeader.getPeppolDocumentTypeId().getLocalName().toString();
            if (!(LocalName.Invoice.toString().equalsIgnoreCase(type) || LocalName.CreditNote.toString().equalsIgnoreCase(type))) {
                throw new IllegalArgumentException("This Ringo Client version is unable to detect sender and receiver from " + type + " type xml files.");
            }
        }
    }

    ParticipantId findGenericParticipantId(String xpath) {
        XmlHelper<ParticipantId> participantIdXmlHelper = new XmlHelper<ParticipantId>(new GenericParticipantIdXmlSpecification(xpath));
        return participantIdXmlHelper.selectSingle(getDocument());
    }

    ParticipantId findReceiver() {
        XmlHelper<ParticipantId> participantIdXmlHelper = new XmlHelper<ParticipantId>(new RecipientParticipantIdXmlSpecification());
        return participantIdXmlHelper.selectSingle(getDocument());
    }

    ParticipantId findSender() {
        XmlHelper<ParticipantId> participantIdXmlHelper = new XmlHelper<ParticipantId>(new SenderParticipantIdXmlSpecification());
        return participantIdXmlHelper.selectSingle(getDocument());
    }


    PeppolDocumentTypeId findDocumentType() {
        XmlHelper<PeppolDocumentTypeId> documentTypeIdXmlHelper = new XmlHelper<PeppolDocumentTypeId>(new PeppolDocumentTypeIdXmlSpecification());
        documentTypeIdXmlHelper.rethrowException();
        return documentTypeIdXmlHelper.selectSingle(getDocument());
    }

    ProfileId findProfileId() {
        XmlHelper<ProfileId> processTypeIdXmlHelper = new XmlHelper<ProfileId>(new ProfileIdXmlSpecification());
        return processTypeIdXmlHelper.selectSingle(getDocument());
    }

    private Document getDocument() {
        if (this.document == null){
            try {
                document = saxBuilder.build(file);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Unable to parse file %s, make sure it is valid XML", file.getName()));
            }
        }
        return document;
    }

}

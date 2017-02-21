package no.sr.ringo.document;

import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.document.specification.*;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolHeader;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.File;

/**
 * ClientPeppolDocument which represents a PeppolDocument backed by a java.io.File object
 * 
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
            peppolHeader.setDocumentTypeIdentifier(findDocumentType());
        }

        if (peppolHeader.getProcessIdentifier() == null) {
            peppolHeader.setProcessIdentifier(findProfileId());
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
            String type = peppolHeader.getPeppolDocumentTypeId().getIdentifier();

            if (!(type.contains(LocalName.Invoice.toString()) || type.contains(LocalName.CreditNote.toString()))) {
                throw new IllegalArgumentException("This Ringo Client version is unable to detect sender and receiver from " + type + " type xml files.");
            }
        }
    }

    ParticipantIdentifier findGenericParticipantId(String xpath) {
        XmlHelper<ParticipantIdentifier> participantIdXmlHelper = new XmlHelper<ParticipantIdentifier>(new GenericParticipantIdXmlSpecification(xpath));
        return participantIdXmlHelper.selectSingle(getDocument());
    }

    ParticipantIdentifier findReceiver() {
        XmlHelper<ParticipantIdentifier> participantIdXmlHelper = new XmlHelper<ParticipantIdentifier>(new RecipientParticipantIdXmlSpecification());
        return participantIdXmlHelper.selectSingle(getDocument());
    }

    ParticipantIdentifier findSender() {
        XmlHelper<ParticipantIdentifier> participantIdXmlHelper = new XmlHelper<ParticipantIdentifier>(new SenderParticipantIdXmlSpecification());
        return participantIdXmlHelper.selectSingle(getDocument());
    }


    DocumentTypeIdentifier findDocumentType() {
        XmlHelper<DocumentTypeIdentifier> documentTypeIdXmlHelper = new XmlHelper<DocumentTypeIdentifier>(new PeppolDocumentTypeIdXmlSpecification());
        documentTypeIdXmlHelper.rethrowException();
        return documentTypeIdXmlHelper.selectSingle(getDocument());
    }

    ProcessIdentifier findProfileId() {
        XmlHelper<ProcessIdentifier> processTypeIdXmlHelper = new XmlHelper<ProcessIdentifier>(new ProfileIdXmlSpecification());
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

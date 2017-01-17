package no.sr.ringo.smp;

import eu.peppol.identifier.ParticipantId;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.difi.vefa.peppol.lookup.api.LookupException;
import no.sr.ringo.peppol.LocalName;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: andy
 * Date: 1/25/12
 * Time: 10:55 AM
 */
public class RingoSmpLookupImpl implements RingoSmpLookup {

    private static Logger logger = LoggerFactory.getLogger(RingoSmpLookupImpl.class);
    private final LookupClient lookupClient;


    @Inject
    public RingoSmpLookupImpl(LookupClient lookupClient) {

        this.lookupClient = lookupClient;
    }

    @Override
    public boolean isRegistered(ParticipantId participantId) {
        fetchAcceptedDocumentTypesFromManager(participantId);
        return true;
    }

    @Override
    public SmpLookupResult fetchSmpMetaData(ParticipantId peppolParticipantId, LocalName localName) {
        SmpLookupResult result = new SmpLookupResult(Collections.<PeppolDocumentTypeId>emptyList());

        List<PeppolDocumentTypeId> documentTypes;
        try {
            documentTypes = fetchAcceptedDocumentTypesFromManager(peppolParticipantId);
        } catch (Exception e) {
            return result;
        }
        if (!documentTypes.isEmpty()) {
            List<PeppolDocumentTypeId> documentTypeForLocalName = RingoSmpLookupImpl.extractDocumentTypesForLocalName(documentTypes, localName);
            result = new SmpLookupResult(documentTypeForLocalName);
        }
        return result;
    }

    @Override
    public boolean isAcceptable(ParticipantId participantId, PeppolDocumentTypeId peppolDocumentTypeId) {
        List<PeppolDocumentTypeId> documentTypes;
        try {
            documentTypes = fetchAcceptedDocumentTypesFromManager(participantId);
        } catch (Exception e) {
            return false;
        }
        return documentTypes.contains(peppolDocumentTypeId);
    }

    static List<PeppolDocumentTypeId> extractDocumentTypesForLocalName(List<PeppolDocumentTypeId> documentTypes, LocalName localName) {
        List<PeppolDocumentTypeId> result = new ArrayList<PeppolDocumentTypeId>();
        for (PeppolDocumentTypeId documentTypeId : documentTypes) {
            if (localName.equals(documentTypeId.getLocalName())) {
                result.add(documentTypeId);
            }
        }
        return result;
    }

    private List<PeppolDocumentTypeId> fetchAcceptedDocumentTypesFromManager(ParticipantId peppolParticipantId)  {
        try {
            ParticipantIdentifier participantIdentifier = ParticipantIdentifier.of(peppolParticipantId.stringValue());
            List<DocumentTypeIdentifier> documentIdentifiers = lookupClient.getDocumentIdentifiers(participantIdentifier);


            return convertOxalisDocumentTypesToRingoDocumentTypes(documentIdentifiers);
        } catch (LookupException e) {
            logger.error("Error performing SMP lookup", e.getMessage());
            throw new IllegalStateException("Error occurred when performing SMP lookup", e);
        }
    }

    private List<PeppolDocumentTypeId> convertOxalisDocumentTypesToRingoDocumentTypes(List<DocumentTypeIdentifier> serviceGroups) {
        List<PeppolDocumentTypeId> documentTypeIds = new ArrayList<PeppolDocumentTypeId>();
        for (PeppolDocumentTypeId documentTypeId : documentTypeIds) {

        }
        for (DocumentTypeIdentifier documentTypeIdentifier : serviceGroups) {

            PeppolDocumentTypeId peppolDocumentTypeId = PeppolDocumentTypeId.valueOf(documentTypeIdentifier.getIdentifier());
            documentTypeIds.add(peppolDocumentTypeId);
        }
        return documentTypeIds;
    }

}

package no.sr.ringo.smp;

import eu.peppol.smp.ParticipantNotRegisteredException;
import eu.peppol.smp.SmpLookupException;
import eu.peppol.smp.SmpLookupManager;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.peppol.*;
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

    private final SmpLookupManager smpLookupManager;

    @Inject
    public RingoSmpLookupImpl(SmpLookupManager smpLookupManager) {
        this.smpLookupManager = smpLookupManager;
    }

    @Override
    public boolean isRegistered(PeppolParticipantId participantId) {
        try {
            fetchAcceptedDocumentTypesFromManager(participantId);
        } catch (ParticipantNotRegisteredException e) {
            return false;
        }
        return true;
    }

    @Override
    public SmpLookupResult fetchSmpMetaData(PeppolParticipantId peppolParticipantId, LocalName localName) {
        SmpLookupResult result = new SmpLookupResult(Collections.<PeppolDocumentTypeId>emptyList());
        List<PeppolDocumentTypeId> documentTypes;
        try {
            documentTypes = fetchAcceptedDocumentTypesFromManager(peppolParticipantId);
        } catch (ParticipantNotRegisteredException e) {
            return result;
        }
        if (!documentTypes.isEmpty()) {
            List<PeppolDocumentTypeId> documentTypeForLocalName = RingoSmpLookupImpl.extractDocumentTypesForLocalName(documentTypes, localName);
            result = new SmpLookupResult(documentTypeForLocalName);
        }
        return result;
    }

    @Override
    public boolean isAcceptable(PeppolParticipantId participantId, PeppolDocumentTypeId peppolDocumentTypeId) {
        List<PeppolDocumentTypeId> documentTypes;
        try {
            documentTypes = fetchAcceptedDocumentTypesFromManager(participantId);
        } catch (ParticipantNotRegisteredException e) {
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

    private List<PeppolDocumentTypeId> fetchAcceptedDocumentTypesFromManager(PeppolParticipantId peppolParticipantId) throws ParticipantNotRegisteredException {
        try {
            ParticipantId participant = new ParticipantId(peppolParticipantId.stringValue());
            List<eu.peppol.identifier.PeppolDocumentTypeId> serviceGroups = smpLookupManager.getServiceGroups(participant);
            return convertOxalisDocumentTypesToRingoDocumentTypes(serviceGroups);
        } catch (SmpLookupException e) {
            logger.error("Error performing SMP lookup", e.getMessage());
            throw new IllegalStateException("Error occurred when performing SMP lookup", e);
        }
    }

    private List<PeppolDocumentTypeId> convertOxalisDocumentTypesToRingoDocumentTypes(List<eu.peppol.identifier.PeppolDocumentTypeId> serviceGroups) {
        List<PeppolDocumentTypeId> documentTypeIds = new ArrayList<PeppolDocumentTypeId>();
        for (eu.peppol.identifier.PeppolDocumentTypeId serviceGroup : serviceGroups) {
            try {
                PeppolDocumentTypeId peppolDocumentTypeId = new PeppolDocumentTypeId(
                        new RootNameSpace(serviceGroup.getRootNameSpace()),
                        LocalName.valueOf(serviceGroup.getLocalName()),
                        CustomizationIdentifier.valueOf(serviceGroup.getCustomizationIdentifier().toString()),
                        serviceGroup.getVersion()
                    );
                documentTypeIds.add(peppolDocumentTypeId);
            } catch (Exception ex) {
                logger.warn("Unable to convertOxalisDocumentTypesToRingoDocumentTypes() for : " + serviceGroup.toDebugString());
            }
        }
        return documentTypeIds;
    }

}

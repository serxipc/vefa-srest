package no.sr.ringo.message;

import no.sr.ringo.peppol.PeppolHeader;

import java.util.function.Supplier;

/**
 * @author steinar
 *         Date: 27.02.2017
 *         Time: 19.33
 */
public class TransmissionMetaDataValidator {


    public static boolean validate(TransmissionMetaData tmd) {

        if (tmd.getTransferDirection() == null) {
            throw new IllegalArgumentException("TransferDirection is required");
        }
        containsData("receptionId", tmd::getReceptionId);
        containsData("received", tmd::getReceived);
        containsData("transferDirection", tmd::getTransferDirection);
        containsData("payloadUri", tmd::getPayloadUri);

        PeppolHeader ph = tmd.getPeppolHeader();
        containsData("sender", ph::getSender);
        containsData("receiver", ph::getReceiver);
        containsData("documentTypeId", ph::getPeppolDocumentTypeId);
        containsData("processIdentifier", ph::getProcessIdentifier);
        containsData("channel", ph::getPeppolChannelId);

        switch (tmd.getTransferDirection()) {
            case IN:
                containsData("evidenceUri", tmd::getEvidenceUri);
                break;
            case OUT:
                break;
        }

        return true;
    }

    static void containsData(String propertyName, Supplier<Object> supplier) {
        if (supplier.get() == null) {
            throw new IllegalArgumentException("Property '" + propertyName + "' is required");
        }
    }

}

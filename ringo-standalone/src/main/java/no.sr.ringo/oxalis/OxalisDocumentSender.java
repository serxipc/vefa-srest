package no.sr.ringo.oxalis;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolProcessTypeId;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.outbound.Transmitter;
import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import no.difi.vefa.peppol.common.model.Receipt;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.MessageMetaData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class OxalisDocumentSender implements PeppolDocumentSender {


    private final OxalisOutboundComponent oxalisOutboundModule;

    @javax.inject.Inject
    public OxalisDocumentSender(OxalisOutboundComponent oxalisOutboundComponent) {
        this.oxalisOutboundModule = oxalisOutboundComponent;
    }

    @Override
    public TransmissionReceipt sendDocument(MessageMetaData messageMetaData, String xmlMessage) throws Exception {

        TransmissionRequestBuilder requestBuilder = oxalisOutboundModule.getTransmissionRequestBuilder();

        requestBuilder
                .receiver(new ParticipantId(messageMetaData.getPeppolHeader().getReceiver().stringValue()))
                .sender((new ParticipantId(messageMetaData.getPeppolHeader().getSender().stringValue())))
                .documentType(PeppolDocumentTypeId.valueOf(messageMetaData.getPeppolHeader().getPeppolDocumentTypeId().stringValue()))
                .processType(PeppolProcessTypeId.valueOf(messageMetaData.getPeppolHeader().getProfileId().stringValue()))
                .payLoad(getXmlDocumentAsStream(xmlMessage));


        TransmissionRequest transmissionRequest = requestBuilder.build();
        Transmitter transmitter = oxalisOutboundModule.getTransmitter();
        TransmissionResponse transmissionResponse = transmitter.transmit(transmissionRequest);

        // Write the transmission id and where the message was delivered
        System.out.printf("Message sent to %s using %s was assigned transmissionId : %s\n",
                transmissionRequest.getEndpoint().getAddress().toString(),
                transmissionRequest.getEndpoint().getTransportProfile().getValue(),
                transmissionResponse.getMessageId()
        );

        Receipt receipt = transmissionResponse.primaryReceipt();
        
        return new TransmissionReceipt(
                transmissionResponse.getMessageId(),
                transmissionResponse.getEndpoint().getAddress(),
                new Date(),
                transmissionResponse.primaryReceipt()
        );
    }

    private InputStream getXmlDocumentAsStream(String xmlMessage) {
        try {
            return new ByteArrayInputStream(xmlMessage.getBytes(RingoConstants.DEFAULT_CHARACTER_SET));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}

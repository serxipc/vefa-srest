package no.sr.ringo.oxalis;

import eu.peppol.identifier.MessageId;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.TransmissionRequest;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import eu.peppol.outbound.transmission.TransmissionResponse;
import eu.peppol.outbound.transmission.Transmitter;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.MessageMetaData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class OxalisDocumentSender implements PeppolDocumentSender {


    private final OxalisOutboundModule oxalisOutboundModule;

    @javax.inject.Inject
    public OxalisDocumentSender(OxalisOutboundModule oxalisOutboundModule) {
        this.oxalisOutboundModule = oxalisOutboundModule;
    }

    @Override
    public TransmissionReceipt sendDocument(MessageMetaData messageMetaData, String xmlMessage) throws Exception {

        TransmissionRequestBuilder requestBuilder = oxalisOutboundModule.getTransmissionRequestBuilder();

        requestBuilder
                .trace(true)
                .receiver(new ParticipantId(messageMetaData.getPeppolHeader().getReceiver().stringValue()))
                .sender((new ParticipantId(messageMetaData.getPeppolHeader().getSender().stringValue())))
                .documentType(PeppolDocumentTypeId.valueOf(messageMetaData.getPeppolHeader().getPeppolDocumentTypeId().stringValue()))
                .processType(PeppolProcessTypeId.valueOf(messageMetaData.getPeppolHeader().getProfileId().stringValue()))
                .messageId(new MessageId(messageMetaData.getUuid()))
                .payLoad(getXmlDocumentAsStream(xmlMessage));


        TransmissionRequest transmissionRequest = requestBuilder.build();
        Transmitter transmitter = oxalisOutboundModule.getTransmitter();
        TransmissionResponse transmissionResponse = transmitter.transmit(transmissionRequest);

        // Write the transmission id and where the message was delivered
        System.out.printf("Message sent to %s using %s was assigned transmissionId : %s\n",
                transmissionRequest.getEndpointAddress().getUrl().toString(),
                transmissionRequest.getEndpointAddress().getBusDoxProtocol().toString(),
                transmissionResponse.getTransmissionId()
        );

        return new TransmissionReceipt(transmissionResponse.getTransmissionId().toString(), transmissionResponse.getURL(), new Date());

    }

    private InputStream getXmlDocumentAsStream(String xmlMessage) {
        try {
            return new ByteArrayInputStream(xmlMessage.getBytes(RingoConstants.DEFAULT_CHARACTER_SET));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}

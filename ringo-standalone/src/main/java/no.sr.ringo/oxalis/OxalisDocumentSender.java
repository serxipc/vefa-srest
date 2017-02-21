package no.sr.ringo.oxalis;

import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.outbound.Transmitter;
import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import no.difi.vefa.peppol.common.model.Receipt;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.message.MessageMetaData;
import no.sr.ringo.transport.TransmissionId;

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
                .receiver( messageMetaData.getPeppolHeader().getReceiver())
                .sender( messageMetaData.getPeppolHeader().getSender())
                .documentType(messageMetaData.getPeppolHeader().getPeppolDocumentTypeId())
                .processType((messageMetaData.getPeppolHeader().getProcessIdentifier()))
                .payLoad(getXmlDocumentAsStream(xmlMessage));


        TransmissionRequest transmissionRequest = requestBuilder.build();
        Transmitter transmitter = oxalisOutboundModule.getTransmitter();
        TransmissionResponse transmissionResponse = transmitter.transmit(transmissionRequest);

        // Write the transmission id and where the message was delivered
        System.out.printf("Message sent to %s using %s was assigned transmissionId : %s\n",
                transmissionRequest.getEndpoint().getAddress().toString(),
                transmissionRequest.getEndpoint().getTransportProfile().getValue(),
                transmissionResponse.getTransmissionIdentifier()
        );

        Receipt receipt = transmissionResponse.primaryReceipt();

        return new TransmissionReceipt(messageMetaData.getReceptionId(),
                // Transmission Id assigned by AS2 or whatever we are using
                new TransmissionId(transmissionResponse.getTransmissionIdentifier().toString()),
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

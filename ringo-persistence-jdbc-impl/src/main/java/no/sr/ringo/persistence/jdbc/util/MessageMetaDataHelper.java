/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.sr.ringo.persistence.jdbc.util;

import no.sr.ringo.message.MessageMetaDataEntity;
import no.sr.ringo.peppol.ChannelProtocol;
import no.sr.ringo.peppol.PeppolTransmissionMetaData;
import no.sr.ringo.transport.TransferDirection;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author steinar
 *         Date: 24.10.2016
 *         Time: 17.14
 */
public class MessageMetaDataHelper {

    /**
     * Converts an instance of {@link PeppolTransmissionMetaData} into a {@link MessageMetaDataEntity} object.
     * <p>
     * The direction of the message transfer, typically always {@link TransferDirection#IN} when you receive from the PEPPOL network,
     * hence this is the default.
     *
     * @param pm the {@link PeppolTransmissionMetaData} instance
     * @return instance of {@link MessageMetaDataEntity} with direction set to {@link TransferDirection#IN}
     */
    public static MessageMetaDataEntity createMessageMetaDataFrom(PeppolTransmissionMetaData pm) {

        MessageMetaDataEntity.Builder builder = new MessageMetaDataEntity.Builder(TransferDirection.IN, pm.getSenderId(), pm.getRecipientId(), pm.getDocumentTypeIdentifier(), ChannelProtocol.AS2);

        if (pm.getDocumentTypeIdentifier() == null) {
            throw new IllegalArgumentException("DocumentType identifier required");
        }
        builder.received(LocalDateTime.ofInstant(pm.getReceivedTimeStamp().toInstant(), ZoneId.systemDefault()))
                .delivered(pm.getSendersTimeStamp() != null ? LocalDateTime.ofInstant(pm.getSendersTimeStamp().toInstant(), ZoneId.systemDefault()) : null)
                .messageId(pm.getMessageId())
                .processTypeId(pm.getProfileTypeIdentifier())
                .accessPointIdentifier(pm.getSendingAccessPoint())
                .apPrincipal(pm.getSendingAccessPointPrincipal());

        return builder.build();
    }
}

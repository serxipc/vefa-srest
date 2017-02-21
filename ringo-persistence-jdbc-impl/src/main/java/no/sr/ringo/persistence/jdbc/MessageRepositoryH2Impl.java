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

package no.sr.ringo.persistence.jdbc;

import no.difi.vefa.peppol.common.model.*;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.message.*;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.persistence.file.ArtifactPathComputer;
import no.sr.ringo.persistence.file.ArtifactType;
import no.sr.ringo.persistence.guice.jdbc.JdbcTxManager;
import no.sr.ringo.persistence.guice.jdbc.Repository;
import no.sr.ringo.transport.TransferDirection;
import no.sr.ringo.transport.TransmissionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * MessageRepository implementation which will store the supplied messages in the file system and the meta data into a H2 database.
 *
 * @author Steinar Overbeck Cook
 * @author Thore Holmberg Johnsen
 */
@Repository
public class MessageRepositoryH2Impl implements MessageRepository {

    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryH2Impl.class);

    private final JdbcTxManager jdbcTxManager;
    private final ArtifactPathComputer artifactPathComputer;


    /**
     * This constructor is required for the META-INF/services idiom
     */
    @Inject
    public MessageRepositoryH2Impl(JdbcTxManager jdbcTxManager, ArtifactPathComputer artifactPathComputer) {
        this.jdbcTxManager = jdbcTxManager;
        this.artifactPathComputer = artifactPathComputer;
    }


    /**
     * Saves an outbound message received from the back-end to the file system, with meta data saved into the DBMS.
     *
     * @param transmissionMetaData
     * @param payloadInputStream
     * @return
     */
    public Long saveOutboundMessage(TransmissionMetaData transmissionMetaData, InputStream payloadInputStream)  {

        if (transmissionMetaData.getAccountId() == null) {
            throw new IllegalArgumentException("Outbound messages from back-end must have account id");
        }

        ArtifactPathComputer.FileRepoKey fileRepoKey = fileRepoKeyFrom(transmissionMetaData);

        Path documentPath = persistArtifact(ArtifactType.PAYLOAD, payloadInputStream, fileRepoKey);

        return createMetaDataEntry(transmissionMetaData, documentPath.toUri());
    }

    @Override
    public Long saveOutboundMessage(TransmissionMetaData transmissionMetaData, Document payloadDocument)  {

        if (transmissionMetaData.getAccountId() == null) {
            throw new IllegalArgumentException("Outbound messages from back-end must have account id");
        }
        ArtifactPathComputer.FileRepoKey fileRepoKey = fileRepoKeyFrom(transmissionMetaData);

        Path documentPath = persistArtifactFromDocument(ArtifactType.PAYLOAD, payloadDocument, fileRepoKey);

        return createMetaDataEntry(transmissionMetaData, documentPath.toUri());
    }


    /**
     * Saves inbound messages from PEPPOL network.
     * <p>
     * An attempt is made to locate the associated account by the receivers {@link ParticipantIdentifier} supplied in the meta data
     *
     * @param payloadInputStream
     * @return
     */
    @Override
    public Long saveInboundMessage(TransmissionMetaData mmd, InputStream payloadInputStream) {

        if (mmd.getReceptionId() == null) {
            throw new IllegalArgumentException("Missing ReceptionId value in TransmissionMetaData");
        }
        ArtifactPathComputer.FileRepoKey fileRepositoryMetaData = fileRepoKeyFrom(mmd.getReceptionId(), no.sr.ringo.transport.TransferDirection.IN, mmd.getPeppolHeader().getSender(), mmd.getPeppolHeader().getReceiver(), mmd.getReceived());

        // Saves the payload to the file store
        Path documentPath = persistArtifact(ArtifactType.PAYLOAD, payloadInputStream, fileRepositoryMetaData);
        URI payloadUrl = documentPath.toUri();

        // Locates the account for which the received message should be attached to.
        AccountId account = srAccountIdForReceiver(mmd.getPeppolHeader().getReceiver());
        if (account == null) {
            log.warn("Message from " + mmd.getPeppolHeader().getSender() + " will be persisted without account_id");
        } else {
            log.info("Inbound message from " + mmd.getPeppolHeader().getSender() + " will be saved to account " + account);

            // Must cat to the actual implementation
            // TODO: remove this once this is handled in the INSERT statement
            MessageMetaDataImpl messageMetaData = (MessageMetaDataImpl) mmd;
            messageMetaData.setAccountId(account);
        }

        return createMetaDataEntry(mmd, payloadUrl);
    }


    @Override
    public void saveOutboundTransportReceipt(Receipt transmissionEvidence, ReceptionId receptionId) {
        no.sr.ringo.transport.TransferDirection transferDirection = no.sr.ringo.transport.TransferDirection.OUT;

        Optional<? extends MessageMetaData> messageMetaDataOptional = findByReceptionId(transferDirection, receptionId);

        if (messageMetaDataOptional.isPresent()) {
            MessageMetaData mmd = messageMetaDataOptional.get();
            ArtifactPathComputer.FileRepoKey fileRepoKey = fileRepoKeyFrom(new ReceptionId(receptionId.stringValue()), transferDirection, mmd.getPeppolHeader().getSender(), mmd.getPeppolHeader().getReceiver(), mmd.getReceived());
            InputStream receiptInputStream = new ByteArrayInputStream(transmissionEvidence.getValue());
            Path nativeEvidencePath = persistArtifact(ArtifactType.NATIVE_EVIDENCE, receiptInputStream, fileRepoKey);

            updateMetadataForEvidence(transferDirection, receptionId, nativeEvidencePath);
        } else
            throw new IllegalStateException("Can not persist native transport evidence for non-existent messageId " + receptionId);
    }

    @Override
    public TransmissionMetaData findByMessageNo(Long msgNo) {
        if (msgNo == null) {
            throw new IllegalArgumentException("msgNo parameter required");
        }

        String sql = "select * from message where msg_no=?";
        Connection connection = jdbcTxManager.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, msgNo);

            TransmissionMetaData result = null;
            ResultSet rs = preparedStatement.executeQuery();

            List<TransmissionMetaData> MessageMetaDataList = messageMetaDataFrom(rs);

            if (MessageMetaDataList.size() == 1) {
                result = MessageMetaDataList.get(0);
            } else if (MessageMetaDataList.size() > 1) {
                throw new IllegalStateException("More than a single entry found for messageNo " + msgNo);
            } else if (MessageMetaDataList.isEmpty()) {
                throw new IllegalStateException("Message no " + msgNo + " not found");
            }

            return result;

        } catch (SQLException e) {
            throw new IllegalStateException("Error retrieving msg " + msgNo + " using " + sql + "\n" + e.getMessage(), e);
        }
    }


    @Override
    public Optional<? extends MessageMetaData> findByReceptionId(no.sr.ringo.transport.TransferDirection transferDirection, ReceptionId receptionId) {

        List<? extends MessageMetaData> byReceptionId = findByReceptionId(receptionId);
        for (MessageMetaData messageMetaData : byReceptionId) {
            if (messageMetaData.getTransferDirection() == transferDirection) {
                return Optional.of(messageMetaData);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TransmissionMetaData> findByReceptionId(ReceptionId receptionId) {
        if (receptionId == null) {
            throw new IllegalArgumentException("Argument messageId is required");
        }

        String sql = "select * from message where message_uuid=?";
        Connection con = jdbcTxManager.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, receptionId.stringValue());
            ResultSet rs = ps.executeQuery();

            final List<TransmissionMetaData> transmissionMetaDataList = messageMetaDataFrom(rs);


            return transmissionMetaDataList;

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed: " + e.getMessage(), e);
        }

    }

    private ArtifactPathComputer.FileRepoKey fileRepoKeyFrom(ReceptionId receptionId, no.sr.ringo.transport.TransferDirection transferDirection, ParticipantIdentifier sender, ParticipantIdentifier receiver, java.util.Date received) {
        if (receptionId == null) {
            throw new IllegalArgumentException("Missing argument receptionId");
        }
        return new ArtifactPathComputer.FileRepoKey(transferDirection, receptionId, sender, receiver, received);
    }


    private ArtifactPathComputer.FileRepoKey fileRepoKeyFrom(TransmissionMetaData transmissionMetaData) {
        return new ArtifactPathComputer.FileRepoKey(transmissionMetaData.getTransferDirection(),
                transmissionMetaData.getReceptionId(),
                transmissionMetaData.getPeppolHeader().getSender(),
                transmissionMetaData.getPeppolHeader().getReceiver(),
                transmissionMetaData.getReceived());
    }

    /**
     * Retrieves {@link MessageMetaData} instances from the provided {@link ResultSet}
     *
     * @param rs the result set as returned from the  {@link PreparedStatement#executeQuery()}
     * @return a list of {@link MessageMetaData}, which is empty if the result set is empty
     * @throws SQLException if any of the JDBC calls go wrong
     */
    protected List<TransmissionMetaData> messageMetaDataFrom(ResultSet rs) throws SQLException {

        List<TransmissionMetaData> result = new ArrayList<>();
        while (rs.next()) {

            final long msg_no = rs.getLong("msg_no");
            final AccountId account_id = new AccountId(rs.getInt("account_id"));
            final TransferDirection direction = TransferDirection.valueOf(rs.getString("direction"));

            // Received time stamp should never be null, but just in case.
            final Timestamp received = rs.getTimestamp("received");
            final Timestamp delivered = rs.getTimestamp("delivered");

            final ParticipantIdentifier sender = ParticipantIdentifier.of(rs.getString("sender"));
            final ParticipantIdentifier receiver = ParticipantIdentifier.of(rs.getString("receiver"));
            final PeppolChannelId channel = new PeppolChannelId(rs.getString("channel"));

            final ReceptionId message_uuid = new ReceptionId(rs.getString("message_uuid"));
            final String transmission_id = rs.getString("transmission_id");
            final String instance_id = rs.getString("instance_id");

            DocumentTypeIdentifier document_id = DocumentTypeIdentifier.of(rs.getString("document_id"));

            final String process_id = rs.getString("process_id");
            final String ap_name = rs.getString("ap_name");

            final URI payload_url = URI.create(rs.getString("payload_url"));

            String native_evidence_url = rs.getString("native_evidence_url");


            final MessageMetaDataImpl mmd = new MessageMetaDataImpl();
            final PeppolHeader peppolHeader = new PeppolHeader();
            mmd.setPeppolHeader(peppolHeader);

            mmd.setMsgNo(msg_no);
            mmd.setAccountId(account_id);
            mmd.setTransferDirection(direction);
            mmd.setReceived(received);
            mmd.setDelivered(delivered);
            peppolHeader.setSender(sender);
            peppolHeader.setReceiver(receiver);
            peppolHeader.setPeppolChannelId(channel);
            mmd.setReceptionId(message_uuid);
            mmd.setTransmissionId(new TransmissionId(transmission_id));
            peppolHeader.setDocumentTypeIdentifier(document_id);

            if (process_id != null) {
                peppolHeader.setProcessIdentifier(ProcessIdentifier.of(process_id));
            }

            if (instance_id != null) {
                mmd.setSbdhInstanceIdentifier(InstanceIdentifier.of(instance_id));
            }

            mmd.setPayloadUri(payload_url);
            if (native_evidence_url != null) {
                try {
                    mmd.setNativeEvidenceUri(new URI(native_evidence_url));
                } catch (URISyntaxException e) {
                    throw new IllegalStateException("Invalid native evidence URI for msg_no=" + msg_no + "; value=" + native_evidence_url + ", cause=" + e.getMessage(), e);
                }
            }

            result.add(mmd);
        }

        return result;
    }


    private Long createMetaDataEntry(TransmissionMetaData tmd, URI payloadUrl) {
        if (tmd == null) {
            throw new IllegalArgumentException("MessageMetaData required argument");
        }
        //
        //                                                            1           2           3       4       5            6               7           8           9           10          11          12
        final String INSERT_INTO_MESSAGE_SQL = "insert into message (account_id, direction, sender, receiver, channel, message_uuid, document_id, process_id, payload_url, received, delivered, transmission_id ) values(?,?,?,?,?,?,?,?,?,?,?,?)";

        Connection connection = null;
        try {
            long start = System.nanoTime();

            log.debug("Creating meta data entry:" + tmd);
            connection = jdbcTxManager.getConnection();

            log.debug("Using JDBC URL:" + connection.getMetaData().getURL());

            PreparedStatement insertStatement = connection.prepareStatement(INSERT_INTO_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS);
            if (tmd.getAccountId() == null)
                insertStatement.setNull(1, Types.INTEGER);
            else
                insertStatement.setInt(1, tmd.getAccountId().toInteger());

            insertStatement.setString(2, tmd.getTransferDirection().name());
            insertStatement.setString(3, tmd.getPeppolHeader().getSender() != null ? tmd.getPeppolHeader().getSender().getIdentifier() : null);
            insertStatement.setString(4, tmd.getPeppolHeader().getReceiver() != null ? tmd.getPeppolHeader().getReceiver().getIdentifier() : null);
            if (tmd.getPeppolHeader().getPeppolChannelId() != null)
                insertStatement.setString(5, tmd.getPeppolHeader().getPeppolChannelId().stringValue());
            else
                insertStatement.setString(5, null);

            insertStatement.setString(6, tmd.getReceptionId().stringValue());     // Unique id of message not to be mixed up with transmission id
            insertStatement.setString(7, tmd.getPeppolHeader().getPeppolDocumentTypeId().getIdentifier());
            insertStatement.setString(8, tmd.getPeppolHeader().getProcessIdentifier() != null ? tmd.getPeppolHeader().getProcessIdentifier().getIdentifier() : (null));   // Optional
            insertStatement.setString(9, payloadUrl.toString());

            insertStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.ofInstant(tmd.getReceived().toInstant(), ZoneId.systemDefault())));

            if (tmd.getDelivered() != null) {
                insertStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.ofInstant(tmd.getDelivered().toInstant(), ZoneId.systemDefault())));
            } else
                insertStatement.setTimestamp(11, null);

            if (tmd.getTransmissionId() != null) {
                insertStatement.setString(12, tmd.getTransmissionId().toString());
            } else
                insertStatement.setString(12, null);

            insertStatement.executeUpdate();

            long generatedKey = 0;

            boolean supportsGetGeneratedKeys = connection.getMetaData().supportsGetGeneratedKeys();
            log.debug("Supports generated keys: " + supportsGetGeneratedKeys);

            if (supportsGetGeneratedKeys) {
                ResultSet rs = insertStatement.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    generatedKey = rs.getLong(1);

                    log.debug("Inserted message with msg_no: " + generatedKey + " into table 'message'");
                } else {
                    log.debug("Inserted message into table 'message', but auto generated keys is not supported");
                }
            } else {
                log.debug("Inserted message into table 'message', auto generated keys not supported ");
            }

            insertStatement.close();
            long elapsed = System.nanoTime() - start;

            log.debug("Creating meta data entry took " + TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS) + "ms");

            return generatedKey;

        } catch (Exception e) {
            log.error("Unable to insert into message table using " + INSERT_INTO_MESSAGE_SQL + ", " + e, e);
            log.error("Please ensure that the DBMS and the MESSAGE table is available.");
            throw new IllegalStateException("Unable to create new entry in MESSAGE " + e.getMessage(), e);
        }
    }


    /**
     * Persists a payload represented as a W3C Document to the file system based upon the meta data
     *
     * @param artifactType
     * @param payloadDocument the payload represented as a W3C Document
     * @param fileRepoKey
     * @return
     */
    Path persistArtifactFromDocument(ArtifactType artifactType, Document payloadDocument, ArtifactPathComputer.FileRepoKey fileRepoKey) {

        Path path = createDirectoryForArtifact(artifactType, fileRepoKey);
        log.debug("Writing w3c document to " + path);

        DOMSource domSource = new DOMSource(payloadDocument);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            StreamResult streamResult = new StreamResult(Files.newBufferedWriter(path, Charset.forName("UTF-8")));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException | IOException e) {
            throw new IllegalStateException("Unable to write xml document to " + path + ". " + e.getMessage(), e);
        }
        return path;
    }

    Path persistArtifact(ArtifactType artifactType, InputStream inputStream, ArtifactPathComputer.FileRepoKey fileRepoKey) {

        long start = System.nanoTime();
        Path documentPath = createDirectoryForArtifact(artifactType, fileRepoKey);
        try {
            Files.copy(inputStream, documentPath);
            long elapsed = System.nanoTime() - start;
            log.debug(artifactType.getDescription() + " copied to " + documentPath + ", took " + TimeUnit.MILLISECONDS.convert(elapsed, TimeUnit.NANOSECONDS) + "ms");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save artifact to " + documentPath, e);
        }
        return documentPath;
    }

    Path createDirectoryForArtifact(ArtifactType artifactType, ArtifactPathComputer.FileRepoKey fileRepoKey) {
        Function<ArtifactPathComputer.FileRepoKey, Path> function = getFileRepoMetaDataPathFunction(artifactType);
        Path path = function.apply(fileRepoKey);
        verifyAndCreateDirectories(path);
        return path;
    }


    /**
     * Figures out which {@link Function} to apply for a given instance of {@link ArtifactType}
     *
     * @param artifactType the artifact type for which a function to apply should be determined.
     * @return the path computing function.
     */
    private Function<ArtifactPathComputer.FileRepoKey, Path> getFileRepoMetaDataPathFunction(ArtifactType artifactType) {

        Function<ArtifactPathComputer.FileRepoKey, Path> function;
        switch (artifactType) {
            case NATIVE_EVIDENCE:
                function = artifactPathComputer::createNativeEvidencePathFrom;
                break;
            case PAYLOAD:
                function = artifactPathComputer::createPayloadPathFrom;
                break;
            default:
                throw new IllegalStateException("No implementation for artifact type " + artifactType.name());
        }
        return function;
    }

    private void updateMetadataForEvidence(no.sr.ringo.transport.TransferDirection transferDirection, ReceptionId receptionId, Path nativeEvidencePath) {

        String dateColumnName = dateColumnNameFor(transferDirection);

        String sql = "update message set "
                + ArtifactType.NATIVE_EVIDENCE.getColumnName() + "=?, " // p1
                + dateColumnName + "=? " // p2
                + " where message_uuid = ? and direction=?"; // p3 & p4

        log.debug("Updating meta data: " + sql);
        Connection con = null;
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nativeEvidencePath.toUri().toString());
            ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            ps.setString(3, receptionId.stringValue());
            ps.setString(4, transferDirection.name());
            int i = ps.executeUpdate();
            if (i != 1) {
                throw new IllegalStateException("Unable to update message table for message_uuid=" + receptionId);
            }
            con.commit();

        } catch (SQLException e) {
            log.error("Unable to update message table." + e.getMessage(), e);
            throw new IllegalStateException("Unable to update database for storing genric and native evidene for message " + receptionId, e);
        }
    }

    private String dateColumnNameFor(no.sr.ringo.transport.TransferDirection transferDirection) {
        String dateColumnName = null;
        switch (transferDirection) {
            case IN:
                dateColumnName = "received";
                break;
            case OUT:
                dateColumnName = "delivered";
                break;
            default:
                throw new IllegalArgumentException("Unknown transferDirection: " + transferDirection);
        }
        return dateColumnName;
    }

    private void verifyAndCreateDirectories(Path documentPath) {
        Path directory = documentPath.getParent();
        if (Files.notExists(directory) || Files.isDirectory(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create directories for path " + directory);
            }
        }
    }

    // Package private to ease testing
    //

    AccountId srAccountIdForReceiver(ParticipantIdentifier participantId) {

        if (participantId == null) {
            return null;
        }

        Integer accountId = null;

        Connection con = null;

        String sql = "select account_id from account_receiver where participant_id=?";
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, participantId.getIdentifier());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Unable to obtain the account id for participant " + participantId + "; reason:" + e.getMessage(), e);
            log.error("SQL statement: " + sql);
            log.error("Using participant_id '" + participantId.getIdentifier() + "'");
            throw new IllegalStateException(sql + "; failed: " + e.getMessage(), e);
        }

        return new AccountId(accountId);
    }

}

package no.sendregning.peppol.persistence;

import eu.peppol.PeppolMessageMetaData;
//import eu.peppol.evidence.TransmissionEvidence;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.jdbc.OxalisDataSourceFactory;
import eu.peppol.jdbc.OxalisDataSourceFactoryProvider;
import eu.peppol.persistence.MessageRepository;
import eu.peppol.persistence.OxalisMessagePersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;

/**
 * MessageRepository implementation which will store the supplied messages and meta data into a MySQL database.
 *
 * @author Steinar Overbeck Cook
 * @author Thore Holmberg Johnsen
 */
public class MessageDbmsRepository implements MessageRepository {

    private static final String INSERT_INTO_MESSAGE_SQL = "insert into message (account_id, direction, sender, receiver, channel, message_uuid, document_id, process_id, remote_host, ap_name, xml_message ) values(?,?,?,?,?,?,?,?,?,?,?)";
    private static final Logger log = LoggerFactory.getLogger(MessageDbmsRepository.class);

    private DataSource dataSource = null; // TODO: make this class a singleton

    public MessageDbmsRepository() {
        log.debug("Obtaining an OxalisDataSourceFactoryProvider");
        OxalisDataSourceFactory oxalisDataSourceFactory = OxalisDataSourceFactoryProvider.getInstance();
        log.debug("Obtaining a DataSource...");
        dataSource = oxalisDataSourceFactory.getDataSource();
        log.debug("Initialization of " + MessageDbmsRepository.class.getSimpleName() + " completed");
    }

    @Override
    public void saveInboundMessage(PeppolMessageMetaData peppolMessageMetaData, Document document) throws OxalisMessagePersistenceException {

    }

    @Override
    public void saveInboundMessage(PeppolMessageMetaData peppolMessageMetaData, InputStream payloadInputStream) throws OxalisMessagePersistenceException {
        insertMessage(peppolMessageMetaData, payloadInputStream);
    }

    // TODO this is the new Oxalis v4 interface - not implemented yet
    /*
    @Override
    public void saveTransportReceipt(TransmissionEvidence transmissionEvidence, PeppolMessageMetaData peppolMessageMetaData) {
    }

    @Override
    public void saveNativeTransportReceipt(byte[] bytes) {
    }
    */

    //
    // helpers
    //

    private void insertMessage(PeppolMessageMetaData peppolMessageMetaData, InputStream documentInputStream) throws OxalisMessagePersistenceException {
        // Find the account identification for the receivers participant id
        Integer account = srAccountIdForReceiver(peppolMessageMetaData.getRecipientId());
        if (account == null) {
            log.error("Unable to find account for participant " + peppolMessageMetaData.getRecipientId());
            log.error("Message from " + peppolMessageMetaData.getSenderId() + " will be persisted without account_id");
        }

        Connection connection = null;
        try {

            connection = dataSource.getConnection();

            PreparedStatement insertStatement = connection.prepareStatement(INSERT_INTO_MESSAGE_SQL, Statement.RETURN_GENERATED_KEYS);
            if (account != null) {
                insertStatement.setInt(1, account);
            } else {
                insertStatement.setNull(1, Types.INTEGER);
            }
            insertStatement.setString(2, "IN");
            insertStatement.setString(3, peppolMessageMetaData.getSenderId() != null ? peppolMessageMetaData.getSenderId().stringValue() : null);
            insertStatement.setString(4, peppolMessageMetaData.getRecipientId() != null ? peppolMessageMetaData.getRecipientId().stringValue() : null);
            insertStatement.setString(5, peppolMessageMetaData.getProtocol() != null ? peppolMessageMetaData.getProtocol().toString() : "unknown");
            insertStatement.setString(6, peppolMessageMetaData.getTransmissionId() != null ? peppolMessageMetaData.getTransmissionId().toString() : null);
            insertStatement.setString(7, peppolMessageMetaData.getDocumentTypeIdentifier() != null ? peppolMessageMetaData.getDocumentTypeIdentifier().toString() : null);
            insertStatement.setString(8, peppolMessageMetaData.getProfileTypeIdentifier() != null ? peppolMessageMetaData.getProfileTypeIdentifier().toString() : null);
            insertStatement.setString(9, peppolMessageMetaData.getSendingAccessPoint() != null ? peppolMessageMetaData.getSendingAccessPoint().toString() : null);
            insertStatement.setString(10, peppolMessageMetaData.getSendingAccessPointPrincipal() != null ? peppolMessageMetaData.getSendingAccessPointPrincipal().getName() : null);


            insertStatement.setCharacterStream(11, new BufferedReader(new InputStreamReader(documentInputStream)));
            insertStatement.executeUpdate();

            if (connection.getMetaData().supportsGetGeneratedKeys()) {
                ResultSet rs = insertStatement.getGeneratedKeys();
                if (rs != null && rs.next()) {
                    long generatedKey = rs.getLong(1);

                    log.debug("Inserted message with msg_no: " + generatedKey + " into table 'message'");
                } else {
                    log.debug("Inserted message into table 'message', but auto generated keys is not supported");
                }
            } else {
                log.debug("Inserted message into table 'message'");
            }

            insertStatement.close();


        } catch (Exception e) {
            log.error("Unable to insert into message table using " + INSERT_INTO_MESSAGE_SQL + ", " + e, e);
            log.error("PEPPOL Message Header: " + peppolMessageMetaData.toString());
            log.error("Please ensure that the DBMS and the table is available according to the JNDI configuration.");
            throw new OxalisMessagePersistenceException(peppolMessageMetaData, e);
        } finally {
            close(connection);
        }

    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                log.error("Unable to close connection: " + e, e);
            }
        }
    }

    //
    // Package private to ease testing
    //

    Integer srAccountIdForReceiver(ParticipantId participantId) {

        if (participantId == null) {
            return null;
        }

        Integer accountId = null;

        Connection con = null;

        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("select account_id from account_receiver where participant_id=?");
            ps.setString(1, participantId.stringValue());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                accountId = rs.getInt(1);
            } else {
                return null;
            }
        } catch (SQLException e) {
            log.error("Unable to obtain the account id for participant " + participantId + "; reason:" + e.getMessage());
        } finally {
            close(con);
        }

        return accountId;

    }

    DataSource getDataSource() {
        return dataSource;
    }

}

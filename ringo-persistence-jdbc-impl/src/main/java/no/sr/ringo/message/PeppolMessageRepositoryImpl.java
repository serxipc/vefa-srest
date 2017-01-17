package no.sr.ringo.message;

import com.google.inject.Inject;
import eu.peppol.evidence.TransmissionEvidence;
import eu.peppol.identifier.MessageId;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.persistence.*;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.guice.jdbc.JdbcTxManager;
import eu.peppol.persistence.guice.jdbc.Repository;
import eu.peppol.persistence.jdbc.platform.DbmsPlatform;
import eu.peppol.persistence.jdbc.platform.DbmsPlatformFactory;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.message.statistics.InboxStatistics;
import no.sr.ringo.message.statistics.OutboxStatistics;
import no.sr.ringo.message.statistics.RingoAccountStatistics;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.utils.SbdhUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Repository for the message meta data entities.
 * <p>
 * TODO: This class should be merged with {@link MessageRepository} in oxalis-persistence.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @author Thore Holmberg Johnsen thore@sendregning.no
 */
@Repository
public class PeppolMessageRepositoryImpl implements PeppolMessageRepository {

    static final Logger log = LoggerFactory.getLogger(PeppolMessageRepositoryImpl.class);

    final JdbcTxManager jdbcTxManager;

    // From oxalis-persistence
    private final MessageRepository oxalisMessageRepository;

    @Inject
    public PeppolMessageRepositoryImpl(JdbcTxManager jdbcTxManager, MessageRepository oxalisMessageRepository) {
        this.jdbcTxManager = jdbcTxManager;
        this.oxalisMessageRepository = oxalisMessageRepository;
    }

    /**
     * Inserts or updates the supplied PEPPOL message to the database
     */
    @Override
    public MessageWithLocations persistOutboundMessage(Account account, PeppolMessage peppolMessage) {

        Connection con;
        if (account == null) {
            throw new IllegalStateException("SrAccountId property of message is required");
        }

        PeppolHeader peppolHeader = peppolMessage.getPeppolHeader();

        String sender = peppolHeader.getSender() != null ? peppolHeader.getSender().stringValue() : null;
        String receiver = peppolHeader.getReceiver() != null ? peppolHeader.getReceiver().stringValue() : null;
        String channelId = peppolHeader.getPeppolChannelId() != null ? peppolHeader.getPeppolChannelId().stringValue() : null;
        String documentTypeId = peppolHeader.getPeppolDocumentTypeId() != null ? peppolHeader.getPeppolDocumentTypeId().stringValue() : null;
        String profileId = peppolHeader.getProfileId() != null ? peppolHeader.getProfileId().stringValue() : null;

        // Converts from our Ringo Types to the Oxalis types and instantiates a builder.
        eu.peppol.persistence.MessageMetaData.Builder builder = new eu.peppol.persistence.MessageMetaData.Builder(TransferDirection.OUT,
                new ParticipantId(sender),
                new ParticipantId(receiver),
                eu.peppol.identifier.PeppolDocumentTypeId.valueOf(documentTypeId),
                ChannelProtocol.SREST);

        // Connects the data to the right account.
        builder.accountId(account.getAccountId().toInteger());

        builder.processTypeId(PeppolProcessTypeId.valueOf(profileId));
        eu.peppol.persistence.MessageMetaData metaData = builder.build();

        // Delegates to the injected message repository
        Long msgNo = null;
        try {
            msgNo = oxalisMessageRepository.saveOutboundMessage(metaData, peppolMessage.getXmlMessage());
        } catch (OxalisMessagePersistenceException e) {
            throw new IllegalStateException("Unable to persiste outbound message " + peppolMessage + "\n" + e.getMessage(), e);
        }


        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
        messageMetaData.setPeppolHeader(peppolHeader);
        messageMetaData.setTransferDirection(TransferDirection.OUT);
        messageMetaData.setMsgNo(msgNo);
        messageMetaData.setUuid(metaData.getMessageId().stringValue());

        return new MessageWithLocationsImpl(messageMetaData);
    }


    DbmsPlatform getDbmsPlatform(){
        return DbmsPlatformFactory.platformFor(jdbcTxManager.getConnection());
    }

    @Override
    public MessageMetaData findMessageByMessageNo(MessageNumber msgNo) throws PeppolMessageNotFoundException {
        try {
            Connection connection = jdbcTxManager.getConnection();
            SqlHelper sql = SqlHelper.create(getDbmsPlatform()).findMessageByMessageNo();
            PreparedStatement ps = sql.prepareStatement(connection);
            ps.setInt(1, msgNo.toInt());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractMessageFromResultSet(rs);
            } else {
                throw new PeppolMessageNotFoundException(msgNo.toLong());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + msgNo, e);
        }
    }

    @Override
    public MessageMetaData findMessageByMessageNo(Account account, Long messageNo) throws PeppolMessageNotFoundException {
        try {
            SqlHelper sql = SqlHelper.create(getDbmsPlatform()).findMessageByMessageNoAndAccountId();
            PreparedStatement ps = sql.prepareStatement(jdbcTxManager.getConnection());
            ps.setLong(1, messageNo);
            ps.setInt(2, account.getAccountId().toInteger());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractMessageFromResultSet(rs);
            } else {
                throw new PeppolMessageNotFoundException(messageNo);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + messageNo, e);
        }
    }

    @Override
    public Integer getInboxCount(AccountId accountId) {
        Integer result = 0;
        try {
            SqlHelper sql = SqlHelper.create(getDbmsPlatform()).inboxCount();
            Connection connection = jdbcTxManager.getConnection();
            PreparedStatement ps = sql.prepareStatement(connection);
            ps.setInt(1, accountId.toInteger());
            ps.setString(2, TransferDirection.IN.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to get count", e);
        }
        return result;
    }

    @Override
    public List<MessageMetaData> findUndeliveredOutboundMessagesByAccount(AccountId accountId) {
        return findUndeliveredMessagesByAccount(accountId, TransferDirection.OUT);
    }

    @Override
    public List<MessageMetaData> findUndeliveredInboundMessagesByAccount(AccountId accountId) {
        return findUndeliveredMessagesByAccount(accountId, TransferDirection.IN);
    }

    /**
     * Helper method for finding undelivered messages, which are either outbound or inbound.
     */
    List<MessageMetaData> findUndeliveredMessagesByAccount(AccountId accountId, TransferDirection transferDirection) {
        final SqlHelper sql = SqlHelper.create(getDbmsPlatform()).undeliveredMessagesSql(transferDirection);
        try {
            PreparedStatement ps = sql.prepareStatement(jdbcTxManager.getConnection());
            ps.setInt(1, accountId.toInteger());
            ps.setString(2, transferDirection.name());
            ResultSet rs = ps.executeQuery();
            return fetchAllMessagesFromResultSet(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to get messages", e);
        }
    }

    @Override
    public List<MessageMetaData> findMessages(AccountId accountId, MessageSearchParams searchParams) {
        try {
            SqlHelper sql = SqlHelper.create(getDbmsPlatform()).findMessages(searchParams);
            PreparedStatement ps = sql.prepareStatement(jdbcTxManager.getConnection());
            ps.setInt(1, accountId.toInteger());
            ResultSet rs = ps.executeQuery();
            return fetchAllMessagesFromResultSet(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Message search failed", e);
        }
    }

    @Override
    public Integer getMessagesCount(AccountId accountId, MessageSearchParams searchParams) {
        SqlHelper sql = SqlHelper.create(getDbmsPlatform()).messagesCount(searchParams);
        Integer result = 0;
        try {
            PreparedStatement ps = sql.prepareStatement(jdbcTxManager.getConnection());
            ps.setInt(1, accountId.toInteger());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Message count failed", e);
        }
        return result;
    }

    @Override
    public Integer getMessagesCount(AccountId accountId) {
        Connection con;
        Integer result = 0;
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement("select count(*) from message where account_id=?");
            ps.setInt(1, accountId.toInteger());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Message count failed", e);
        }
        return result;
    }

    @Override
    public void markMessageAsRead(Long messageNo) {
        Connection con;
        String sql = "update message set delivered = ? where msg_no = ?";
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(new Date().getTime()));
            ps.setLong(2, messageNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Marking message as read failed", e);
        }
    }

    @Override
    public List<MessageMetaData> findMessagesWithoutAccountId() {
        Connection con;
        List<MessageMetaData> metaData = new ArrayList<MessageMetaData>();
        String mainSql = "select msg_no, direction, received, delivered, sender, receiver, channel, document_id, process_id, message_uuid from message where account_id is null ";
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(mainSql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MessageMetaDataImpl m = extractMessageForResultSetWithoutAccountId(rs);
                metaData.add(m);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Message search failed", e);
        }
        return metaData;
    }

    @Override
    public void updateOutBoundMessageDeliveryDateAndUuid(MessageNumber msgNo, String remoteAP, MessageId messageId, Date delivered, byte[] nativeEvidenceBytes) {

        // Persists the evidence, after which the DBMS is updated
        try {
            persistOutboundEvidence(messageId, delivered, nativeEvidenceBytes);
        } catch (OxalisMessagePersistenceException e) {
            throw new IllegalStateException("Unable to persist evidence bytes to database");
        }

        Connection con;
        String sql = "update message set delivered = ?, ap_name = ?, message_uuid = ? where msg_no = ?";
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(delivered.getTime()));
            ps.setString(2, remoteAP);
            ps.setString(3, messageId.stringValue());
            ps.setLong(4, msgNo.toInt());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    void persistOutboundEvidence(final MessageId messageId, final Date delivered, final byte[] nativeEvidenceBytes) throws OxalisMessagePersistenceException {

        TransmissionEvidence transmissionEvidence = new TransmissionEvidence() {
            @Override
            public Date getReceptionTimeStamp() {
                return delivered;
            }

            @Override
            public InputStream getNativeEvidenceStream() {
                return new ByteArrayInputStream(nativeEvidenceBytes);
            }
        };

        oxalisMessageRepository.saveOutboundTransportReceipt(transmissionEvidence,messageId);
    }

    @Override
    public Long copyOutboundMessageToInbound(Long outMsgNo, String uuid) {
        Connection con;
        String sql = "insert into message (account_id, direction, received, sender, receiver, channel, message_uuid, document_id, process_id, payload_url) (select account_id, 'IN', received, sender, receiver, channel, ?, document_id, process_id, payload_url from message where msg_no = ?);";
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, uuid);
            ps.setLong(2, outMsgNo);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long msgNo = rs.getLong(1);
                return msgNo;
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    @Override
    public String findDocumentByMessageNoWithoutAccountCheck(Long messageNo) throws PeppolMessageNotFoundException {
        Connection con;
        String xmlMessage;
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement("select payload_url from message where msg_no=?");
            ps.setLong(1, messageNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String payloadUrl = SbdhUtils.removeSbdhEnvelope(rs.getString("payload_url"));

                try (Stream<String> lines = Files.lines(Paths.get(URI.create(payloadUrl)), Charset.forName("UTF-8"))) {
                    xmlMessage = lines.collect(joining(System.lineSeparator()));
                }
            } else
                throw new PeppolMessageNotFoundException(messageNo.longValue());
            return SbdhUtils.removeSbdhEnvelope(xmlMessage);
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + messageNo, e);
        }
    }

    @Override
    public boolean isSenderAndReceiverAccountTheSame(Long messageNo) {
        String query = "select" +
                "        EXISTS" +
                "                (select 1 from account_receiver ar, message m" +
                "                        where m.msg_no = ?" +
                "                        and m.receiver = ar.participant_id" +
                "                        and m.account_id = ar.account_id)" +
                "                 as same_account;";
        Connection con;
        Boolean same_account;
        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setLong(1, messageNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                same_account = rs.getBoolean("same_account");
            } else
                throw new PeppolMessageNotFoundException(messageNo.longValue());
            return same_account;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + messageNo, e);
        }
    }

    @Override
    public RingoStatistics getAdminStatistics() {
        return getAccountStatistics(null);
    }

    @Override
    public RingoStatistics getAccountStatistics(AccountId accountId) {

        List<RingoAccountStatistics> accountStatistics = new ArrayList<RingoAccountStatistics>();

        try {
            Connection con = jdbcTxManager.getConnection();
            final String selectSql = "SELECT " +
                    "    SUM(message.msg_no IS NOT NULL) AS \"total\", " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='OUT') AS \"out\", " +
                    "    SUM(msg_no IS NOT NULL " +
                    "        AND message.direction='OUT' " +
                    "        AND message.delivered IS NULL) AS \"undelivered out\", " +
                    "    outLatest.delivered AS \"last sent\", " +
                    "    outLatest.received AS \"last received out\", " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='IN' ) AS \"in\", " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='IN' " +
                    "        AND message.delivered IS NULL) AS \"undelivered in\", " +
                    "    inLatest.delivered AS \"last downloaded\", " +
                    "    inLatest.received AS \"last received in\", " +
                    "    inOldestUndelivered.received AS \"oldest undelivered in\", " +
                    "    a.id , " +
                    "    a.name,  " +
                    "    c.contact_email " +
                    "FROM " +
                    "    account a " +
                    "LEFT OUTER JOIN message  " +
                    "ON " +
                    "    message.account_id = a.id " +
                    "LEFT OUTER JOIN customer c " +
                    "ON " +
                    "    c.id = a.customer_id " +
                    "LEFT OUTER JOIN ( " +
                    "    select account_id, max(delivered) as delivered, max(received) as received from message " +
                    "    where direction = 'IN' " +
                    "    group by account_id " +
                    ") as inLatest " +
                    "ON a.id = inLatest.account_id " +
                    "LEFT OUTER JOIN (  \n" +
                    "   select account_id, min(received) as received from message  \n" +
                    "   where direction = 'IN'  \n" +
                    "   and delivered is null\n" +
                    "   group by account_id  \n" +
                    ") as inOldestUndelivered  \n" +
                    "ON a.id = inOldestUndelivered.account_id  \n " +
                    "LEFT OUTER JOIN ( " +
                    "    select account_id, max(delivered) as delivered, max(received) as received from message " +
                    "    where direction = 'OUT' " +
                    "    group by account_id " +
                    ") as outLatest " +
                    "ON a.id = outLatest.account_id ";

            //If an accountId is provided the where clause should
            //restrict the result set to that where clause
            final String whereClause;
            if (accountId != null) {
                whereClause = " where a.id = ? ";
            } else {
                whereClause = "";
            }

            final String groupBy = "GROUP BY a.Id, a.name ";
            final String orderBy = "ORDER BY a.name ASC; ";

            //generates the sqlStatement
            final String sql = selectSql + whereClause + groupBy + orderBy;

            PreparedStatement ps = con.prepareStatement(sql);
            //if we have an account id it needs to be provided to the statement
            if (accountId != null) {
                ps.setInt(1, accountId.toInteger());
            }

            //executes the sql and iterates the result set creating statistics for each account
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final RingoAccountStatistics statistics = extractAccountStatistics(rs);
                accountStatistics.add(statistics);

            }

        } catch (SQLException e) {
            throw new IllegalStateException("Unable to get ringo statistics", e);
        }

        return new RingoStatistics(accountStatistics);

    }

    private List<MessageMetaData> fetchAllMessagesFromResultSet(ResultSet rs) throws SQLException {
        List<MessageMetaData> metaData = new ArrayList<MessageMetaData>();
        while (rs.next()) {
            metaData.add(extractMessageFromResultSet(rs));
        }
        return metaData;
    }

    private MessageMetaDataImpl extractMessageFromResultSet(ResultSet rs) throws SQLException {
        MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
        messageMetaData.setMsgNo(rs.getLong("msg_no"));
        messageMetaData.setAccountId(new AccountId(rs.getInt("account_id")));
        messageMetaData.setTransferDirection(TransferDirection.valueOf(rs.getString("direction")));
        messageMetaData.setReceived(rs.getTimestamp("received"));
        messageMetaData.setDelivered(rs.getTimestamp("delivered"));
        messageMetaData.getPeppolHeader().setSender(ParticipantId.valueOf(rs.getString("sender")));

        String receiverAsString = rs.getString("receiver");
        ParticipantId receiver = ParticipantId.valueOf(receiverAsString);

        messageMetaData.getPeppolHeader().setReceiver(receiver);

        messageMetaData.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(rs.getString("channel")));
        String message_uuid = rs.getString("message_uuid");
        if (message_uuid != null) {
            messageMetaData.setUuid(message_uuid);
        }
        messageMetaData.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.valueOf(rs.getString("document_id")));

        String processId = rs.getString("process_id");
        if (processId != null) {
            messageMetaData.getPeppolHeader().setProfileId(new ProfileId(processId));
        }
        return messageMetaData;
    }

    private MessageMetaDataImpl extractMessageForResultSetWithoutAccountId(ResultSet rs) throws SQLException {
        MessageMetaDataImpl m = new MessageMetaDataImpl();
        m.setMsgNo(rs.getLong("msg_no"));
        m.setTransferDirection(TransferDirection.valueOf(rs.getString("direction")));
        m.setReceived(rs.getTimestamp("received"));
        m.setDelivered(rs.getTimestamp("delivered"));
        m.getPeppolHeader().setSender(ParticipantId.valueOf(rs.getString("sender")));
        m.getPeppolHeader().setReceiver(ParticipantId.valueOf(rs.getString("receiver")));
        m.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(rs.getString("channel")));
        m.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.valueOf(rs.getString("document_id")));
        m.getPeppolHeader().setProfileId(new ProfileId(rs.getString("process_id")));
        // UUIDs are heavy lifting, check for null values first.
        String uuidString = rs.getString("message_uuid");
        if (!rs.wasNull()) {
            m.setUuid(uuidString);
        }
        return m;
    }

    private RingoAccountStatistics extractAccountStatistics(ResultSet rs) throws SQLException {
        //get the inbox statistics
        int in = rs.getInt("in");
        int undeliveredIn = rs.getInt("undelivered in");
        final Timestamp lastDownloadedTs = rs.getTimestamp("last downloaded");
        Date lastDownloaded = lastDownloadedTs == null ? null : new Date(lastDownloadedTs.getTime());

        final Timestamp lastReceivedInTs = rs.getTimestamp("last received in");
        Date lastReceivedIn = lastReceivedInTs == null ? null : new Date(lastReceivedInTs.getTime());

        final Timestamp oldestUndeliveredInTs = rs.getTimestamp("oldest undelivered in");
        Date oldestUndeliveredIn = oldestUndeliveredInTs == null ? null : new Date(oldestUndeliveredInTs.getTime());

        final InboxStatistics inboxStatistics = new InboxStatistics(in, undeliveredIn, lastDownloaded, lastReceivedIn, oldestUndeliveredIn);

        //extract the outbox statistcs
        int total = rs.getInt("total");
        int out = rs.getInt("out");
        int undeliveredOut = rs.getInt("undelivered out");

        final Timestamp lastSentTs = rs.getTimestamp("last sent");
        Date lastSent = lastSentTs == null ? null : new Date(lastSentTs.getTime());

        final Timestamp lastReceivedOutTs = rs.getTimestamp("last received out");
        Date lastReceivedOut = lastReceivedOutTs == null ? null : new Date(lastReceivedOutTs.getTime());

        final OutboxStatistics outboxStatistics = new OutboxStatistics(out, undeliveredOut, lastSent, lastReceivedOut);

        //extract the account details
        int accountIdint = rs.getInt("id");
        String accountName = rs.getString("name");
        String contactEmail = rs.getString("contact_email");

        return new RingoAccountStatistics(total, inboxStatistics, outboxStatistics, new AccountId(accountIdint), accountName, contactEmail);
    }

    /**
     * Helps creating SQL statements
     */
    private static class SqlHelper {

        private final DbmsPlatform dbmsPlatform;
        private String sql;

        private SqlHelper(DbmsPlatform dbmsPlatform) {
            this.dbmsPlatform = dbmsPlatform;
        }

        public static SqlHelper create(DbmsPlatform dbmsPlatform) {
            return new SqlHelper(dbmsPlatform);
        }

        public SqlHelper inboxCount() {
            sql = "select count(*) from message where account_id=? and direction= ? and delivered is null and message_uuid is not null and message_uuid != ''";
            return this;
        }

        private SqlHelper undeliveredMessagesSql(TransferDirection transferDirection) {

            String limitCondition = dbmsPlatform.getLimitClause(0, DEFAULT_PAGE_SIZE);


            if (TransferDirection.IN.equals(transferDirection)) {
                // Delivered must be null and uuid must not be null for valid undelivered incoming messages
                sql = selectMessage() +
                        "where delivered is null and message_uuid is not null and message_uuid != '' and account_id=? and direction=? order by msg_no " + limitCondition;
            } else {
                // Delivered must be null and uuid must be null for valid undelivered outgoing messages
                sql = selectMessage() +
                        "where delivered is null " +
                        "and account_id=? and direction=? " +
                        "and not exists(select 1 from outbound_message_queue omq where omq.msg_no = message.msg_no and omq.state='AOD') order by msg_no " +
                        limitCondition;
            }
            return this;
        }

        public SqlHelper findMessageByMessageNoAndAccountId() {
            sql = selectMessage() + " where msg_no=? and account_id=?";
            return this;
        }

        public SqlHelper findMessageByMessageNo() {
            sql = selectMessage() + " where msg_no=? ";
            return this;
        }

        public PreparedStatement prepareStatement(Connection connection) throws SQLException {
            log.debug("Preparing " + sql);
            return connection.prepareStatement(sql);
        }

        public SqlHelper findMessages(MessageSearchParams searchParams) {
            sql = selectMessage() + "where account_id=? ";
            generateWhereClause(searchParams);
            sql = sql.concat(" order by msg_no ");
            generateLimitCondition(searchParams.getPageIndex());
            return this;
        }

        private String selectMessage() {
            return "select account_id, msg_no, direction, received, delivered, sender, receiver, channel, document_id, process_id, message_uuid from message ";
        }

        private String generateWhereClause(MessageSearchParams searchParams) {
            if (searchParams.getDirection() != null) {
                sql = sql.concat(" and direction = '" + searchParams.getDirection().name() + "'");
            }
            if (searchParams.getSender() != null) {
                sql = sql.concat(" and sender = '" + searchParams.getSender().stringValue() + "'");
            }
            if (searchParams.getReceiver() != null) {
                sql = sql.concat(" and receiver = '" + searchParams.getReceiver().stringValue() + "'");
            }
            if (searchParams.getSent() != null && searchParams.getDateCondition() != null) {
                // Mysql:  sql = sql.concat(" and Date(received) " + searchParams.getDateCondition().getValue() + "'" + searchParams.getSent() + "'");
                sql = sql.concat(" and FORMATDATETIME(received,'yyyy-MM-dd') " + searchParams.getDateCondition().getValue() + "'" + searchParams.getSent() + "'");
            }
            return sql;
        }

        private String generateLimitCondition(Integer pageIndex) {
            String limit = "";
            int offset = 0;
            if (pageIndex != null) {
                //first page should have offset 0, second one 25 etc...so subtracting 1 before multiplication
                offset = (pageIndex - 1) * PeppolMessageRepository.DEFAULT_PAGE_SIZE;
            }


            String limitClause = dbmsPlatform.getLimitClause(offset, DEFAULT_PAGE_SIZE);
            sql = sql.concat(" ").concat(limitClause);
            return sql;
        }

        public SqlHelper messagesCount(MessageSearchParams searchParams) {
            sql = "select count(*) from message where account_id=?";
            generateWhereClause(searchParams);
            return this;
        }

    }

}


package no.sr.ringo.message;

import com.google.inject.Inject;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Repository;
import no.sr.ringo.message.statistics.InboxStatistics;
import no.sr.ringo.message.statistics.OutboxStatistics;
import no.sr.ringo.message.statistics.RingoAccountStatistics;
import no.sr.ringo.message.statistics.RingoStatistics;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.peppol.PeppolHeader;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.utils.SbdhUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.transform.dom.DOMResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @author Thore Holmberg Johnsen thore@sendregning.no
 */
@Repository
public class PeppolMessageRepositoryImpl implements PeppolMessageRepository {

    static final Logger log = LoggerFactory.getLogger(PeppolMessageRepositoryImpl.class);

    final JdbcTxManager dataSource;

    @Inject
    public PeppolMessageRepositoryImpl(JdbcTxManager jdbcTxManager) {
        this.dataSource = jdbcTxManager;
    }

    /**
     * Inserts or updates the supplied PEPPOL message to the database
     */
    @Override
    public MessageWithLocations persistOutboundMessage(RingoAccount ringoAccount, PeppolMessage peppolMessage, String invoiceNo) {

        Connection con;
        if (ringoAccount == null) {
            throw new IllegalStateException("SrAccountId property of message is required");
        }

        PeppolHeader header = peppolMessage.getPeppolHeader();

        String sender = header.getSender() != null ? header.getSender().stringValue() : null;
        String receiver = header.getReceiver() != null ? header.getReceiver().stringValue() : null;
        String channelId = header.getPeppolChannelId() != null ? header.getPeppolChannelId().stringValue() : null;
        String documentTypeId = header.getPeppolDocumentTypeId() != null ? header.getPeppolDocumentTypeId().stringValue() : null;
        String profileId = header.getProfileId() != null ? header.getProfileId().stringValue() : null;

        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO message " +
                    "( direction, received, sender, receiver, channel, document_id, process_id, xml_message, account_id, invoice_no) " +
                    " VALUES  (?,?,?,?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);

            TransferDirection transferDirection = TransferDirection.OUT;
            ps.setString(1, transferDirection.name());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(2, timestamp);

            ps.setString(3, sender);
            ps.setString(4, receiver);
            ps.setString(5, channelId);
            ps.setString(6, documentTypeId);
            ps.setString(7, profileId);

            // Converts the XML Document into something which may be persisted into the database
            SQLXML sqlxml = con.createSQLXML();
            DOMResult domResult = sqlxml.setResult(DOMResult.class);
            domResult.setNode(peppolMessage.getXmlMessage());
            ps.setSQLXML(8, sqlxml);

            /* Converts the XML Document into String which may be persisted into the database
            DOMSource domSource = new DOMSource(peppolMessage.getXmlMessage());
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            ps.setString(8, writer.toString());
            */

            ps.setInt(9, ringoAccount.getId().toInteger());
            ps.setString(10, invoiceNo);

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();

            MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
            if (rs.next()) {
                int msgNo = rs.getInt(1);
                messageMetaData.setPeppolHeader(header);
                messageMetaData.setTransferDirection(TransferDirection.OUT);
                messageMetaData.setMsgNo(msgNo);

            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }

            return new MessageWithLocationsImpl(messageMetaData);

        } catch(SQLException e) {
            throw new IllegalStateException("Unable to insert message ", e);
        }
    }

    @Override
    public MessageWithLocations persistInboundMessage(RingoAccount ringoAccount, PeppolMessage peppolMessage, String invoiceNo, String remoteHost, String apName) {

        Connection con;
        if (ringoAccount == null) {
            throw new IllegalStateException("SrAccountId property of message is required");
        }

        PeppolHeader header = peppolMessage.getPeppolHeader();

        String sender = header.getSender() != null ? header.getSender().stringValue() : null;
        String receiver = header.getReceiver() != null ? header.getReceiver().stringValue() : null;
        String channelId = header.getPeppolChannelId() != null ? header.getPeppolChannelId().stringValue() : null;
        String documentTypeId = header.getPeppolDocumentTypeId() != null ? header.getPeppolDocumentTypeId().stringValue() : null;
        String profileId = header.getProfileId() != null ? header.getProfileId().stringValue() : null;

        /*
        mysql> describe message;
        +---------------+------------------+------+-----+-------------------+----------------+
        | Field         | Type             | Null | Key | Default           | IMPORT ACTION
        +---------------+------------------+------+-----+-------------------+----------------+
        | -msg_no       | int(11)          | NO   | PRI | NULL              | auto_increment
        | -account_id   | int(11)          | YES  | MUL | NULL              | ringoAccount.id
        | -direction    | enum('IN','OUT') | NO   |     | NULL              | "IN"
        | -received     | timestamp        | NO   |     | CURRENT_TIMESTAMP | current timestamp
        |  delivered    | datetime         | YES  |     | NULL              |
        | -sender       | varchar(32)      | NO   |     | NULL              | from peppolMessage
        | -receiver     | varchar(32)      | NO   |     | NULL              | from peppolMessage
        | -channel      | varchar(128)     | NO   |     | NULL              | from peppolMessage
        |  message_uuid | varchar(64)      | YES  |     | NULL              | null (have been lost in failed transfer)
        | -document_id  | varchar(256)     | NO   |     | NULL              | from peppolMessage
        | -process_id   | varchar(128)     | YES  |     | NULL              | from peppolMessage
        |  remote_host  | varchar(128)     | YES  |     | NULL              | ?
        |  ap_name      | varchar(128)     | YES  |     | NULL              | ?
        | -xml_message  | mediumtext       | YES  |     | NULL              | from peppolMessage
        | -invoice_no   | varchar(255)     | YES  |     | NULL              | ?
        +--------------+------------------+------+-----+-------------------+----------------+
        15 rows in set (0.01 sec)
        */

        try {

            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO message " +
                    "( direction, received, sender, receiver, channel, document_id, process_id, xml_message, account_id, invoice_no, remote_host, ap_name ) " +
                    " VALUES  (?,?,?,?,?,?,?,?,?,?,?,?) ", Statement.RETURN_GENERATED_KEYS);

            TransferDirection transferDirection = TransferDirection.IN;
            ps.setString(1, transferDirection.name());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(2, timestamp);

            ps.setString(3, sender);
            ps.setString(4, receiver);
            ps.setString(5, channelId);
            ps.setString(6, documentTypeId);
            ps.setString(7, profileId);

            // Converts the XML Document into something which may be persisted into the database
            SQLXML sqlxml = con.createSQLXML();
            DOMResult domResult = sqlxml.setResult(DOMResult.class);
            domResult.setNode(peppolMessage.getXmlMessage());
            ps.setSQLXML(8, sqlxml);
            ps.setInt(9, ringoAccount.getId().toInteger());
            ps.setString(10, invoiceNo);

            // Additional inbound transfer details
            ps.setString(11, remoteHost);
            ps.setString(12, apName);

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            MessageMetaDataImpl messageMetaData = new MessageMetaDataImpl();
            if (rs.next()) {
                int msgNo = rs.getInt(1);
                messageMetaData.setPeppolHeader(header);
                messageMetaData.setTransferDirection(TransferDirection.IN);
                messageMetaData.setMsgNo(msgNo);
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
            return new MessageWithLocationsImpl(messageMetaData);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to insert message ", e);
        }
    }

    @Override
    public MessageMetaData findMessageByMessageNo(MessageNumber msgNo) throws PeppolMessageNotFoundException {
        try {
            SqlHelper sql = SqlHelper.create().findMessageByMessageNo();
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
            ps.setInt(1, msgNo.toInt());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractMessageFromResultSet(rs);
            } else {
                throw new PeppolMessageNotFoundException(msgNo.toInt());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + msgNo, e);
        }
    }

    @Override
    public MessageMetaData findMessageByMessageNo(RingoAccount ringoAccount, Integer messageNo) throws PeppolMessageNotFoundException {
        try {
            SqlHelper sql = SqlHelper.create().findMessageByMessageNoAndAccountId();
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
            ps.setInt(1, messageNo);
            ps.setInt(2, ringoAccount.getId().toInteger());
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
            SqlHelper sql = SqlHelper.create().inboxCount();
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
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
        final SqlHelper sql = SqlHelper.create().undeliveredMessagesSql(transferDirection);
        try {
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
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
            SqlHelper sql = SqlHelper.create().findMessages(searchParams);
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
            ps.setInt(1, accountId.toInteger());
            ResultSet rs = ps.executeQuery();
            return fetchAllMessagesFromResultSet(rs);
        } catch (SQLException e) {
            throw new IllegalStateException("Message search failed", e);
        }
    }

    @Override
    public Integer getMessagesCount(AccountId accountId, MessageSearchParams searchParams) {
        SqlHelper sql = SqlHelper.create().messagesCount(searchParams);
        Integer result = 0;
        try {
            PreparedStatement ps = sql.prepareStatement(dataSource.getConnection());
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
            con = dataSource.getConnection();
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
    public void markMessageAsRead(Integer messageNo) {
        Connection con;
        String sql = "update message set delivered = ? where msg_no = ?";
        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(new Date().getTime()));
            ps.setInt(2, messageNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Marking message as read failed", e);
        }
    }

    @Override
    public List<MessageMetaData> findMessagesWithoutAccountId() {
        Connection con;
        List<MessageMetaData> metaData = new ArrayList<MessageMetaData>();
        String mainSql = "select msg_no, direction, received, delivered, sender, receiver, channel, document_id, process_id, message_uuid, invoice_no from message where account_id is null ";
        try {
            con = dataSource.getConnection();
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
    public void updateOutBoundMessageDeliveryDateAndUuid(Integer msgNo, String remoteAP, String uuid, Date delivered) {
        Connection con;
        String sql = "update message set delivered = ?, ap_name = ?, message_uuid = ? where msg_no = ?";
        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(delivered.getTime()));
            ps.setString(2, remoteAP);
            ps.setString(3, uuid);
            ps.setInt(4, msgNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    @Override
    public int copyOutboundMessageToInbound(Integer outMsgNo, String uuid) {
        Connection con;
        String sql = "insert into message (account_id, direction, received, sender, receiver, channel, message_uuid, document_id, process_id, xml_message, invoice_no) (select account_id, 'IN', received, sender, receiver, channel, ?, document_id, process_id, xml_message, invoice_no from message where msg_no = ?);";
        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, uuid);
            ps.setInt(2, outMsgNo);
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int msgNo = rs.getInt(1);
                return msgNo;
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    @Override
    public String findDocumentByMessageNoWithoutAccountCheck(Integer messageNo) throws PeppolMessageNotFoundException {
        Connection con;
        String xmlMessage;
        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement("select xml_message from message where msg_no=?");
            ps.setInt(1, messageNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                xmlMessage = SbdhUtils.removeSbdhEnvelope(rs.getString("xml_message"));
            } else
                throw new PeppolMessageNotFoundException(messageNo);
            return xmlMessage;
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to retrieve xml document for message no: " + messageNo, e);
        }
    }

    @Override
    public boolean isSenderAndReceiverAccountTheSame(Integer messageNo) {
        String query = "select IF(" +
                "        EXISTS" +
                "                (select 1 from account_receiver ar, message m" +
                "                        where m.msg_no = ?" +
                "                        and m.receiver = ar.participant_id" +
                "                        and m.account_id = ar.account_id), true, false)" +
                "                 as same_account;";
        Connection con;
        Boolean same_account;
        try {
            con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, messageNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                same_account = rs.getBoolean("same_account");
            } else
                throw new PeppolMessageNotFoundException(messageNo);
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
            Connection con = dataSource.getConnection();
            final String selectSql = "SELECT " +
                    "    SUM(message.msg_no IS NOT NULL) AS 'total', " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='OUT') AS 'out', " +
                    "    SUM(msg_no IS NOT NULL " +
                    "        AND message.direction='OUT' " +
                    "        AND message.delivered IS NULL) AS 'undelivered out', " +
                    "    outLatest.delivered AS 'last sent', " +
                    "    outLatest.received AS 'last received out', " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='IN' ) AS 'in', " +
                    "    SUM(message.msg_no IS NOT NULL " +
                    "        AND message.direction='IN' " +
                    "        AND message.delivered IS NULL) AS 'undelivered in', " +
                    "    inLatest.delivered AS 'last downloaded', " +
                    "    inLatest.received AS 'last received in', " +
                    "    inOldestUndelivered.received AS 'oldest undelivered in', " +
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
                final RingoAccountStatistics statistics = extractRingoAccountStatistics(rs);
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
        messageMetaData.setMsgNo(rs.getInt("msg_no"));
        messageMetaData.setInvoiceNo(rs.getString("invoice_no"));
        messageMetaData.setAccountId(new AccountId(rs.getInt("account_id")));
        messageMetaData.setTransferDirection(TransferDirection.valueOf(rs.getString("direction")));
        messageMetaData.setReceived(rs.getTimestamp("received"));
        messageMetaData.setDelivered(rs.getTimestamp("delivered"));
        messageMetaData.getPeppolHeader().setSender(PeppolParticipantId.valueFor(rs.getString("sender")));
        messageMetaData.getPeppolHeader().setReceiver(PeppolParticipantId.valueFor(rs.getString("receiver")));
        messageMetaData.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(rs.getString("channel")));
        String message_uuid = rs.getString("message_uuid");
        if (message_uuid != null) {
            messageMetaData.setUuid(message_uuid);
        }
        messageMetaData.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.valueFor(rs.getString("document_id")));
        messageMetaData.getPeppolHeader().setProfileId(new ProfileId(rs.getString("process_id")));
        return messageMetaData;
    }

    private MessageMetaDataImpl extractMessageForResultSetWithoutAccountId(ResultSet rs) throws SQLException {
        MessageMetaDataImpl m = new MessageMetaDataImpl();
        m.setMsgNo(rs.getInt("msg_no"));
        m.setInvoiceNo(rs.getString("invoice_no"));
        m.setTransferDirection(TransferDirection.valueOf(rs.getString("direction")));
        m.setReceived(rs.getTimestamp("received"));
        m.setDelivered(rs.getTimestamp("delivered"));
        m.getPeppolHeader().setSender(PeppolParticipantId.valueFor(rs.getString("sender")));
        m.getPeppolHeader().setReceiver(PeppolParticipantId.valueFor(rs.getString("receiver")));
        m.getPeppolHeader().setPeppolChannelId(new PeppolChannelId(rs.getString("channel")));
        m.getPeppolHeader().setPeppolDocumentTypeId(PeppolDocumentTypeId.valueFor(rs.getString("document_id")));
        m.getPeppolHeader().setProfileId(new ProfileId(rs.getString("process_id")));
        // UUIDs are heavy lifting, check for null values first.
        String uuidString = rs.getString("message_uuid");
        if (!rs.wasNull()) {
            m.setUuid(uuidString);
        }
        return m;
    }

    private RingoAccountStatistics extractRingoAccountStatistics(ResultSet rs) throws SQLException {
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

    private static class SqlHelper {

        private String sql;

        public static SqlHelper create() {
            return new SqlHelper();
        }

        public SqlHelper inboxCount() {
            sql = "select count(*) from message where account_id=? and direction= ? and delivered is null and message_uuid is not null and message_uuid != ''";
            return this;
        }

        private SqlHelper undeliveredMessagesSql(TransferDirection transferDirection) {
            if (TransferDirection.IN.equals(transferDirection)) {
                // Delivered must be null and uuid must not be null for valid undelivered incoming messages
                sql = selectMessage() +
                        "where delivered is null and message_uuid is not null and message_uuid != '' and account_id=? and direction=? limit " + PeppolMessageRepository.DEFAULT_PAGE_SIZE;
            } else {
                // Delivered must be null and uuid must be null for valid undelivered outgoing messages
                sql = selectMessage() +
                        "where delivered is null " +
                        "and message_uuid is null and account_id=? and direction=?" +
                        "and not exists(select 1 from outbound_message_queue omq where omq.msg_no = message.msg_no and omq.state='AOD')" +
                        " limit " + PeppolMessageRepository.DEFAULT_PAGE_SIZE;
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
            return connection.prepareStatement(sql);
        }

        public SqlHelper findMessages(MessageSearchParams searchParams) {
            sql = selectMessage() + "where account_id=? ";
            generateWhereClause(searchParams);
            generateLimitCondition(searchParams.getPageIndex());
            return this;
        }

        private String selectMessage() {
            return "select account_id, msg_no, direction, received, delivered, sender, receiver, channel, document_id, process_id, message_uuid, invoice_no from message ";
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
                sql = sql.concat(" and Date(received) " + searchParams.getDateCondition().getValue() + "'" + searchParams.getSent() + "'");
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
            limit = String.format(" limit %d, %d", offset, PeppolMessageRepository.DEFAULT_PAGE_SIZE);
            sql = sql.concat(limit);
            return sql;
        }

        public SqlHelper messagesCount(MessageSearchParams searchParams) {
            sql = "select count(*) from message where account_id=?";
            generateWhereClause(searchParams);
            return this;
        }

    }

}


/* Created by steinar on 08.01.12 at 21:46 */
package no.sr.ringo.common;

import com.google.inject.Inject;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import no.sr.ringo.account.AccountId;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.account.Customer;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.billing.BillingCycle;
import no.sr.ringo.billing.BillingPeriodId;
import no.sr.ringo.billing.BillingSchemeId;
import no.sr.ringo.cenbiimeta.ProfileId;
import no.sr.ringo.guice.TestModuleFactory;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Repository;
import no.sr.ringo.message.OutboundMessageQueueState;
import no.sr.ringo.message.TransferDirection;
import no.sr.ringo.peppol.*;
import no.sr.ringo.queue.OutboundMessageQueueErrorId;
import no.sr.ringo.queue.OutboundMessageQueueId;
import no.sr.ringo.queue.QueuedOutboundMessageError;
import org.testng.annotations.Guice;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class providing helper methods to create messages and accounts
 * It can be either extended by other integration test or used in http tests
 *
 * @author Adam Mscisz adam@sendregning.no
 */
@Guice(moduleFactory = TestModuleFactory.class)
@Repository
public class DatabaseHelper {

    private final AccountRepository accountRepository;
    private final JdbcTxManager jdbcTxManager;

    @Inject
    public DatabaseHelper(AccountRepository accountRepository, JdbcTxManager jdbcTxManager) {
        this.accountRepository = accountRepository;
        this.jdbcTxManager = jdbcTxManager;
    }


    public int createMessage(PeppolDocumentTypeId documentId, String message, Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final String uuid, Date delivered, Date received) {
        Connection con = null;
        String sql = "insert into message (account_id, direction,received, delivered, sender, receiver, channel, message_uuid, document_id, process_id, xml_message) values(?,?,?,?,?,?,?,?,?,?,?)";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (accountId != null) {
                ps.setInt(1, accountId);
            } else {
                ps.setObject(1, null);
            }
            ps.setString(2, direction.name());
            if (received == null) {
                ps.setNull(3, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(3, new Timestamp(received.getTime()));
            }
            if (delivered == null) {
                ps.setNull(4, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, new Timestamp(delivered.getTime()));
            }

            ps.setString(5, senderValue);
            ps.setString(6, receiverValue);
            ps.setString(7, "CH-TEST");
            if (uuid == null) {
                ps.setNull(8, Types.VARCHAR);
            } else {
                ps.setString(8, uuid);
            }
            ps.setString(9, documentId.stringValue());
            ps.setString(10, PeppolProcessTypeIdAcronym.INVOICE_ONLY.getPeppolProcessTypeId().toString());
            ps.setString(11, message);

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

    /**
     * Helper method creating simple message
     */
    public int createMessage(Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final String uuid, Date delivered) {
        PeppolDocumentTypeId invoiceDocumentType = new PeppolDocumentTypeId(
                RootNameSpace.INVOICE,
                LocalName.Invoice,
                CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T010_INVOICE_V1 + ":#" + ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY + "#" + ProfileId.Predefined.EHF_INVOICE),
                "2.0");
        return createMessage(invoiceDocumentType, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, uuid, delivered, new Date());
    }

    public int createMessage(Integer accountId, TransferDirection direction, String senderValue, String receiverValue, final String uuid, Date delivered, Date received) {
        PeppolDocumentTypeId invoiceDocumentType = new PeppolDocumentTypeId(
                RootNameSpace.INVOICE,
                LocalName.Invoice,
                CustomizationIdentifier.valueOf(TransactionIdentifier.Predefined.T010_INVOICE_V1 + ":#" + ProfileId.Predefined.PEPPOL_4A_INVOICE_ONLY + "#" + ProfileId.Predefined.EHF_INVOICE),
                "2.0");
        return createMessage(invoiceDocumentType, "<test>\u00E5</test>", accountId, direction, senderValue, receiverValue, uuid, delivered, received);
    }


    /**
     * Helper method to delete rows in message table
     *
     * @param msgNo
     */

    public void deleteMessage(Integer msgNo) {

        if (msgNo == null) {
            return;
        }

        Connection con = null;
        String sql = "delete from message where msg_no = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, msgNo);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    /**
     * Helper method updating received date on message
     *
     * @param date
     * @param msgNo
     */
    public void updateMessageDate(Date date, Integer msgNo) {
        Connection con = null;
        String sql = "update message set received = ? where msg_no = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(date.getTime()));
            ps.setInt(2, msgNo);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }


    public void deleteAllMessagesForAccount(RingoAccount account) {
        if (account == null || account.getId() == null) {
            return;
        }

        Connection con = null;
        String sql = "delete from message where account_id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, account.getId().toInteger());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void deleteAllMessagesWithoutAccountId() {
        Connection con = null;
        String sql = "delete from message where account_id is null";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void updateMessageReceiver(int msgNo, String receiver) {

        Connection con = null;
        String sql = "update message set receiver = ? where msg_no = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, receiver);
            ps.setInt(2, msgNo);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }

    }

    public int addAccountReceiver(AccountId id, String receiver) {

        Connection con = null;
        String sql = "insert into account_receiver (account_id, participant_id) values(?,?)";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, id.toInteger());
            ps.setString(2, receiver);

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int accountReceiverId = rs.getInt(1);

                return accountReceiverId;
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void deleteAccountReceiver(Integer accountReceiverId) {
        if (accountReceiverId == null) {
            return;
        }

        Connection con = null;
        String sql = "delete from account_receiver where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, accountReceiverId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void deleteCustomer(Customer customer) {
        if (customer == null) {
            return;
        }
        Connection con = null;
        String sql = "delete from customer where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, customer.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }


    public BillingSchemeId createBillingScheme(String code, BigDecimal sendPrice, BigDecimal receivePrice, BigDecimal cyclePrice, BigDecimal startPrice, BillingCycle billingCycle) {
        Connection con = null;
        String sql = "insert into billing_scheme (code, price_invoice_send, price_invoice_receive, price_billing_cycle, start_price, billing_cycle) values(?,?,?,?,?,?)";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, code);
            ps.setBigDecimal(2, sendPrice);
            ps.setBigDecimal(3, receivePrice);
            ps.setBigDecimal(4, cyclePrice);
            ps.setBigDecimal(5, startPrice);
            ps.setString(6, billingCycle.name());

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int schemeId = rs.getInt(1);

                return new BillingSchemeId(schemeId);
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void deleteBillingScheme(BillingSchemeId schemeId) {
        if (schemeId == null) {
            return;
        }

        Connection con = null;
        String sql = "delete from billing_scheme where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, schemeId.toInteger());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void deleteBillingPeriod(BillingPeriodId billingPeriodId) {
        if (billingPeriodId == null) {
            return;
        }

        Connection con = null;
        String sql = "delete from billing_period where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, billingPeriodId.toInteger());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    /**
     * Deletes all data related to an account.
     *
     * @param userName - it's both account.username and customer.name
     */
    public void deleteAccountData(String userName) {
        Connection con = null;
        String sql = "delete from billing_period where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement("delete from account_role where username = ?");
            ps.setString(1, userName);
            ps.executeUpdate();

            ps = con.prepareStatement("delete from account_receiver where account_id = (select id from account where username = ?)");
            ps.setString(1, userName);
            ps.executeUpdate();

            ps = con.prepareStatement("delete from account where username = ?");
            ps.setString(1, userName);
            ps.executeUpdate();

            ps = con.prepareStatement("delete from billing_period where customer_id = (select id from customer where name = ?)");
            ps.setString(1, userName);
            ps.executeUpdate();

            ps = con.prepareStatement("delete from customer where name = ?");
            ps.setString(1, userName);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    /**
     * @return true if account has client role in account_role table
     */
    public boolean hasClientRole(String userName) {

        Connection con = null;
        String sql = "select count(*) from account_role where username like ? and role_name ='client'";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("%s failed with username: %s", sql, userName), e);
        }
    }

    public boolean accountReceiverExists(AccountId id, String orgNo) {

        Connection con = null;
        String sql = "select count(*) from account_receiver where account_id = ? and participant_id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id.toInteger());
            ps.setString(2, "9908:".concat(orgNo));


            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("%s failed with orgNo: %s", sql, orgNo), e);
        }
    }

    public boolean defauktBillingPeriodExists(Integer id) {
        Integer default_billing_scheme_id = 1;

        Connection con = null;
        String sql = "select count(*) from billing_period where customer_id = ? and billing_scheme_id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, default_billing_scheme_id);


            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("%s failed with customer_id: %s", sql, id), e);
        }
    }

    public JdbcTxManager getJdbcTxManager() {
        return jdbcTxManager;
    }

    public QueuedMessage getQueuedMessageByQueueId(OutboundMessageQueueId queueId) {
        Connection con = null;
        String sql = "select * from outbound_message_queue where id = ?";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, queueId.toInt());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new QueuedMessage(rs.getInt("id"), rs.getInt("msg_no"), OutboundMessageQueueState.valueOf(rs.getString("state")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("error fetching fault messages", e);
        }
        return null;
    }

    public QueuedMessage getQueuedMessageByMsgNo(Integer msgNo) {
        Connection con = null;
        String sql = "select * from outbound_message_queue where msg_no = ?";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, msgNo);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new QueuedMessage(rs.getInt("id"), rs.getInt("msg_no"), OutboundMessageQueueState.valueOf(rs.getString("state")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("error fetching fault messages", e);
        }
        return null;
    }

    public Integer putMessageOnQueue(int msgId) {
        Connection con = null;
        String sql = "insert into outbound_message_queue (msg_no, state) values (?,?)";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, msgId);
            ps.setString(2, OutboundMessageQueueState.QUEUED.name());

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public List<QueuedOutboundMessageError> getErrorMessages() {
        List<QueuedOutboundMessageError> result = new ArrayList<QueuedOutboundMessageError>();

        Connection con = null;
        String sql = "select * from outbound_message_queue_error";

        try {
            con = jdbcTxManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new QueuedOutboundMessageError(new OutboundMessageQueueErrorId(rs.getInt("id")), new OutboundMessageQueueId(rs.getInt("queue_id")), null,  rs.getString("message"), rs.getString("details"), rs.getString("stacktrace"), rs.getTimestamp("create_dt"), "1"));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("error fetching fault messages", e);
        }
    }

    public void updateValidateFlagOnAccount(AccountId accountId, boolean validateUpdate) {
        Connection con = null;
        String sql = "update account set validate_upload= ? where id = ?";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setBoolean(1, validateUpdate);
            ps.setInt(2, accountId.toInteger());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public void removeExistingErrorMessages() {
        Connection con = null;
        String sql = "delete from outbound_message_queue_error";

        try {
            con = jdbcTxManager.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        }
    }

    public class QueuedMessage {
        private final Integer queueId;
        private final Integer msgNo;
        private final OutboundMessageQueueState status;

        public QueuedMessage(Integer queueId, Integer msgNo, OutboundMessageQueueState status) {
            this.queueId = queueId;
            this.msgNo = msgNo;
            this.status = status;
        }

        public Integer getMsgNo() {
            return msgNo;
        }

        public Integer getQueueId() {
            return queueId;
        }

        public OutboundMessageQueueState getState() {
            return status;
        }
    }

    public class FaultMessageRow {
        private final int messageNo;
        private final String message;
        private final Date ts;

        public FaultMessageRow(int messageNo, String message, Date ts) {
            this.messageNo = messageNo;
            this.message = message;
            this.ts = ts;
        }

        public int getMessageNo() {
            return messageNo;
        }

        public String getMessage() {
            return message;
        }

        public Date getTs() {
            return ts;
        }
    }
}

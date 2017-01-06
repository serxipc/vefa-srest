package no.sr.ringo.message;

import eu.peppol.identifier.MessageId;
import eu.peppol.persistence.AccountId;
import eu.peppol.persistence.MessageNumber;
import eu.peppol.persistence.api.account.Account;
import no.sr.ringo.message.statistics.RingoStatistics;

import java.util.Date;
import java.util.List;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public interface PeppolMessageRepository {

    public static final Integer DEFAULT_PAGE_SIZE = 25;

    /**
     * Persists the supplied PeppolMessage, updating especially the message number.
     *
     *
     *
     * @param ringoAccountId
     * @param peppolMessage
     * @return the surrogate primary key of the persisted message.
     */
    MessageWithLocations persistOutboundMessage(Account ringoAccountId, PeppolMessage peppolMessage);

    /**
     * Retrieves message by primary key
     * @param msgNo - primary key in message table
     * @return
     * @throws PeppolMessageNotFoundException
     */
    MessageMetaData findMessageByMessageNo(MessageNumber msgNo) throws PeppolMessageNotFoundException;

    /**
     * Retrieves message by primary key
     * @param account -  account_id in message
     * @param messageNo - primary key in message table
     * @return
     * @throws PeppolMessageNotFoundException
     */
    MessageMetaData findMessageByMessageNo(Account account, Long messageNo) throws PeppolMessageNotFoundException;

    /**
     * Gives the count of messages in the inbox for the supplied account
     *
     * @param accountId identifies the account for which we are counting
     * @return the count.
     */
    Integer getInboxCount(AccountId accountId);

    /**
     *
     * Retrieves messages from outbox where delivered is null
     *
     * @param accountId
     *
     */
    List<MessageMetaData> findUndeliveredOutboundMessagesByAccount(AccountId accountId);

    /**
     *
     * Retrieves messages from inbox where delivered is null
     *
     * @param accountId
     *
     */
    List<MessageMetaData> findUndeliveredInboundMessagesByAccount(AccountId accountId);

    /**
     * Searches all messages within inbox and outbox for given account that meet criteria specified in searchParams
     * @param id
     * @param searchParams
     * @return
     */
    List<MessageMetaData> findMessages(AccountId id, MessageSearchParams searchParams);

    /**
     * Gets the count of all messages which match the search params provided
     * @param accountId
     * @param searchParams
     * @return
     */
    Integer getMessagesCount(AccountId accountId, MessageSearchParams searchParams);

    /**
     * Gives the count of all inbound and outbound messages
     *
     * @param accountId identifies the account for which we are counting
     * @return the count.
     */
    Integer getMessagesCount(AccountId accountId);

    /**
     * Marks message as read which means it simply updates deliveder date to current timestamp
     * @param messageNo
     */
    void markMessageAsRead(Long messageNo);


    /**
     * Finds messages that dont' have account_id
     * @return
     */
    List<MessageMetaData> findMessagesWithoutAccountId();


    /***************************************************
     *** Methods related to sending queued documents ***
     ***************************************************/

    /**
     * Updates delivered, uuid fields on outbound message. Furthermore the AS2 MDN (native evidence) and
     * REM evidence is persisted.
     *
     */
    public void updateOutBoundMessageDeliveryDateAndUuid(MessageNumber msgNo, String remoteAP, MessageId messageId, Date delivered, byte[] nativeEvidenceBytes, byte[] remEvidenceBytes);

    /**
     * Creates inbound message as copy of outbound one with delivered being null
     *
     *
     * @param outMsgNo id of outbound message to copy
     * @param uuid
     * @return
     */
    public Long copyOutboundMessageToInbound(Long outMsgNo, String uuid);

    /**
     * Retrieves xml document from given message not checking the account_id
     * @param messageNo
     * @return
     * @throws PeppolMessageNotFoundException
     */
    public String findDocumentByMessageNoWithoutAccountCheck(Long messageNo) throws PeppolMessageNotFoundException;


    /**
     * Returns true if both receiver and sender are associated with the same account_id (account_receiver table)
     * @param messageNo
     * @return
     */
    public boolean isSenderAndReceiverAccountTheSame(Long messageNo);

    /**
     * Fetches the statistics for the provided account
     * @return
     */
    RingoStatistics getAccountStatistics(AccountId accountId);

    /**
     * Fetches the statistics for all accounts
     * @return
     */
    RingoStatistics getAdminStatistics();


}

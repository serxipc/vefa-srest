package no.sr.ringo.account;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.message.MessageNumber;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 31.12.11
 *         Time: 16:54
 */
public interface AccountRepository {

    public RingoAccount findAccountById(final AccountId id);

    RingoAccount findAccountByParticipantId(final ParticipantId participantId);

    RingoAccount findAccountByUsername(final UserName username);

    /**
     * Creates an account with the provided details. if the account already
     * exists it will be retrieved from the database based on the participant id
     *
     * Default client role will be added
     *
     * If participantId is not null, account_receiver entry will also be created
     * @param ringoAccount
     * @param participantId if not null will be used in account_receiver
     * @return
     */
    RingoAccount createAccount(final RingoAccount ringoAccount,final ParticipantId participantId);

    /**
     * Persists new customer
     * @return
     */
    Customer createCustomer(final String name, final String email, final String phone, final String country, final String contactPerson, final String address1, final String address2, final String zip, final String city, final String orgNo);

    /**
     * Deletes the account with the given id
     * @param accountId
     */
    void deleteAccount(AccountId accountId);

    boolean accountExists(final UserName username);

    Customer findCustomerById(final Integer id);

    void updatePasswordOnAccount(final AccountId id, final String hash);

    RingoAccount findAccountAsOwnerOfMessage(MessageNumber messageNumber);
}

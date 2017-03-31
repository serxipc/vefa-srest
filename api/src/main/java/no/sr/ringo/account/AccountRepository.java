/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or, as soon they
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

package no.sr.ringo.account;

import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.sr.ringo.message.MessageNumber;

/**
 * @author Steinar Overbeck Cook
 */
public interface AccountRepository {

    public Account findAccountById(final AccountId id) throws SrAccountNotFoundException;

    Account findAccountByParticipantIdentifier(final ParticipantIdentifier participantId);

    /**
     * Locates an account, which you expect to exist by the username.
     *
     * @param username the search key
     * @return a reference to the account if found, null otherwise
     * @throws SrAccountNotFoundException if the account could not be found
     * @see #accountExists(UserName) to figure out whether an account exists or not.
     */
    Account findAccountByUsername(final UserName username) throws SrAccountNotFoundException;

    /**
     * Creates an account with the provided details. if the account already
     * exists it will be retrieved from the database based on the participant id
     * <p>
     * Default client role will be added
     * <p>
     * If participantId is not null, account_receiver entry will also be created
     *
     * @param account       holds the data for the account to be crated
     * @param participantId if not null will be used in account_receiver
     * @return instance of {@link Account} created
     */
    Account createAccount(final Account account, final ParticipantIdentifier participantId);

    /**
     * Persists new customer
     *
     * @param name          of customer
     * @param email         of customer
     * @param phone         number to contact customer
     * @param country       of customer
     * @param contactPerson of customer
     * @param address1      of customer
     * @param address2      of customer
     * @param zip           of customer
     * @param city          of customer
     *                      @param orgNo organisation number of customer with ISO6523 ICD prefix
     * @return instance of {@link Customer} created
     */
    Customer createCustomer(final String name, final String email, final String phone, final String country, final String contactPerson, final String address1, final String address2, final String zip, final String city, final String orgNo);

    /**
     * Deletes the account with the given id
     *
     * @param accountId the unique key of the account to be deleted.
     */
    void deleteAccount(AccountId accountId);

    /**
     * Inspects the repository to see if an account identified by username exists or not.
     *
     * @param username the key for the search
     * @return true if account exists, false otherwise.
     * @see #findAccountByUsername(UserName)
     */
    boolean accountExists(final UserName username);

    Customer findCustomerById(final Integer id);

    void updatePasswordOnAccount(final AccountId id, final String hash);

    Account findAccountAsOwnerOfMessage(MessageNumber messageNumber);
}

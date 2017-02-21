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

import eu.peppol.identifier.PeppolDocumentTypeIdAcronym;
import eu.peppol.identifier.PeppolProcessTypeIdAcronym;
import eu.peppol.identifier.WellKnownParticipant;
import no.sr.ringo.account.*;
import no.sr.ringo.message.MessageRepository;
import no.sr.ringo.message.ReceptionId;
import no.sr.ringo.message.TransmissionMetaData;
import no.sr.ringo.persistence.guice.PersistenceTestModuleFactory;
import no.sr.ringo.transport.TransferDirection;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * @author steinar
 *         Date: 11.11.2016
 *         Time: 10.21
 */
@Guice(moduleFactory = PersistenceTestModuleFactory.class)

public class DatabaseHelperTest {

    @Inject
    DatabaseHelper databaseHelper;

    @Inject
    AccountRepository accountRepository;

    @Inject
    MessageRepository messageRepository;

    private Account account;

    @BeforeMethod
    public void setUp() {
        account = new Account(new CustomerId(1), "SteinarAccount",
                new UserName("steinar"), new Date(), "ringo1", new AccountId(2), false, false
        );

        account = accountRepository.createAccount(this.account, WellKnownParticipant.DIFI_TEST);
        assertNotEquals(account.getAccountId().toInteger(), Integer.valueOf(1));

    }

    /**
     * Ensures that we a) can insert a messag and b) the message is associated with the account we specify, i.e. there is no attempt made
     * to connect it to another account.
     *
     * @throws Exception
     */
    @Test
    public void testCreateMessageVerifyAccount() throws Exception {
        // Creates an outbound message, which should be associated with account no #2
        // even though the receivers ppid is bound to account #1
        Long msgNo = databaseHelper.createSampleMessage(account.getAccountId().toInteger(), TransferDirection.OUT,
                WellKnownParticipant.DUMMY.getIdentifier(),
                WellKnownParticipant.DUMMY.getIdentifier(),
                new ReceptionId(), new Date(),
                PeppolDocumentTypeIdAcronym.EHF_INVOICE.toVefa(),
                PeppolProcessTypeIdAcronym.INVOICE_ONLY.toVefa());

        TransmissionMetaData messageByNo = messageRepository.findByMessageNo(msgNo);
        assertEquals(messageByNo.getAccountId(), account.getAccountId());
    }

    @Test
    public void testCreateMessage1() throws Exception {

    }

    @Test
    public void testCreateMessage2() throws Exception {

    }

    @Test
    public void testDeleteMessage() throws Exception {

    }

}
package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.*;
import no.sr.ringo.guice.ServerTestModuleFactory;
import no.sr.ringo.message.MessageNumber;
import no.sr.ringo.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.transport.TransferDirection;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 31.12.11
 *         Time: 17:24
 */
@Guice(moduleFactory = ServerTestModuleFactory.class)
public class AccountRepositoryImplTest {


    private AccountRepository accountRepository;
    private final DbmsTestHelper dbmsTestHelper;
    private Account account;
    private ParticipantId participantId;
    private DatabaseHelper databaseHelper;
    private Customer customer;
    Account adamsAccount;

    @Inject
    public AccountRepositoryImplTest(DatabaseHelper databaseHelper, AccountRepository accountRepository, DbmsTestHelper dbmsTestHelper) {
        this.databaseHelper = databaseHelper;
        this.accountRepository = accountRepository;
        this.dbmsTestHelper = dbmsTestHelper;
    }

    @BeforeMethod(groups = {"persistence"})
    public void setUp() throws Exception {
        participantId = ObjectMother.getAdamsParticipantId();
        adamsAccount = ObjectMother.getAdamsAccount();
        adamsAccount = accountRepository.createAccount(adamsAccount, participantId);
        account = accountRepository.createAccount(account, participantId);

    }

    @AfterMethod(groups = {"persistence"})
    public void tearDown() throws Exception {
        databaseHelper.deleteAllMessagesForAccount(account);
        accountRepository.deleteAccount(account.getAccountId());
        databaseHelper.deleteCustomer(customer);

        databaseHelper.deleteAllMessagesForAccount(adamsAccount);
        accountRepository.deleteAccount(adamsAccount.getAccountId());
    }

    @Test(groups = {"persistence"})
    public void testFindAccountById() throws Exception {

        Account accountById = accountRepository.findAccountById(account.getAccountId());

        assertNotNull(accountById.getAccountId());
        assertNotNull(accountById.getCustomerId());
    }

    @Test(groups = {"persistence"})
    public void testFindAccountByUsername() throws Exception {

        Account accountByUsername = accountRepository.findAccountByUsername(account.getUserName());

        assertNotNull(accountByUsername.getAccountId());
        assertNotNull(accountByUsername.getCustomerId());
        assertNotNull(accountByUsername.getUserName());
    }


    @Test(groups = {"persistence"})
    public void testFindAccountByParticipantId() throws Exception {
        Account accountByParticipantId = accountRepository.findAccountByParticipantId(participantId);
        assertNotNull(accountByParticipantId);
        assertNotNull(accountByParticipantId.getAccountId());
        assertNotNull(accountByParticipantId.getCustomerId());
        assertNotNull(accountByParticipantId.getUserName());
    }

    @Test(groups = {"persistence"})
    public void testAccountExists() throws Exception {
        assertTrue(accountRepository.accountExists(new UserName("sr")));
        assertTrue(accountRepository.accountExists(new UserName("SR")));
        assertFalse(accountRepository.accountExists(new UserName("notExistingAccount")));
    }

    @Test(groups = {"persistence"})
    public void testCreateCustomer() {
                customer = accountRepository.createCustomer("adam", "adam@sendregning.no", "666", "Norge", "Andy S", "Adam vei", "222", "0976", "Oslo", "976098897");
                assertNotNull(customer.getCustomerId());
                assertNotNull(customer.getCreated());
                assertEquals("adam", customer.getName());
                assertEquals("adam@sendregning.no", customer.getEmail());
                assertEquals("666", customer.getPhone());
                assertEquals("Norge", customer.getCountry());
                assertEquals("Adam vei", customer.getAddress1());
                assertEquals("222", customer.getAddress2());
                assertEquals("0976", customer.getZip());
                assertEquals("Oslo", customer.getCity());
                assertEquals("976098897", customer.getOrgNo());


    }

    @Test(groups = {"persistence"})
    public void testUpdatePasswordOnAccount() throws SrAccountNotFoundException {
        AccountId id = new AccountId(1);
        String currentPass = accountRepository.findAccountById(id).getPassword();
        String pass = "testPassword";

        accountRepository.updatePasswordOnAccount(id, pass);
        assertEquals(pass, accountRepository.findAccountById(id).getPassword());

        accountRepository.updatePasswordOnAccount(id, currentPass);
        assertEquals(currentPass, accountRepository.findAccountById(id).getPassword());

    }

    @Test(groups = {"persistence"})
    public void testValidateFlag() throws SrAccountNotFoundException {
        assertFalse(adamsAccount.isValidateUpload());

        databaseHelper.updateValidateFlagOnAccount(adamsAccount.getAccountId(), true);

        adamsAccount = accountRepository.findAccountById(adamsAccount.getAccountId());
        assertTrue(adamsAccount.isValidateUpload());

    }

    @Test(groups = {"persistence"})
    public void findMessageOwner(){
        Long messageNumber = dbmsTestHelper.createMessage(adamsAccount.getAccountId().toInteger(), TransferDirection.IN, participantId.stringValue(), participantId.stringValue(), UUID.randomUUID().toString(), null);
        assertEquals(adamsAccount, accountRepository.findAccountAsOwnerOfMessage(MessageNumber.create(messageNumber)));

    }


}

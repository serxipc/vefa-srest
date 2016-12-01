/* Created by steinar on 01.01.12 at 18:16 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import eu.peppol.persistence.api.SrAccountNotFoundException;
import eu.peppol.persistence.api.UserName;
import eu.peppol.persistence.api.account.Account;
import eu.peppol.persistence.api.account.AccountRepository;
import eu.peppol.persistence.api.account.Customer;
import eu.peppol.persistence.jdbc.util.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Guice(moduleFactory = TestModuleFactory.class)
public class RegisterUseCaseIntegrationTest {

    private final RegisterUseCase registerUseCase;
    private final AccountRepository accountRepository;
    private final DatabaseHelper databaseHelper;

    private final String ORG_NUM_STR = "NO810418052";
    private final String userName = "UserName";

    @Inject
    public RegisterUseCaseIntegrationTest(RegisterUseCase registerUseCase, AccountRepository accountRepository, DatabaseHelper databaseHelper) {
        this.registerUseCase = registerUseCase;
        this.accountRepository = accountRepository;
        this.databaseHelper = databaseHelper;
    }

    /**
     * Make sure we always remove the created user after running these tests
     */
    @AfterMethod
    public void removeTestUser() {
        databaseHelper.deleteAccountData(new UserName(userName));
    }

    /**
     * Test will create the account/customer but creating billing period will throw an exception, so account should be rolled back
     */
    @Test(groups = {"persistence"})
    public void testTransactionRollback() {

        boolean exists = accountRepository.accountExists(new UserName(userName));
        assertFalse(exists, "The username was already found in the database, test cannot be started");

        RegistrationData rd = new RegistrationData(userName, "pass", "username", "add1", "add2", "ZipTooLong", "city", "country", "contactPerson", "email", "phone", ORG_NUM_STR, true);

        try {
            registerUseCase.registerUser(rd);
            fail("User seems to have been registered, that should not have happened");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("too long for column"));
        }

        boolean shouldStillBeFalse = accountRepository.accountExists(new UserName(userName));
        assertFalse(shouldStillBeFalse, "The test was not supposed to create an account with the given username, it was supposed to fail an rollback");

    }

    /**
     * Tests that all data is there, account, customer, account_receiver and account_role
     */
    @Test(groups = {"persistence"})
    public void testSuccessfulRegistration() throws SrAccountNotFoundException {

        String password = "pass";
        String add1 = "add1";
        String add2 = "add2";
        String zip = "zip";
        String city = "city";
        String country = "country";
        String contactPerson = "contactPerson";
        String email = "email";
        String phone = "phone";


        removeAccountIfExists();

        RegistrationData rd = new RegistrationData(userName, password, userName, add1, add2, zip, city, country, contactPerson, email, phone, ORG_NUM_STR, true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);
        assertTrue(result.isSuccess(), result.getMessage());

        // check that account exists
        Account account = accountRepository.findAccountByUsername(new UserName(userName));
        assertNotNull(account);

        //check account_role 'client' has been created
        assertTrue(databaseHelper.hasClientRole(new UserName(userName)));

        //check there's account receiver
        assertTrue(databaseHelper.accountReceiverExists(account.getAccountId(), ORG_NUM_STR.substring(2)));

        //check customer
        Customer customer = accountRepository.findCustomerById(account.getCustomerId().toInteger());
        assertNotNull(customer);
        assertEquals(ORG_NUM_STR, customer.getOrgNo());
        assertEquals(add1, customer.getAddress1());
        assertEquals(add2, customer.getAddress2());
        assertEquals(email, customer.getEmail());
        assertEquals(zip, customer.getZip());
        assertEquals(city, customer.getCity());
        assertEquals(country, customer.getCountry());
        assertEquals(contactPerson, customer.getContactPerson());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhone());

    }

    private void removeAccountIfExists() throws SrAccountNotFoundException {
        UserName username = new UserName(userName);
        if (accountRepository.accountExists(username)) {
            Account accountByUsername = accountRepository.findAccountByUsername(username);
            accountRepository.deleteAccount(accountByUsername.getAccountId());
        }
    }

}



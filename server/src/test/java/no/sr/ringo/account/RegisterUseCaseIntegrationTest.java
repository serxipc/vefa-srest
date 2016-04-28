/* Created by steinar on 01.01.12 at 18:16 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@Guice(moduleFactory = TestModuleFactory.class)
public class RegisterUseCaseIntegrationTest {

    final RegisterUseCase registerUseCase;
    final AccountRepository accountRepository;
    final DatabaseHelper databaseHelper;

    private final String ORG_NUM_STR = "222222222";

    @Inject
    public RegisterUseCaseIntegrationTest(RegisterUseCase registerUseCase, AccountRepository accountRepository, DatabaseHelper databaseHelper) {
        this.registerUseCase = registerUseCase;
        this.accountRepository = accountRepository;
        this.databaseHelper = databaseHelper;
    }

    /**
     * Test will create the account/customer but creating billing period will throw an exception, so account should be rolled back
     */
    @Test(groups = {"persistence"})
    public void testTransactionRollback() {

        String transactionName = "TransactionName";

        //test there's no account created for name "name"
        boolean exists = accountRepository.accountExists(new UserName(transactionName));
        assertFalse(exists);

        RegistrationData rd = new RegistrationData(transactionName, "pass", "username", "add1", "add2", "zip", "city", "country", "contactPerson", "email", "phone", ORG_NUM_STR, true);

        try {
            registerUseCase.registerUser(rd);
        } catch (IllegalStateException e) {
            assertEquals("Simulating exception",e.getMessage());
        }

        //test there's no account created for name "name"
        boolean stillExists = accountRepository.accountExists(new UserName(transactionName));
        assertFalse(stillExists);

    }

    /**
     * Tests that all data is there, account, customer, account_receiver and account_role
     */
    @Test(groups = {"persistence"})
    public void testSuccessfulRegistration() {

        String userName = "testUserName";

        try {

            String password = "pass";
            String add1 = "add1";
            String add2 = "add2";
            String zip = "zip";
            String city = "city";
            String country = "country";
            String contactPerson = "contactPerson";
            String email = "email";
            String phone = "phone";

            RegistrationData rd = new RegistrationData(userName, password, userName, add1, add2, zip, city, country, contactPerson, email, phone, ORG_NUM_STR, true);
            RegistrationProcessResult result = registerUseCase.registerUser(rd);
            assertTrue(result.isSuccess(), result.getMessage());

            //check that account exists
            RingoAccount account = accountRepository.findAccountByUsername(new UserName(userName));
            assertNotNull(account);

            //check account_role 'client' has been created
            assertTrue(databaseHelper.hasClientRole(userName));

            //check there's account receiver
            assertTrue(databaseHelper.accountReceiverExists(account.getId(), ORG_NUM_STR));

            //check customer
            Customer customer = accountRepository.findCustomerById(account.getCustomer().getId());
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

        } finally {
            databaseHelper.deleteAccountData(userName);
        }

    }

}



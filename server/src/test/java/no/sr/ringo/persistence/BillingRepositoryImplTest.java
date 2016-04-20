package no.sr.ringo.persistence;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.ObjectMother;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.account.Customer;
import no.sr.ringo.account.CustomerId;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.billing.*;
import no.sr.ringo.common.DatabaseHelper;
import no.sr.ringo.guice.TestModuleFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Guice(moduleFactory = TestModuleFactory.class)
public class BillingRepositoryImplTest {


    private AccountRepository accountRepository;
    private BillingRepository billingRepository;
    private RingoAccount ringoAccount;
    private ParticipantId participantId;
    private DatabaseHelper databaseHelper;

    @Inject
    public BillingRepositoryImplTest(DatabaseHelper databaseHelper, AccountRepository accountRepository, BillingRepository billingRepository) {
        this.databaseHelper = databaseHelper;
        this.accountRepository = accountRepository;
        this.billingRepository = billingRepository;
    }

    @Test(groups = {"persistence"})
    public void testGetBillingSchemeByCode() throws Exception {
        BigDecimal sendPrice = new BigDecimal("10.10");
        BigDecimal receivePrice = new BigDecimal("20.10");
        BigDecimal cyclePrice = new BigDecimal("30.10");
        BigDecimal startPrice = new BigDecimal("40.10");
        BillingSchemeId billingSchemeId = databaseHelper.createBillingScheme("testScheme", sendPrice, receivePrice, cyclePrice, startPrice, BillingCycle.MONTHLY);
        try {
            BillingScheme scheme = billingRepository.getBillingSchemeByCode("testScheme");

            assertEquals(BillingCycle.MONTHLY, scheme.getBillingCycle());
            assertEquals("testScheme", scheme.getCode());
            assertEquals(0, cyclePrice.compareTo(scheme.getPriceBillingCycle()));
            assertEquals(0, startPrice.compareTo(scheme.getStartPrice()));
            assertEquals(0, receivePrice.compareTo(scheme.getPriceInvoiceReceive()));
            assertEquals(0, sendPrice.compareTo(scheme.getPriceInvoiceSend()));

        } finally {
            databaseHelper.deleteBillingScheme(billingSchemeId);

        }
    }

    @Test(groups = {"persistence"})
    public void testGetDefaultBillingScheme() throws Exception {
        BillingScheme defaultByName = billingRepository.getBillingSchemeByCode("DEFAULT");
        BillingScheme defaultScheme = billingRepository.getDefaultBillingScheme();

        assertEquals(defaultByName, defaultScheme);

        assertEquals(defaultScheme.getBillingCycle(), BillingCycle.YEARLY);
        assertEquals(defaultScheme.getCode(), "DEFAULT");
        assertEquals(defaultScheme.getPriceBillingCycle(), new BigDecimal("9100"));
        assertEquals(defaultScheme.getStartPrice(), new BigDecimal("3100.00"));
        assertEquals(defaultScheme.getPriceInvoiceReceive(), new BigDecimal("0.00"));
        assertEquals(defaultScheme.getPriceInvoiceSend(), new BigDecimal("0.00"));

    }

    @Test(groups = {"persistence"})
    public void testCreateBillingPeriod() throws Exception {
        Customer testCustomer = accountRepository.createCustomer("testCustomer", "test@sendregning.no", null, null, null, null, null, null, null, null);
        BillingPeriodId billingPeriodId = null;

        try {
            billingPeriodId = billingRepository.createBillingPeriod(new CustomerId(testCustomer.getId()), billingRepository.getDefaultBillingScheme().getId());
            assertNotNull(billingPeriodId);

        } finally {
            databaseHelper.deleteBillingPeriod(billingPeriodId);
            databaseHelper.deleteCustomer(testCustomer);
        }

    }


    @BeforeMethod
    public void setUp() throws Exception {
        participantId = ObjectMother.getAdamsParticipantId();
        final RingoAccount adamsAccount = ObjectMother.getAdamsAccount();
        ringoAccount = accountRepository.createAccount(adamsAccount, participantId);

    }

    @AfterMethod
    public void tearDown() throws Exception {
        databaseHelper.deleteAllMessagesForAccount(ringoAccount);
        accountRepository.deleteAccount(ringoAccount.getId());
    }
}

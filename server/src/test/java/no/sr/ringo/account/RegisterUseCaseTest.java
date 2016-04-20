/* Created by steinar on 01.01.12 at 18:16 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.RingoConstant;
import no.sr.ringo.billing.*;
import no.sr.ringo.common.MessageHelper;
import no.sr.ringo.guice.TestModuleFactory;
import org.easymock.EasyMock;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

@Guice(moduleFactory = TestModuleFactory.class)
public class RegisterUseCaseTest {

    final RegisterUseCase registerUseCase;

    @Inject
    public RegisterUseCaseTest(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @Test
    public void testValidateRegData() {


        RegistrationData data = new RegistrationData(null, null, null, null, null, null, null, null, null, null, null, null, null, true);
        ValidationResult res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.name.required"), res.getMessage());

        data = new RegistrationData("name", null, null, null, null, null, null, null, null, null, null, null, null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.password.required"), res.getMessage());

        data = new RegistrationData("name", "pass", null, null, null, null, null, null, null, null, null, null, null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.orgNoRequired"), res.getMessage());

        data = new RegistrationData("name", "pass", null, null, null, null, null, null, null, null, null, "orgNo", null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.username.required"), res.getMessage());

        data = new RegistrationData("name", "pass", "username", null, null, null, null, null, null, null, null, "orgNo", null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals("orgNo er ikke et gyldig norsk organisasjonsnummer", res.getMessage());

        data = new RegistrationData("name", "pass", "username", null, null, null, null, null, null, null, null, "976098897", null, true);
        res = registerUseCase.validateData(data);
        assertTrue(res.isValid());
    }

    @Test(groups = {"persistence"})
    public void testRegisterExistingUserName() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        AccountRepository mockRepo = createStrictMock(AccountRepository.class);
        registerUseCase.setAccountRepository(mockRepo);
        final String orgNo = "222222222";
        expect(mockRepo.accountExists(new UserName("username"))).andReturn(true);
        replay(mockRepo);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "Andy S", "email", "phone", orgNo, null, true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("reg.user.exists"), result.getMessage());

        verify(mockRepo);
    }

    /**
     * Discount code is specified. Billing scheme with this code exists so it will be used
     */
    @Test(groups = {"persistence"})
    public void testRegisterWithCode() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        CustomerId customerId = new CustomerId(10);
        BillingSchemeId billingSchemeId = new BillingSchemeId(1);

        final String orgNo = "222222222";

        AccountRepository mockAccountRepository = createStrictMock(AccountRepository.class);
        BillingRepository mockBillingRepository = createStrictMock(BillingRepository.class);
        registerUseCase.setAccountRepository(mockAccountRepository);
        registerUseCase.setBillingRepository(mockBillingRepository);

        expect(mockAccountRepository.accountExists(new UserName("username"))).andReturn(false);
        expect(mockAccountRepository.findAccountByParticipantId(new ParticipantId(RingoConstant.PEPPOL_PARTICIPANT_PREFIX + orgNo))).andReturn(null);

        Customer customer = new Customer(customerId.toInteger(), "name", new Date(), "Andy S", "email", "phone", "country", "add1", "add2", "zip", "city", orgNo);

        expect(mockAccountRepository.createCustomer("name", "email", "phone", "country", "Andy S", "add1", "add2", "zip", "city", orgNo)).andReturn(customer);
        RingoAccount ra = new RingoAccount(customer, new UserName("username"), new Date(), "password", new AccountId(1), false, true);

        expect(mockAccountRepository.createAccount(isA(RingoAccount.class), isA(ParticipantId.class))).andReturn(ra);

        mockAccountRepository.updatePasswordOnAccount(isA(AccountId.class), isA(String.class));
        expectLastCall();

        //expect discount but it doesn't exist so expect to get the default one
        expect(mockBillingRepository.getBillingSchemeByCode("discountCode")).andReturn(new BillingScheme(billingSchemeId, "DEFAULT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN, BillingCycle.YEARLY));

        //expect to create new billing period
        expect(mockBillingRepository.createBillingPeriod(customerId, billingSchemeId)).andReturn(new BillingPeriodId(20));

        replay(mockAccountRepository, mockBillingRepository);


        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "Andy S", "email", "phone", orgNo, "discountCode", true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("difi.reg.successful"), result.getMessage());

        verify(mockAccountRepository, mockBillingRepository);
    }

    /**
     * Discount code is specified. Billing scheme with this code DOES NOT exist so NO billing period will be created
     */
    @Test(groups = {"persistence"})
    public void testRegisterWithWrongCode() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        CustomerId customerId = new CustomerId(10);
        BillingSchemeId billingSchemeId = new BillingSchemeId(1);
        final String orgNo = "222222222";
        AccountRepository mockAccountRepository = createStrictMock(AccountRepository.class);
        BillingRepository mockBillingRepository = createStrictMock(BillingRepository.class);
        registerUseCase.setAccountRepository(mockAccountRepository);
        registerUseCase.setBillingRepository(mockBillingRepository);

        expect(mockAccountRepository.accountExists(new UserName("username"))).andReturn(false);
        expect(mockAccountRepository.findAccountByParticipantId(new ParticipantId(RingoConstant.PEPPOL_PARTICIPANT_PREFIX + orgNo))).andReturn(null);

        Customer customer = new Customer(customerId.toInteger(), "name", new Date(), null, "email", "phone", "country", "add1", "add2", "zip", "city", orgNo);

        expect(mockAccountRepository.createCustomer("name", "email", "phone", "country", "contactPerson", "add1", "add2", "zip", "city", orgNo)).andReturn(customer);
        RingoAccount ra = new RingoAccount(customer, new UserName("username"), new Date(), "password", new AccountId(1), false, true);

        expect(mockAccountRepository.createAccount(isA(RingoAccount.class), isA(ParticipantId.class))).andReturn(ra);

        mockAccountRepository.updatePasswordOnAccount(isA(AccountId.class), isA(String.class));
        expectLastCall();

        //expect discount but it doesn't exist so expect to get the default one
        expect(mockBillingRepository.getBillingSchemeByCode("discountCode")).andReturn(null);

        //DO NOT expect to create new billing period
        //expect(mockBillingRepository.createBillingPeriod(customerId, billingSchemeId)).andReturn(new BillingPeriodId(20));


        replay(mockAccountRepository, mockBillingRepository);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "contactPerson", "email", "phone", orgNo, "discountCode", true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("difi.reg.successful"), result.getMessage());

        verify(mockAccountRepository, mockBillingRepository);
    }


    /**
     * Discount code is NOT specified. DEFAULT billing scheme will be used
     */
    @Test(groups = {"persistence"})
    public void testRegisterWithoutCode() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        CustomerId customerId = new CustomerId(10);
        BillingSchemeId billingSchemeId = new BillingSchemeId(1);
        final String orgNo = "222222222";
        AccountRepository mockAccountRepository = createStrictMock(AccountRepository.class);
        BillingRepository mockBillingRepository = createStrictMock(BillingRepository.class);
        registerUseCase.setAccountRepository(mockAccountRepository);
        registerUseCase.setBillingRepository(mockBillingRepository);

        expect(mockAccountRepository.accountExists(new UserName("username"))).andReturn(false);
        expect(mockAccountRepository.findAccountByParticipantId(new ParticipantId(RingoConstant.PEPPOL_PARTICIPANT_PREFIX + orgNo))).andReturn(null);

        Customer customer = new Customer(customerId.toInteger(), "name", new Date(), "contactPerson", "email", "phone", "country", "add1", "add2", "zip", "city", orgNo);

        expect(mockAccountRepository.createCustomer("name", "email", "phone", "country", "contactPerson", "add1", "add2", "zip", "city", orgNo)).andReturn(customer);
        RingoAccount ra = new RingoAccount(customer, new UserName("username"), new Date(), "password", new AccountId(1), false, true);

        expect(mockAccountRepository.createAccount(isA(RingoAccount.class), isA(ParticipantId.class))).andReturn(ra);

        mockAccountRepository.updatePasswordOnAccount(isA(AccountId.class), isA(String.class));
        expectLastCall();

        //expect discount but it doesn't exist so expect to get the default one
        expect(mockBillingRepository.getDefaultBillingScheme()).andReturn(new BillingScheme(billingSchemeId, "DEFAULT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN, BillingCycle.YEARLY));

        //expect to create new billing period
        expect(mockBillingRepository.createBillingPeriod(customerId, billingSchemeId)).andReturn(new BillingPeriodId(20));


        replay(mockAccountRepository, mockBillingRepository);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "contactPerson", "email", "phone", orgNo, null, true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("difi.reg.successful"), result.getMessage());

        verify(mockAccountRepository, mockBillingRepository);
    }


    /**
     * When register SMP flag is false, participantId won't be passed to createAccount method (account_receiver won't be created)
     */
    @Test(groups = {"persistence"})
    public void testRegisterWithoutSmp() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        CustomerId customerId = new CustomerId(10);
        BillingSchemeId billingSchemeId = new BillingSchemeId(1);
        final String orgNo = "222222222";
        AccountRepository mockAccountRepository = createStrictMock(AccountRepository.class);
        BillingRepository mockBillingRepository = createStrictMock(BillingRepository.class);
        registerUseCase.setAccountRepository(mockAccountRepository);
        registerUseCase.setBillingRepository(mockBillingRepository);

        expect(mockAccountRepository.accountExists(new UserName("username"))).andReturn(false);
        expect(mockAccountRepository.findAccountByParticipantId(new ParticipantId(RingoConstant.PEPPOL_PARTICIPANT_PREFIX + orgNo))).andReturn(null);

        Customer customer = new Customer(customerId.toInteger(), "name", new Date(), null, "email", "phone", "country", "add1", "add2", "zip", "city", orgNo);

        expect(mockAccountRepository.createCustomer("name", "email", "phone", "country", "contactPerson", "add1", "add2", "zip", "city", orgNo)).andReturn(customer);
        RingoAccount ra = new RingoAccount(customer, new UserName("username"), new Date(), "password", new AccountId(1), false, true);

        expect(mockAccountRepository.createAccount(isA(RingoAccount.class), EasyMock.<ParticipantId>isNull())).andReturn(ra);

        mockAccountRepository.updatePasswordOnAccount(isA(AccountId.class), isA(String.class));
        expectLastCall();

        //expect discount but it doesn't exist so expect to get the default one
        expect(mockBillingRepository.getDefaultBillingScheme()).andReturn(new BillingScheme(billingSchemeId, "DEFAULT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, BigDecimal.TEN, BillingCycle.YEARLY));

        //expect to create new billing period
        expect(mockBillingRepository.createBillingPeriod(customerId, billingSchemeId)).andReturn(new BillingPeriodId(20));

        replay(mockAccountRepository, mockBillingRepository);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "contactPerson", "email", "phone", orgNo, null, false);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("reg.successful"), result.getMessage());

        verify(mockAccountRepository, mockBillingRepository);
    }

}

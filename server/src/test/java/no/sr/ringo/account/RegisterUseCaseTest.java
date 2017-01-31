/* Created by steinar on 01.01.12 at 18:16 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.api.UserName;
import eu.peppol.persistence.api.account.*;
import no.sr.ringo.RingoConstant;
import no.sr.ringo.common.MessageHelper;
import no.sr.ringo.guice.ServerTestModuleFactory;
import org.easymock.EasyMock;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

@Guice(moduleFactory = ServerTestModuleFactory.class)
public class RegisterUseCaseTest {

    final RegisterUseCase registerUseCase;

    @Inject
    public RegisterUseCaseTest(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @Test
    public void testValidateRegData() {

        RegistrationData data = new RegistrationData(null, null, null, null, null, null, null, null, null, null, null, null, true);
        ValidationResult res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.name.required"), res.getMessage());

        data = new RegistrationData("name", null, null, null, null, null, null, null, null, null, null, null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.password.required"), res.getMessage());

        data = new RegistrationData("name", "pass", null, null, null, null, null, null, null, null, null, null, true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.orgNoRequired"), res.getMessage());

        data = new RegistrationData("name", "pass", null, null, null, null, null, null, null, null, null, "orgNo", true);
        res = registerUseCase.validateData(data);
        assertFalse(res.isValid());
        assertEquals(MessageHelper.getMessage("reg.username.required"), res.getMessage());


        data = new RegistrationData("name", "pass", "username", null, null, null, null, null, null, null, null, "NO976098897", true);
        res = registerUseCase.validateData(data);
        assertTrue(res.isValid());

    }

    @Test(groups = {"persistence"})
    public void testRegisterExistingUserName() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        AccountRepository mockRepo = createStrictMock(AccountRepository.class);
        registerUseCase.setAccountRepository(mockRepo);
        final String orgNo = "976098897";
        expect(mockRepo.accountExists(new UserName("username"))).andReturn(true);
        replay(mockRepo);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "Andy S", "email", "phone", orgNo, true);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("reg.user.exists"), result.getMessage());

        verify(mockRepo);
    }

    /**
     * When register SMP flag is false, participantId won't be passed to createAccount method (account_receiver won't be created)
     */
    @Test(groups = {"persistence"})
    public void testRegisterWithoutSmp() throws NoSuchAlgorithmException, UnsupportedEncodingException {

        CustomerId customerId = new CustomerId(10);
        final String orgNo = "NO222222222";
        AccountRepository mockAccountRepository = createStrictMock(AccountRepository.class);
        registerUseCase.setAccountRepository(mockAccountRepository);

        expect(mockAccountRepository.accountExists(new UserName("username"))).andReturn(false);
        expect(mockAccountRepository.findAccountByParticipantId(new ParticipantId(RingoConstant.NORWEGIAN_PEPPOL_PARTICIPANT_PREFIX + orgNo))).andReturn(null);

        Customer customer = new Customer(customerId.toInteger(), "name", new Date(), null, "email", "phone", "country", "add1", "add2", "zip", "city", orgNo);

        expect(mockAccountRepository.createCustomer("name", "email", "phone", "country", "contactPerson", "add1", "add2", "zip", "city", orgNo)).andReturn(customer);
        Account ra = new Account(customer.getCustomerId(), customer.getName(),new UserName("username"), new Date(), "password", new AccountId(1), false, true);

        expect(mockAccountRepository.createAccount(isA(Account.class), EasyMock.<ParticipantId>isNull())).andReturn(ra);

        mockAccountRepository.updatePasswordOnAccount(isA(AccountId.class), isA(String.class));
        expectLastCall();

        replay(mockAccountRepository);

        RegistrationData rd = new RegistrationData("name", "pass", "username", "add1", "add2", "zip", "city", "country", "contactPerson", "email", "phone", orgNo, false);
        RegistrationProcessResult result = registerUseCase.registerUser(rd);

        assertEquals(MessageHelper.getMessage("reg.successful"), result.getMessage());

        verify(mockAccountRepository);
    }

}

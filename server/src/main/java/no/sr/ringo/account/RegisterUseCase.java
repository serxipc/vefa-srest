/* Created by steinar on 01.01.12 at 17:54 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.email.EmailService;
import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.RingoConstant;
import no.sr.ringo.common.MessageHelper;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Transactional;
import no.sr.ringo.peppol.PeppolParticipantId;
import no.sr.ringo.security.CredentialHandler;
import no.sr.ringo.security.HashedPassword;
import no.sr.ringo.security.Hasher;
import no.sr.ringo.security.SaltData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * UseCase responsible for the process of registration of new users
 *
 * @author Adam Mscisz adam@sendregning.no
 */
@RequestScoped
public class RegisterUseCase {

    private static Logger logger = LoggerFactory.getLogger(RegisterUseCase.class);

    private AccountRepository accountRepository;
    private JdbcTxManager jdbcTxManager;
    private final EmailService emailService;
    private final CredentialHandler credentialHandler;

    @Inject
    RegisterUseCase(AccountRepository accountRepository, JdbcTxManager jdbcTxManager, EmailService emailService, CredentialHandler credentialHandler) {
        this.jdbcTxManager = jdbcTxManager;
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.credentialHandler = credentialHandler;
    }

    public ValidationResult validateData(RegistrationData registrationData) {

        boolean valid = true;
        String message = null;

        if (isEmpty(registrationData.getName())) {
            message = MessageHelper.getMessage("reg.name.required");
        } else if (isEmpty(registrationData.getPassword())) {
            message = MessageHelper.getMessage("reg.password.required");
        } else if (isEmpty(registrationData.getOrgNo())) {
            message = MessageHelper.getMessage("reg.orgNoRequired");
        } else if (isEmpty(registrationData.getUsername())) {
            message = MessageHelper.getMessage("reg.username.required");
        } else {
            if (!PeppolParticipantId.isValidNorwegianOrgNum(registrationData.getOrgNo())) {
                message = MessageHelper.getMessage("reg.orgNo.invalid", registrationData.getOrgNo());
            }
        }

        if (message != null) {
            valid = false;
        }

        return new ValidationResult(valid, message);

    }

    @Transactional
    public RegistrationProcessResult registerUser(final RegistrationData registrationData) {

        // check if user with this username already exists
        boolean exists = accountRepository.accountExists(new UserName(registrationData.getUsername()));
        if (exists) {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.RINGO, false, MessageHelper.getMessage("reg.user.exists"));
        }

        //Prefix given orgNo with 9908
        final PeppolParticipantId peppolParticipantId = PeppolParticipantId.valueFor(registrationData.getOrgNo());
        if (peppolParticipantId == null) {
            throw new IllegalArgumentException("Provided organisation number is invalid");
        }

        RingoAccount orgNoUsed = accountRepository.findAccountByParticipantId(new ParticipantId(peppolParticipantId.stringValue()));
        if (orgNoUsed != null) {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.RINGO, false, MessageHelper.getMessage("reg.orgNo.registered"));
        }

        // create customer entry
        Customer customer = accountRepository.createCustomer(registrationData.getName(), registrationData.getEmail(), registrationData.getPhone(), registrationData.getCountry(), registrationData.getContactPerson(), registrationData.getAddress1(), registrationData.getAddress2(), registrationData.getZip(), registrationData.getCity(), registrationData.getOrgNo());

        RingoAccount account = new RingoAccount(customer, new UserName(registrationData.getUsername()), null, null, null, false, true);

        // create account entry and account_receiver entry (only if registering in SMP)
        //Prefix given orgNo with 9908
        ParticipantId participantId = registrationData.isRegisterSmp() ? new ParticipantId(RingoConstant.PEPPOL_PARTICIPANT_PREFIX + registrationData.getOrgNo()) : null;
        RingoAccount stored = accountRepository.createAccount(account, participantId);

        // Encrypts/hashes the password
        String mutatedPassword = credentialHandler.mutate(registrationData.getPassword());

        // update account entry with password
        accountRepository.updatePasswordOnAccount(stored.getId(), mutatedPassword);

        // FIXME: add code here if you want to register party with SMP
        if (registrationData.isRegisterSmp()) {
            logger.info(String.format("Registering %s, with orgNo %s at SMP is not implemented", registrationData.getName(), registrationData.getOrgNo()));
        } else {
            logger.debug(String.format("Skipping registering %s, with orgNo %s at SMP", registrationData.getName(), registrationData.getOrgNo()));
        }

        //send info to sales department that new customer has been registered
        emailService.sendRegistrationNotification(account, registrationData.isRegisterSmp() ? "konto skal motta" : "konto skal kun sende");

        //if difi registration happened
        if (registrationData.isRegisterSmp()) {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, true, MessageHelper.getMessage("difi.reg.successful"));
        } else {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.RINGO, true, MessageHelper.getMessage("reg.successful"));
        }

    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    /**
     * package protected for tests
     *
     * @param accountRepository
     */
    void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

}

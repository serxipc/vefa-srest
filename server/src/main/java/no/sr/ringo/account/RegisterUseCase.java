/* Created by steinar on 01.01.12 at 17:54 */
package no.sr.ringo.account;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import no.sr.ringo.email.EmailService;
import eu.peppol.identifier.ParticipantId;
import no.difi.ws.client.SmpRegistrationClient;
import no.difi.ws.client.SmpRegistrationStatus;
import no.sr.ringo.RingoConstant;
import no.sr.ringo.billing.BillingRepository;
import no.sr.ringo.billing.BillingScheme;
import no.sr.ringo.common.MessageHelper;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Transactional;
import no.sr.ringo.peppol.PeppolParticipantId;
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
    private BillingRepository billingRepository;
    private SmpRegistrationClient smpRegistrationClient;
    private JdbcTxManager jdbcTxManager;
    private final EmailService emailService;

    @Inject
    RegisterUseCase(AccountRepository accountRepository, SmpRegistrationClient smpRegistrationClient, BillingRepository billingRepository, JdbcTxManager jdbcTxManager, EmailService emailService) {
        this.jdbcTxManager = jdbcTxManager;
        this.accountRepository = accountRepository;
        this.billingRepository = billingRepository;
        this.smpRegistrationClient = smpRegistrationClient;
        this.emailService = emailService;
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

        // calculate password hash
        SaltData salt = new SaltData(stored.getId().toInteger(), stored.getCreated());

        Hasher hasher = new Hasher();
        HashedPassword hash = null;
        try {
            hash = hasher.hash(registrationData.getPassword(), salt.getSalt());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error occurred while trying to calculate password");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error occurred while trying to calculate password");
        }

        // update account entry with password
        accountRepository.updatePasswordOnAccount(stored.getId(), hash.toString());

        //create billing period
        createBillingPeriod(registrationData.getDiscountCode(), new CustomerId(customer.getId()));


        //FIX ME THE LOGIC HERE NEEDS TO BE FIXED

        //REGISTER THE USER WITH THE ELMA
        if (registrationData.isRegisterSmp()) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Trying to register %s, with orgNo %s at difi", registrationData.getName(), registrationData.getOrgNo()));
            }
            //register user in DIFI
            SmpRegistrationStatus status = smpRegistrationClient.register(registrationData.getName(), registrationData.getOrgNo(), registrationData.getPhone(), registrationData.getEmail(), registrationData.getContactPerson());
            switch (status) {
                case ALREADY_REGISTERED:
                    jdbcTxManager.setRollbackOnly();
                    return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, false, MessageHelper.getMessage("difi.orgNo.registered"));
                case INVALID_ORG_NO:
                    jdbcTxManager.setRollbackOnly();
                    return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, false, MessageHelper.getMessage("difi.orgNo.invalid"));
                case ERROR:
                    jdbcTxManager.setRollbackOnly();
                    return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, false, MessageHelper.getMessage("difi.error.occurred"));
                case OK:
                    //that's what we expected
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Successfully registered %s, with orgNo %s at difi", registrationData.getName(), registrationData.getOrgNo()));
                    }
                    break;
                default:
                    jdbcTxManager.setRollbackOnly();
                    return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, false, MessageHelper.getMessage("difi.reg.failed"));
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Not registering %s, with orgNo %s at difi", registrationData.getName(), registrationData.getOrgNo()));
            }
        }

        //send info to sales department that new customer has been registered
        emailService.sendRegistrationNotification(account, registrationData.getDiscountCode());


        //if difi registration happened
        if (registrationData.isRegisterSmp()) {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.DIFI, true, MessageHelper.getMessage("difi.reg.successful"));
        } else {
            return new RegistrationProcessResult(RegistrationProcessResult.RegistrationSource.RINGO, true, MessageHelper.getMessage("reg.successful"));
        }

    }

    /**
     * If discount code:
     * - is empty: DEFAULT scheme will be used in billing period
     * - is not empty and exists in billing_scheme: it will be used in billing_period
     * - is not empty and DOESN"T exist in billing_scheme - no billing_period will be created
     *
     * @param discountCode
     * @param customerId
     */
    private void createBillingPeriod(String discountCode, CustomerId customerId) {
        BillingScheme billingScheme = null;
        if (discountCode != null && discountCode.trim().length() > 0) {
            billingScheme = billingRepository.getBillingSchemeByCode(discountCode);
        } else {
            billingScheme = billingRepository.getDefaultBillingScheme();
        }

        if (billingScheme != null) {
            billingRepository.createBillingPeriod(customerId, billingScheme.getId());
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

    /**
     * package protected for tests
     *
     * @param billingRepository
     */
    void setBillingRepository(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

}

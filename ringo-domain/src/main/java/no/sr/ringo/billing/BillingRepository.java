package no.sr.ringo.billing;

import no.sr.ringo.account.CustomerId;

public interface BillingRepository {


    public BillingScheme getBillingSchemeByCode(final String code);

    /**
     * Gets billingScheme with code = 'DEFAULT'
     */
    public BillingScheme getDefaultBillingScheme();

    /**
     * Creates new entry in billing_period table with startDate set to now and end_date to null
     */
    public BillingPeriodId createBillingPeriod(final CustomerId customerId, final BillingSchemeId billingSchemeId);

}

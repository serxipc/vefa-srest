package no.sr.ringo.billing;

import no.sr.ringo.account.CustomerId;

import java.util.Date;

/**
 * User: Adam
 * Date: 7/25/12
 * Time: 9:41 AM
 */
public class BillingPeriod {
    private final BillingPeriodId id;
    private final BillingSchemeId billingSchemeId;
    private final CustomerId customerId;
    private final Date fromDate;
    private final Date toDate;

    public BillingPeriod(BillingPeriodId id, BillingSchemeId billingSchemeId, CustomerId customerId, Date fromDate, Date toDate) {
        this.id = id;
        this.billingSchemeId = billingSchemeId;
        this.customerId = customerId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public BillingPeriodId getId() {
        return id;
    }

    public BillingSchemeId getBillingSchemeId() {
        return billingSchemeId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }
}

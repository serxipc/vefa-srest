package no.sr.ringo.billing;

import com.google.inject.Inject;
import no.sr.ringo.account.AccountRepository;
import no.sr.ringo.account.CustomerId;
import no.sr.ringo.guice.jdbc.JdbcTxManager;
import no.sr.ringo.guice.jdbc.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Calendar;

@Repository
public class BillingRepositoryImpl implements BillingRepository {

    private static final String DEFAULT_BILLING_SCHEME_CODE = "DEFAULT";

    private final JdbcTxManager jdbcTxManager;
    private final AccountRepository accountRepository;

    @Inject
    public BillingRepositoryImpl(JdbcTxManager jdbcTxManager, AccountRepository accountRepository) {
        this.jdbcTxManager = jdbcTxManager;
        this.accountRepository = accountRepository;
    }

    @Override
    public BillingScheme getBillingSchemeByCode(final String code) {
        final Connection con = jdbcTxManager.getConnection();
        PreparedStatement ps = null;

        try {
            String sql = "select * from  billing_scheme where code like ?";
            ps = con.prepareStatement(sql);

            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BillingSchemeId billingSchemeId = new BillingSchemeId(rs.getInt("id"));
                BigDecimal priceInvoiceSend = rs.getBigDecimal("price_invoice_send");
                BigDecimal priceInvoiceReceive = rs.getBigDecimal("price_invoice_receive");
                BigDecimal priceBillingCycle = rs.getBigDecimal("price_billing_cycle");
                BigDecimal startPrice = rs.getBigDecimal("start_price");
                BillingCycle billingCycle = BillingCycle.valueOf(rs.getString("billing_cycle"));
                return new BillingScheme(billingSchemeId, code, priceInvoiceSend, priceInvoiceReceive, priceBillingCycle, startPrice, billingCycle);
            }

            return null;
        } catch (SQLException e) {
            throw new IllegalStateException("Error fetching billing scheme for code " + code + "; " + e, e);
        }
    }

    @Override
    public BillingScheme getDefaultBillingScheme() {
        return getBillingSchemeByCode(DEFAULT_BILLING_SCHEME_CODE);
    }

    @Override
    public BillingPeriodId createBillingPeriod(final CustomerId customerId,final BillingSchemeId billingSchemeId) {
        if (billingSchemeId == null) {
            throw new IllegalArgumentException("Billing scheme id required");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer id required");
        }

        final Connection con = jdbcTxManager.getConnection();

        //does BillingScheme exist?
        if (!billingSchemeExists(billingSchemeId)) {
            throw new IllegalArgumentException(String.format("Billing scheme with id %d doesn't exist", billingSchemeId.toInteger()));
        }

        try {

            //does customer exist?
            if (accountRepository.findCustomerById(customerId.toInteger()) == null) {
                throw new IllegalArgumentException(String.format("Customer with id %d doesn't exist", customerId));
            }

            //create test account
            String sql = "insert into billing_period (customer_id, billing_scheme_id, from_date) values (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId.toInteger());
            ps.setInt(2, billingSchemeId.toInteger());


            //TODO: the start date should be passed as a parameter not hardcoded here!
            //sets the billing start 30 days in the future.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR,30);
            ps.setTimestamp(3, new Timestamp(cal.getTimeInMillis()));

            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new BillingPeriodId(rs.getInt(1));
            } else {
                throw new IllegalStateException("Unable to obtain generated key after insert.");
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Checks if scheme for given id exists
     *
     * @param billingSchemeId
     * @return
     */
    private boolean billingSchemeExists(final BillingSchemeId billingSchemeId) {
        if (billingSchemeId == null) {
            return false;
        }

        final Connection con = jdbcTxManager.getConnection();
        String sql = "select count(*) from billing_scheme where id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, billingSchemeId.toInteger());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("%s failed with schemeId: %s", sql, billingSchemeId.toInteger()), e);
        }
    }
}

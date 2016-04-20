package no.sr.ringo.billing;

import java.math.BigDecimal;

/**
 * User: Adam
 * Date: 7/25/12
 * Time: 9:41 AM
 */
public class BillingScheme {
    private final BillingSchemeId id;
    private final String code;
    private final BigDecimal priceInvoiceSend;
    private final BigDecimal priceInvoiceReceive;
    private final BigDecimal priceBillingCycle;
    private final BigDecimal startPrice;
    private final BillingCycle billingCycle;

    public BillingScheme(BillingSchemeId id, String code, BigDecimal priceInvoiceSend, BigDecimal priceInvoiceReceive, BigDecimal priceBillingCycle, BigDecimal startPrice, BillingCycle billingCycle) {
        this.id = id;
        this.code = code;
        this.priceInvoiceSend = priceInvoiceSend;
        this.priceInvoiceReceive = priceInvoiceReceive;
        this.priceBillingCycle = priceBillingCycle;
        this.startPrice = startPrice;
        this.billingCycle = billingCycle;
    }

    public BillingSchemeId getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getPriceInvoiceSend() {
        return priceInvoiceSend;
    }

    public BigDecimal getPriceInvoiceReceive() {
        return priceInvoiceReceive;
    }

    public BigDecimal getPriceBillingCycle() {
        return priceBillingCycle;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingScheme that = (BillingScheme) o;

        if (billingCycle != that.billingCycle) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (priceBillingCycle != null ? !priceBillingCycle.equals(that.priceBillingCycle) : that.priceBillingCycle != null)
            return false;
        if (priceInvoiceReceive != null ? !priceInvoiceReceive.equals(that.priceInvoiceReceive) : that.priceInvoiceReceive != null)
            return false;
        if (priceInvoiceSend != null ? !priceInvoiceSend.equals(that.priceInvoiceSend) : that.priceInvoiceSend != null)
            return false;
        if (startPrice != null ? !startPrice.equals(that.startPrice) : that.startPrice != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (priceInvoiceSend != null ? priceInvoiceSend.hashCode() : 0);
        result = 31 * result + (priceInvoiceReceive != null ? priceInvoiceReceive.hashCode() : 0);
        result = 31 * result + (priceBillingCycle != null ? priceBillingCycle.hashCode() : 0);
        result = 31 * result + (startPrice != null ? startPrice.hashCode() : 0);
        result = 31 * result + (billingCycle != null ? billingCycle.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BillingScheme{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", priceInvoiceSend=" + priceInvoiceSend +
                ", priceInvoiceReceive=" + priceInvoiceReceive +
                ", priceBillingCycle=" + priceBillingCycle +
                ", startPrice=" + startPrice +
                ", billingCycle=" + billingCycle +
                '}';
    }
}

package no.sr.ringo.billing;

public class BillingPeriodId {
    private Integer id;

    public BillingPeriodId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id required");
        }
        this.id = Integer.parseInt(id);
    }

    public BillingPeriodId(Integer id){
        if (id == null) {
            throw new IllegalArgumentException("id required");
        }

        this.id = id;
    }

    public Integer toInteger(){
        return id;
    }


    public String toString() {
        return id.toString();
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BillingPeriodId that = (BillingPeriodId) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }


    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

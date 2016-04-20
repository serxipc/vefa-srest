package no.sr.ringo.peppol;

/**
 * Value object which represents the Peppol ChannelId.
 *
 * User: andy
 * Date: 2/3/12
 * Time: 9:41 AM
 */
public class PeppolChannelId {

    final String value;

    public PeppolChannelId(String channelId) {
        if (channelId == null) {
            value="";
        } else
            this.value = channelId.trim();
    }

    public String stringValue() {
        return this.value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PeppolChannelId");
        sb.append("{value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeppolChannelId that = (PeppolChannelId) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }


}

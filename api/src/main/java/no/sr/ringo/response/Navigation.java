/* Created by steinar on 06.01.12 at 15:31 */
package no.sr.ringo.response;

import java.net.URI;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class Navigation {
    
    final URI next;
    final URI previous;

    public Navigation(URI previous, URI next) {
        this.next = next;
        this.previous = previous;
    }

    public URI getNext() {
        return next;
    }

    public URI getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Navigation");
        sb.append("{next=").append(next);
        sb.append(", previous=").append(previous);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Navigation that = (Navigation) o;

        if (next != null ? !next.equals(that.next) : that.next != null) return false;
        if (previous != null ? !previous.equals(that.previous) : that.previous != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = next != null ? next.hashCode() : 0;
        result = 31 * result + (previous != null ? previous.hashCode() : 0);
        return result;
    }
}

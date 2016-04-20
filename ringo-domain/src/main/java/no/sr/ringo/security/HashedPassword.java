package no.sr.ringo.security;

public class HashedPassword {

    private final String hashedPassword;

    public HashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    @Override
    public String toString() {
        return hashedPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashedPassword that = (HashedPassword) o;

        if (hashedPassword != null ? !hashedPassword.equals(that.hashedPassword) : that.hashedPassword != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashedPassword != null ? hashedPassword.hashCode() : 0;
    }
}

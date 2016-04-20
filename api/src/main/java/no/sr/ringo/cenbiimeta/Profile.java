package no.sr.ringo.cenbiimeta;

/**
 * Represents a Profile according to the CEN/BII Meta model.
 *
 * User: steinar
 * Date: 06.11.12
 * Time: 14:21
 */
public class Profile {

    private final String name;
    private final String description;
    private final ProfileId profileId;

    public Profile(String name, String description, ProfileId profileId) {
        this.name = name;
        this.description = description;
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProfileId getProfileId() {
        return profileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (description != null ? !description.equals(profile.description) : profile.description != null) return false;
        if (name != null ? !name.equals(profile.name) : profile.name != null) return false;
        if (profileId != null ? !profileId.equals(profile.profileId) : profile.profileId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (profileId != null ? profileId.hashCode() : 0);
        return result;
    }

    static class Predefined {
        public static final Profile BII01_CATALOGUE_ONLY = new Profile("BII01","Catalogue only", ProfileId.Predefined.BII01_CATALOGUE);
        public static final Profile BII03_ORDER_ONLY = new Profile("BII03","Order only", ProfileId.Predefined.BII03_ORDER_ONLY);
        public static final Profile BII04_INVOICE_ONLY = new Profile("BII04","Invoice only", ProfileId.Predefined.BII04_INVOICE_ONLY);
        public static final Profile BII06_PROCUREMENT = new Profile("BII06","Procurement", ProfileId.Predefined.BII06_PROCUREMENT);
    }
}

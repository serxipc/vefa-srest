package no.sr.ringo.peppol;

/**
 * Oasis/UBL root name space
 *
 * User: steinar
 * Date: 07.11.12
 * Time: 15:07
 */
public class RootNameSpace {

    // Well known predefined root name spaces.
    public static final RootNameSpace INVOICE = new RootNameSpace("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
    public static final RootNameSpace CREDIT = new RootNameSpace("urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2");
    public static final RootNameSpace REMINDER = new RootNameSpace("urn:oasis:names:specification:ubl:schema:xsd:Reminder-2");
    public static final RootNameSpace ORDER = new RootNameSpace("urn:oasis:names:specification:ubl:schema:xsd:Order-2");
    public static final RootNameSpace ORDER_RESPONSE = new RootNameSpace("urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2");

    private final String rootNameSpace;

    public RootNameSpace(String rootNameSpace) {

        this.rootNameSpace = rootNameSpace;
    }

    @Override
    public String toString() {
        return rootNameSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RootNameSpace that = (RootNameSpace) o;

        if (rootNameSpace != null ? !rootNameSpace.equals(that.rootNameSpace) : that.rootNameSpace != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootNameSpace != null ? rootNameSpace.hashCode() : 0;
    }

    public RootNameSpace valueOf(String rootNameSpace) {
        return new RootNameSpace(rootNameSpace);
    }

}

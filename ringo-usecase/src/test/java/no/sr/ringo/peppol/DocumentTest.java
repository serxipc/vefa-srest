package no.sr.ringo.peppol;

public abstract class DocumentTest {

    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

    public String xmlHeader() {
        return XML_HEADER;
    }

    protected String join(String[] contents) {
        return contents.toString();
    }

}

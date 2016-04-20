package no.sr.ringo.peppol;

public abstract class PeppolDocumentTest extends DocumentTest {

    protected String validXmlDocumentFor(PeppolDocumentTypeId documentId, String... contents) {
        return xmlHeader() +
                join(contents) +
                document(documentId);
    }

    protected String document(PeppolDocumentTypeId documentId) {
        if (documentId.equals(PeppolDocumentTypeId.EHF_INVOICE)) {
            return invoice();
        }
        else if(documentId.equals(PeppolDocumentTypeId.EHF_CREDIT_NOTE)){
            return creditNote();
        }
        else {
            return unknown();
        }
    }

    protected String invoice() {
        return "<Invoice:Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n" +
                "         xmlns:Invoice=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n" +
                "         xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n" +
                "         xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"\n" +
                "        ></Invoice:Invoice>";
    }

    protected String creditNote() {
        return "<CreditNote:CreditNote xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2\"" +
                "        xmlns:CreditNote=\"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2\"" +
                "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "        xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" " +
                "        xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" " +
                "        xmlns:ext=\"urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2\"></CreditNote>";
    }

    protected String unknown() {
        return "<Order:Order xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Order-2\"\n" +
                "         xmlns:Invoice=\"urn:oasis:names:specification:ubl:schema:xsd:Order-2\"\n" +
                "         xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n" +
                "         xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\"\n" +
                "        ></Order:Order>";
    }

}

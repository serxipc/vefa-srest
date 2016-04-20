package no.sr.ringo.document;

import no.sr.ringo.peppol.PeppolDocumentTypeId;

public interface PeppolDocumentFactory {

    PeppolDocument makePeppolDocument(PeppolDocumentTypeId peppolDocumentTypeId, String contentsAsXml);
}

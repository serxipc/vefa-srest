/* Created by steinar on 08.01.12 at 22:20 */
package no.sr.ringo.response;

import no.sr.ringo.peppol.PeppolDocumentTypeId;
import no.sr.ringo.smp.SmpLookupResult;

/**
 * Class representing the result of /directory/{participantId}/{localName} request
 * @author adam
 */
public class SmpLookupResponse implements RestResponse {

    private static final String version = "1.0";

    private final SmpLookupResult smpLookupResult;

    public SmpLookupResponse(SmpLookupResult smpLookupResult) {
        this.smpLookupResult = smpLookupResult;
    }

    public String asXml() {
        StringBuilder resultXml = new StringBuilder();
        resultXml.append("<directory-response version=\""+version+"\">\n");
        resultXml.append(documentTypesAsXml());
        resultXml.append("</directory-response>");

        return resultXml.toString();
    }

    private String documentTypesAsXml() {
        StringBuilder resultXml = new StringBuilder();
        for (PeppolDocumentTypeId peppolDocumentTypeId : smpLookupResult.getAcceptedDocumentTypes()) {
            resultXml.append("  <accepted-document-transfer>\n");
            addDocumentId(resultXml, peppolDocumentTypeId);
            addProfileId(resultXml, peppolDocumentTypeId);
            resultXml.append("  </accepted-document-transfer>\n");
        }
        return resultXml.toString();
    }

    private void addProfileId(StringBuilder resultXml, PeppolDocumentTypeId peppolDocumentTypeId) {
        resultXml.append("     "+"<ProcessID>"+smpLookupResult.profileIdFor(peppolDocumentTypeId)+"</ProcessID>\n");
    }

    private void addDocumentId(StringBuilder resultXml, PeppolDocumentTypeId peppolDocumentTypeId) {
        resultXml.append("     "+"<DocumentID>"+peppolDocumentTypeId.toString()+"</DocumentID>\n");
    }

    public String getVersion() {
        return version;
    }


}

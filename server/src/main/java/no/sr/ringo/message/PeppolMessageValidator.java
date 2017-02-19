package no.sr.ringo.message;

import eu.peppol.identifier.ParticipantId;
import no.sr.ringo.resource.InvalidUserInputWebException;
import org.apache.commons.lang.StringUtils;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Validator used to validateHeader outbound post parameters.
 * This includes performing smpLookup to check if recipient is registered and whether documentType is supported
 * <p/>
 *
 * @author Adam
 * @author Thore
 */
public class PeppolMessageValidator {

    private final PeppolMessage peppolMessage;
    private final OutboundPostParams postParams;
    private final String LOCAL_VALIDATOR = "http://127.0.0.1:9090/validate";

    public PeppolMessageValidator(PeppolMessage peppolMessage, OutboundPostParams postParams) {
        this.peppolMessage = peppolMessage;
        this.postParams = postParams;
    }

    /**
     * Validates all post parameters
     */
    public PeppolMessage validateHeader() {
        validateRecipientWithoutSmpLookup();
        validateSender();
        validateDocumentIdAndResolveDocumentTypeUsingSmpLookupIfOldStyleAcronymIsUsed();
        validateProcessId();
        return peppolMessage;
    }

    public void validateDocument() {

        String result = null;

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(peppolMessage.getXmlMessage());
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            result = httpPostXmlToUrl(LOCAL_VALIDATOR, outputStream.toString());
        } catch (Exception e) {
            throw new InvalidUserInputWebException("Unable to validate the XML document", e);
        }

        if (result == null)
            throw new InvalidUserInputWebException("XmlDocument was not validated - got null response\n");
        if (result.contains("<status>error</status>"))
            throw new InvalidUserInputWebException(getErrorMessage("XmlDocument contains error", result));
        if (result.contains("<status>fatal</status>"))
            throw new InvalidUserInputWebException(getErrorMessage("XmlDocument was unknown or corrupt", result));

    }

    private String httpPostXmlToUrl(String url, String xml) throws Exception {

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Ringo Server");
        con.setDoOutput(true);
        // con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(xml);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    }

    private String getErrorMessage(String message, String result) {
        return message + "\n" + result;
    }

    private void validateProcessId() {
        //process id
        if (StringUtils.isEmpty(postParams.getProcessIdString())) {
            throw new InvalidUserInputWebException("ProcessId required");
        }
        // ProfileIdResolver processIdResolver = new ProfileIdResolver();
        //not extracted by EHFDocumentExtractor
        if (peppolMessage.getPeppolHeader().getProfileId() == null) {
            throw new InvalidUserInputWebException(String.format("Wrong processId value: %s", postParams.getProcessIdString()));
        }
    }

    private void validateDocumentIdAndResolveDocumentTypeUsingSmpLookupIfOldStyleAcronymIsUsed() {
        //document id
        if (StringUtils.isEmpty(postParams.getDocumentIdString())) {
            throw new InvalidUserInputWebException("DocumentId required");
        }

        //not extracted by EHFDocumentExtractor
        if (peppolMessage.getPeppolHeader().getPeppolDocumentTypeId() == null) {
            throw new InvalidUserInputWebException(String.format("Wrong documentId value: %s", postParams.getDocumentIdString()));
        } else if (!peppolMessage.getPeppolHeader().getPeppolDocumentTypeId().isKnown()) {
            throw new InvalidUserInputWebException(String.format("Unknown document type: %s", postParams.getDocumentIdString()));
        }
    }

    private void validateSender() {
        //sender id
        ParticipantId senderId = peppolMessage.getPeppolHeader().getSender();
        if (senderId == null) {
            throw new InvalidUserInputWebException(String.format("Wrong senderId value: %s", postParams.getSenderIdString()));
        }
    }

    private ParticipantId validateRecipientWithoutSmpLookup() {
        //recipient id
        ParticipantId receiver = ParticipantId.valueOf(postParams.getRecipientIdString());
        if (peppolMessage.getPeppolHeader().getReceiver() == null) {
            throw new InvalidUserInputWebException(String.format("Wrong recipientId value: %s", postParams.getRecipientIdString()));
        }
        return receiver;
    }
}

package no.sr.ringo.response;

import no.sr.ringo.client.RingoService;
import no.sr.ringo.common.XmlHelper;
import no.sr.ringo.response.xml.AcceptedDocumentTransfersXmlSpec;
import no.sr.ringo.response.xml.XmlResponseParser;
import no.sr.ringo.smp.AcceptedDocumentTransfer;
import org.apache.http.HttpStatus;

import java.util.List;

/**
 * Handler responsible for parsing smp lookup response and creating list of AcceptedDocumentTransfer objects
 * User: Adam
 * Date: 10/25/12
 * Time: 3:16 PM
 */
public class AcceptedDocumentTransfersRingoResponseHandler extends GenericXmlRingoResponseHandler<List<AcceptedDocumentTransfer>> {


    public AcceptedDocumentTransfersRingoResponseHandler(RingoService ringoService) {
        super(ringoService);
        validResponseStatusCodes.add(HttpStatus.SC_NO_CONTENT);
    }

    public List<AcceptedDocumentTransfer> resolve(XmlResponseParser xmlResponseParser) {
        return getAcceptedDocumentTransfers(xmlResponseParser);
    }

    /**
     * gets the list of accepted document requests
     *
     * @param xmlResponseParser
     * @return
     */
    private List<AcceptedDocumentTransfer> getAcceptedDocumentTransfers(XmlResponseParser xmlResponseParser) {
        XmlHelper<AcceptedDocumentTransfer> xmlHelper = new XmlHelper<AcceptedDocumentTransfer>(new AcceptedDocumentTransfersXmlSpec());
        return xmlResponseParser.selectList(xmlHelper);
    }

}



package no.sr.ringo.client;

import no.sr.ringo.standalone.DefaultRingoConfig;
import no.sr.ringo.standalone.parser.RingoClientConnectionParams;

/**
 * Posts http request notifying about client error
 * User: Adam
 * Date: 4/9/13
 * Time: 9:29 AM
 */
public class ClientErrorNotifier {

    private RingoService ringoService;

    /**
     * Uses ringo client to post error notification request
     */
    public void sendErrorNotification(ErrorNotificationData errorNotificationData, RingoClientConnectionParams connectionParams) {
            getRingoService(connectionParams).sendErrorNotification(errorNotificationData);
    }

    /**
     * Returns ringo service with smaller socket timeout as we don't want to wait long for the response if coomand line is wrong.
     * If it doesn't finish within short time nothing wrong happens, we ignore the error anyways because it's just
     * the notification.
     *
     * @param connectionParams
     * @return
     */
    private RingoService getRingoService(RingoClientConnectionParams connectionParams) {
        if (ringoService == null) {
            ringoService = new RingoServiceRestImpl(new DefaultRingoConfig(connectionParams.getAccessPointURI().toASCIIString(), connectionParams.getProxySettings(), 5, 10), connectionParams.getUsername(), connectionParams.getPassword());
        }
        return ringoService;
    }

    //for tests
    protected void injectRingoService(RingoService ringoService) {
        this.ringoService = ringoService;
    }

}

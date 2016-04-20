/* Created by steinar on 05.01.12 at 16:24 */
package no.sr.ringo.client;

import no.sr.ringo.response.InboxRingoResponseHandler;

/**
 * Provides access to all incoming messages that have not yet been read.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class Inbox extends Messagebox {

    /**
     * It is not intended for client code to create this object.
     * The inbox can be fetched by calling getInbox() on the ringoClient.
     *
     * @param ringoService
     *
     */
    Inbox(RingoService ringoService) {
        super(ringoService);
    }

    @Override
    public String getPath() {
        return "inbox";
    }

    /**
     * Fetches the messages that are currently available in this message box.
     *
     * @return
     */
    @Override
    public Messages getMessages() {
        return ringoService.messages(this, new InboxRingoResponseHandler(ringoService));
    }
}

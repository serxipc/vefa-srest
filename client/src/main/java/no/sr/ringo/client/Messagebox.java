/* Created by steinar on 05.01.12 at 16:24 */
package no.sr.ringo.client;

import no.sr.ringo.common.MessageContainer;
import no.sr.ringo.response.MessageListRingoResponseHandler;

/**
 * Provides access to all messages.
 *
 * @author Adam Mscisz adam@sendregning.no
 */
public class Messagebox implements MessageContainer {
    protected final RingoService ringoService;

    Messagebox(RingoService ringoService) {
        this.ringoService = ringoService;
    }

    /**
     * Fetches the number of messages in this message box.
     *
     * @return count of messages in the inbox
     */
    public Integer getCount() {
        return ringoService.count(this);
    }

    /**
     * Fetches the messages that are currently available in this message box.
     *
     * @return
     */
    public Messages getMessages() {
        return ringoService.messages(this, new MessageListRingoResponseHandler(ringoService));
    }

    public String getPath() {
        return "messages";
    }

}

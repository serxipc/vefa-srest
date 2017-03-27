/* Created by steinar on 09.01.12 at 13:58 */
package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;
import no.sr.ringo.peppol.RingoUtils;

import java.util.List;

/**
 * The response from a query on /messages
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class MessagesQueryResponse implements MessageQueryRestResponse {

    String version = "1.0";

    Navigation navigation;

    final List<MessageWithLocations> messageList;

    public MessagesQueryResponse(List<MessageWithLocations> messages) {
        this.messageList = messages;
    }

    public String getVersion() {
        return version;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public void setNavigation(Navigation navigation) {
        this.navigation = navigation;
    }

    public List<MessageWithLocations> getMessageList() {
        return messageList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MessagesQueryResponse");
        sb.append("{version='").append(version).append('\'');
        sb.append(", navigation=").append(navigation);
        sb.append(", messageList=").append(messageList);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessagesQueryResponse that = (MessagesQueryResponse) o;

        if (messageList != null ? !messageList.equals(that.messageList) : that.messageList != null) return false;
        if (navigation != null ? !navigation.equals(that.navigation) : that.navigation != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version != null ? version.hashCode() : 0;
        result = 31 * result + (navigation != null ? navigation.hashCode() : 0);
        result = 31 * result + (messageList != null ? messageList.hashCode() : 0);
        return result;
    }

    /**
     * The Response as XML.
     * @return string representation of xml
     */
    public String asXml() {
        StringBuilder xml = new StringBuilder();
        xml.append("<messages-query-response version=\""+version+"\">\n");
        navigationAsXml(xml);
        xml.append("<messages>\n");
        messagesAsXml(xml);
        xml.append("</messages>\n</messages-query-response>");
        return xml.toString();
    }

    /**
     * Helper method for adding the navigation
     * @param xml the {@link StringBuilder} instance
     */
    protected void navigationAsXml(StringBuilder xml) {
        if (navigation == null) {
            xml.append("<navigation/>\n");
        }
        else {
            xml.append("<navigation>\n");
            if (navigation.getNext() != null) {
                xml.append(String.format("<next>%s</next>\n", RingoUtils.toXml(navigation.getNext())));
            }
            if (navigation.getPrevious() != null) {
                xml.append(String.format("<previous>%s</previous>\n", RingoUtils.toXml(navigation.getPrevious())));
            }
            xml.append("</navigation>\n");
        }
    }

    /**
     * Helper method generating xml from messages
     * @param xml {@link StringBuilder} instance
     */
    protected void messagesAsXml(StringBuilder xml) {
        for (MessageWithLocations current : messageList) {
            SingleMessagesResponse.singleMessageAsXml(xml, current);
        }
    }

}

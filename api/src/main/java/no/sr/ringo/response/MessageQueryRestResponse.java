package no.sr.ringo.response;

import no.sr.ringo.message.MessageWithLocations;

import java.util.List;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public interface MessageQueryRestResponse extends RestResponse {

    List<MessageWithLocations> getMessageList();

    Navigation getNavigation();
}

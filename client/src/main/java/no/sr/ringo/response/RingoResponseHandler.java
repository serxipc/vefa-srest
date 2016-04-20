package no.sr.ringo.response;

import org.apache.http.client.ResponseHandler;

/**
 * Implementations are responsible for extracting the expected object from the HttpResponse.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 * @author andy andy@sendregning.no
 */
public interface RingoResponseHandler<T> extends ResponseHandler<T> {

}

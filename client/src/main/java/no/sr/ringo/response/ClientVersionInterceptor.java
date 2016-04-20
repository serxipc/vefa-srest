package no.sr.ringo.response;

import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.RingoLoggingStream;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Logs a message if the version is detected.
 *
 * This is an implementation of the Apache Http Client interceptor interface.
 * 
 * User: andy
 * Date: 2/24/12
 * Time: 4:43 PM
 */
public class ClientVersionInterceptor implements HttpResponseInterceptor {
    static final Logger log = LoggerFactory.getLogger(ClientVersionInterceptor.class);

    private final RingoLoggingStream ringoLoggingStream;

    public ClientVersionInterceptor(RingoLoggingStream ringoLoggingStream) {
        this.ringoLoggingStream = ringoLoggingStream;
    }

    /**
     * Processes a response.
     * On the server side, this step is performed before the response is
     * sent to the client. On the client side, this step is performed
     * on incoming messages before the message body is evaluated.
     *
     * @param response the response to postprocess
     * @param context  the context for the request
     * @throws org.apache.http.HttpException in case of an HTTP protocol violation
     * @throws java.io.IOException           in case of an I/O error
     */
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {

        if (response.containsHeader(RingoConstants.CLIENT_DOWNLOAD_HEADER)) {
            ringoLoggingStream.println("New version of ringo client is available for download at: " + response.getLastHeader(RingoConstants.CLIENT_DOWNLOAD_HEADER).getValue());
        }
    }
}

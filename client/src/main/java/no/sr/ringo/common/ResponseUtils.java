package no.sr.ringo.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 1:11 PM
 */
public class ResponseUtils {
    static final Logger log = LoggerFactory.getLogger(ResponseUtils.class);
    
    /**
     * Writes the contents of the response to a String
     *
     * @param response
     * @param defaultCharset if content encoding is not specified this value will be used.
     * @return
     * @throws IOException
     */
    public static String writeResponseToString(HttpResponse response, String defaultCharset) throws IOException {
        //read the response into memory
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity,defaultCharset);
    }

    public static String writeResponseToString(HttpResponse response) throws IOException {
        return writeResponseToString(response, RingoConstants.DEFAULT_CHARACTER_SET);
    }
}

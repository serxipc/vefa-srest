package no.sr.ringo.resource;

import com.google.inject.Inject;
import no.sr.ringo.common.RingoConstants;
import no.sr.ringo.common.SoftwareVersionNumber;
import no.sr.ringo.guice.ClientVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper which will determine if a given version number is out of date with respect to another version number.
 *
 *
 *
 * User: andy
 * Date: 2/24/12
 * Time: 3:31 PM
 */
public class ClientVersionHelper {
    static final Logger log = LoggerFactory.getLogger(ClientVersionHelper.class);

    private final String currentClientVersion;
    private final Pattern pattern = Pattern.compile(RingoConstants.USER_AGENT +" \\(Version:\\s?([^\\)]+)\\)");

    @Inject
    public ClientVersionHelper(@ClientVersion String clientVersion) {
        this.currentClientVersion = clientVersion;
    }

    public String getCurrentClientVersion() {
        return currentClientVersion;
    }

    public boolean isOutOfDate(String userAgent) {
        if(log.isDebugEnabled()) {
            log.debug("Checking user agent: " + userAgent);
        }
        boolean result = false;
        
        if (userAgent == null) {
            return result;
        }
        final Matcher matcher = pattern.matcher(userAgent);
        if (matcher.matches()) {
            final String versionNumber = matcher.group(1);
            if(log.isDebugEnabled()) {
                log.debug("Detected SendRegning client");
            }
            SoftwareVersionNumber server = new SoftwareVersionNumber(currentClientVersion);
            SoftwareVersionNumber client = new SoftwareVersionNumber(versionNumber);
            result = server.isOutOfDate(client);
        }
        if(log.isDebugEnabled()) {
            String msg = result ? "Client is out of date" : "Client is not out of date";
            log.debug(msg);
        }
        return result;
    }



}

package no.sr.ringo.standalone.parser;

import no.sr.ringo.common.ProxySettings;
import no.sr.ringo.peppol.PeppolChannelId;
import no.sr.ringo.peppol.PeppolParticipantId;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URI;

/**
 * Holds RingoClient execution params parsed from command line arguments
 * <p/>
 * User: adam
 * Date: 1/27/12
 * Time: 8:08 AM
 */
public class RingoClientConnectionParams {

    //connection username
    private String username;

    //connection password
    private String password;

    //base URI for access point
    private URI accessPointURI;

    private String proxyAddress;

    private Integer proxyPort;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public URI getAccessPointURI() {
        return accessPointURI;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccessPointURI(URI accessPointURI) {
        this.accessPointURI = accessPointURI;
    }

    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public ProxySettings getProxySettings() {
        ProxySettings result = null;
        if (StringUtils.isNotBlank(proxyAddress) && proxyPort != null) {
            result = new ProxySettings(proxyAddress, proxyPort);
        }
        return result;
    }
}

package no.sr.ringo.common;

/**
 * Class holding settings for proxy
 * User: Adam
 * Date: 6/13/12
 * Time: 4:20 PM
 */
public class ProxySettings {

    private String address;
    private Integer port;

    public ProxySettings(String proxyAddress, Integer proxyPort) {
        this.address = proxyAddress;
        this.port = proxyPort;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }
}

package no.sr.ringo.request;

import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static javax.net.ssl.SSLContext.getInstance;

/**
 * SocketFactory which is only used when testing so that
 * when running against a local Ringo SSL will work even though
 * the certificate our server serves is not valid.
 */
public class TestSocketFactory extends SSLSocketFactory {

    public TestSocketFactory(URI baseUri) throws NoSuchAlgorithmException, KeyManagementException {
        super(getLocalhostContext(), new LocalhostX509HostnameVerifier(baseUri));
    }

    /**
     * When running against a local Ringo we need SSL to work even though
     * the certificate our server serves is not valid.
     */
    private static class LocalhostX509HostnameVerifier extends AbstractVerifier {
        private final URI baseUri;
        public LocalhostX509HostnameVerifier(URI baseUri) {
            this.baseUri = baseUri;
        }

        public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
            if (host.equals(baseUri.getHost())) {
                return;
            }
            super.verify(host, cns, subjectAlts, true);
        }
    }

    private static SSLContext getLocalhostContext() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext ctx = SSLContext.getInstance("TLS");
        X509TrustManager tm = new X509TrustManager() {

                    public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }

                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
        ctx.init(null, new TrustManager[]{tm}, null);
        return ctx;

    }
}

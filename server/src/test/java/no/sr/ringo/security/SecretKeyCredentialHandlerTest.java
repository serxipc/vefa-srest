package no.sr.ringo.security;

import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;

import static org.testng.Assert.*;

/**
 * @author steinar
 *         Date: 11.10.2016
 *         Time: 12.35
 */
public class SecretKeyCredentialHandlerTest {


    static final String CREDENTIAL = "ringo1";

    @Test
    public void testMatches() throws Exception {
        SecretKeyCredentialHandler sh = new SecretKeyCredentialHandler();
        String hashedCredentials = sh.mutate(CREDENTIAL);
        System.out.println(hashedCredentials);
        assertTrue(sh.matches("ringo1", hashedCredentials), "Input credentials does not match previously calculated credentials.");
    }

    @Test
    public void testDigestSh() throws NoSuchAlgorithmException {
        SecretKeyCredentialHandler sh = new SecretKeyCredentialHandler();

        // this string was generated using:
        // bin/digest.sh -a PBKDF2WithHmacSHA1 -h org.apache.catalina.realm.SecretKeyCredentialHandler ringo1
        boolean matches = sh.matches("ringo1", "9f8036bc7c88af1578082b0edd154fb3ed7ba3c2ed3605e094e7c2808ac94322$20000$dac6ceac850f9e8e4a48a263164f0593f6bce0f0");
        assertTrue(matches);

    }

}
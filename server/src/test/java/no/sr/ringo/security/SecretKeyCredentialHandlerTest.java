package no.sr.ringo.security;

import org.testng.annotations.Test;

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

        assertTrue(sh.matches("ringo1", hashedCredentials), "Input credentials does not match previously calculated credentials.");
    }

}
package no.sr.ringo.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.auth.BasicScheme;
import org.testng.annotations.Test;

import java.net.URL;

/**
 * @author steinar
 *         Date: 04.05.2016
 *         Time: 12.36
 */
public class Base64Test {

    @Test
    public void testBase64() throws Exception {

        // Figures out where the class was loaded from.
        URL loc1 = Base64.class.getProtectionDomain().getCodeSource().getLocation();

        URL loc2 = BasicScheme.class.getProtectionDomain().getCodeSource().getLocation();

        System.out.println("Base64.class :" + loc1);

        System.out.println("BasicScheme.class : " + loc2);

    }
}

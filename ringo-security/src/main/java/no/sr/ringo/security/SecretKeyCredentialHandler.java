package no.sr.ringo.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * This code has been borrowed from Apache Tomcat version 9.0.0.M10 in order to prevent us from declaring
 * a hard dependency on Tomcat artifacts.
 *
 * @author steinar
 *         Date: 11.10.2016
 *         Time: 12.04
 */
public class SecretKeyCredentialHandler extends AbstractCredentialHandler {

    public static final String DEFAULT_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int DEFAULT_KEY_LENGTH = 160;
    public static final int DEFAULT_ITERATIONS = 20000;


    private SecretKeyFactory secretKeyFactory;
    private int keyLength = DEFAULT_KEY_LENGTH;


    public SecretKeyCredentialHandler() throws NoSuchAlgorithmException {
        setAlgorithm(DEFAULT_ALGORITHM);
    }


    @Override
    public String getAlgorithm() {
        return secretKeyFactory.getAlgorithm();
    }


    @Override
    public void setAlgorithm(String algorithm) throws NoSuchAlgorithmException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        this.secretKeyFactory = secretKeyFactory;
    }


    public int getKeyLength() {
        return keyLength;
    }


    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }


    @Override
    public boolean matches(String inputCredentials, String storedCredentials) {
        return matchesSaltIterationsEncoded(inputCredentials, storedCredentials);
    }


    @Override
    protected String mutate(String inputCredentials, byte[] salt, int iterations) {
        KeySpec spec = new PBEKeySpec(inputCredentials.toCharArray(), salt, iterations, getKeyLength());

        try {
            SecretKey secretKey = secretKeyFactory.generateSecret(spec);
            return HexUtils.toHexString(secretKey.getEncoded());
        } catch (InvalidKeySpecException e) {
            log.warn("pbeCredentialHandler.invalidKeySpec", e);
            return null;
        }
    }


    @Override
    protected int getDefaultIterations() {
        return DEFAULT_ITERATIONS;
    }
}

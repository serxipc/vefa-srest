
package no.sr.ringo.security;

/**
 * @author steinar
 *         Date: 11.10.2016
 *         Time: 12.01
 */
public interface CredentialHandler {

    /**
     * Checks to see if the input credentials match the stored credentials
     *
     * @param inputCredentials  User provided credentials
     * @param storedCredentials Credentials previously stored
     *
     * @return <code>true</code> if the inputCredentials match the
     *         storedCredentials, otherwise <code>false</code>
     */
    boolean matches(String inputCredentials, String storedCredentials);

    /**
     * Generates the equivalent stored credentials for the given input
     * credentials.
     *
     * @param inputCredentials  User provided credentials
     *
     * @return  The equivalent stored credentials for the given input
     *          credentials
     */
    String mutate(String inputCredentials);

}

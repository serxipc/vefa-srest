package no.sr.ringo.response.exception;

import java.io.IOException;

/**
 * The username and password provided were invalid.
 * User: andy
 * Date: 2/23/12
 * Time: 3:09 PM
 */
public class BadCredentialsException extends IOException {
    private final int statusCode;

    public BadCredentialsException(int statusCode) {
        this.statusCode = statusCode;
    }
}

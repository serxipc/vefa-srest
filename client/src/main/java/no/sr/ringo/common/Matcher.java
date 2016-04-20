package no.sr.ringo.common;


/**
 * Matcher Pattern.
 * User: andy
 * Date: 1/20/12
 * Time: 3:08 PM
 */
public interface Matcher<T> {
    /**
     * Returns true if the object matches
     * @param object
     * @return true if the object matches false otherwise
     */
    boolean matches(T object);
}

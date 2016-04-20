package no.sr.ringo.common;

import org.apache.http.client.methods.HttpRequestBase;

import java.util.regex.Pattern;

/**
 * Matches a URI based on a regular expression
 * User: andy
 * Date: 1/25/12
 * Time: 1:05 PM
 */
public class UriRegExpMatcher implements Matcher<HttpRequestBase> {
    private final Pattern pattern;


    public UriRegExpMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean matches(HttpRequestBase currentHttpRequest) {
        String currentPath = currentHttpRequest.getURI().getPath();
        java.util.regex.Matcher matcher = pattern.matcher(currentPath);
        return matcher.matches();
    }


}

/* Created by steinar on 06.01.12 at 13:55 */
package no.sr.ringo.common;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Matches the uri provided with one in a given HttpRequest
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class UriMatcher implements Matcher<HttpRequestBase> {
    private final String expectedPath;
    private final Class<? extends HttpRequestBase> expectedRequestClass;

    public UriMatcher(String expectedPath, Class<? extends HttpRequestBase> expectedRequestClass) {
        this.expectedPath = expectedPath;
        this.expectedRequestClass = expectedRequestClass;
    }

    public boolean matches(HttpRequestBase currentHttpRequest) {
        if(!currentHttpRequest.getClass().equals(expectedRequestClass)){
            return false;
        }

        String currentPath = currentHttpRequest.getURI().getPath();
        if (currentPath.endsWith("/")) {
            return currentHttpRequest.getURI().getPath().endsWith(expectedPath + "/");
        }
        else {
            return currentHttpRequest.getURI().getPath().endsWith(expectedPath);
        }
    }
}

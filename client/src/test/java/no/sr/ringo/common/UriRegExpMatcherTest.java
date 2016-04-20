package no.sr.ringo.common;

import org.apache.http.client.methods.HttpRequestBase;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Checks the UriRegExpMatcher matches against the URIs path
 *
 * User: andy
 * Date: 1/30/12
 * Time: 11:06 AM
 */
public class UriRegExpMatcherTest {
    
    @Test
    public void testMatches() throws Exception {

        UriRegExpMatcher matcher = new UriRegExpMatcher(Pattern.compile("^\\/abcd.efgh$"));
        HttpRequestBase mockRequest = EasyMock.createStrictMock(HttpRequestBase.class);
        
        expect(mockRequest.getURI()).andStubReturn(new URI("http://www.fishing.com/abcd1efgh"));

        replay(mockRequest);

        final boolean matches = matcher.matches(mockRequest);

        verify(mockRequest);

        assertTrue(matches);
    }

    @Test
    public void testNotMatches() throws Exception {

        UriRegExpMatcher matcher = new UriRegExpMatcher(Pattern.compile("^\\/abcd.efgh$"));
        HttpRequestBase mockRequest = EasyMock.createStrictMock(HttpRequestBase.class);

        expect(mockRequest.getURI()).andStubReturn(new URI("http://www.fishing.com/somethingelse"));

        replay(mockRequest);

        final boolean matches = matcher.matches(mockRequest);

        verify(mockRequest);

        assertFalse(matches);
    }
}

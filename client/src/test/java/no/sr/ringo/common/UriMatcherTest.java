package no.sr.ringo.common;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import java.net.URI;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 11:14 AM
 */
public class UriMatcherTest {
    @Test
    public void testMatches() throws Exception {

        HttpRequestBase mockRequest = EasyMock.createStrictMock(HttpRequestBase.class);

        UriMatcher matcher = new UriMatcher("/abcd1efgh", mockRequest.getClass());

        expect(mockRequest.getURI()).andStubReturn(new URI("http://www.fishing.com/abcd1efgh"));

        replay(mockRequest);

        final boolean matches = matcher.matches(mockRequest);

        verify(mockRequest);

        assertTrue(matches);

    }

    @Test
    public void testNotMatchesDueToMethodType() throws Exception {

        HttpRequestBase mockRequest = EasyMock.createStrictMock(HttpRequestBase.class);

        UriMatcher matcher = new UriMatcher("/abcd1efgh", HttpPost.class);

        expect(mockRequest.getURI()).andStubReturn(new URI("http://www.fishing.com/abcd1efgh"));

        replay(mockRequest);

        final boolean matches = matcher.matches(mockRequest);

        verify(mockRequest);

        assertFalse(matches);

    }

    @Test
    public void testNotMatchesDueToPath() throws Exception {

        HttpRequestBase mockRequest = EasyMock.createStrictMock(HttpRequestBase.class);

        UriMatcher matcher = new UriMatcher("/somethingelse", HttpPost.class);

        expect(mockRequest.getURI()).andStubReturn(new URI("http://www.fishing.com/abcd1efgh"));

        replay(mockRequest);

        final boolean matches = matcher.matches(mockRequest);

        verify(mockRequest);

        assertFalse(matches);

    }
}

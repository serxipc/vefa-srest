package no.sr.ringo.response;

import no.sr.ringo.response.xml.XmlRingoResponseHandler;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * User: andy
 * Date: 10/26/12
 * Time: 9:05 AM
 */
public class NoContentXmlResponseParserTest {


    private XmlRingoResponseHandler xmlResponseHandler;
    private NoContentXmlResponseParser parser;

    @BeforeMethod
    public void setUp() throws Exception {
        xmlResponseHandler = EasyMock.createStrictMock(XmlRingoResponseHandler.class);
        parser = new NoContentXmlResponseParser(xmlResponseHandler);
    }

    @Test
    public void testParse() throws Exception {

        expect(xmlResponseHandler.resolve(parser)).andStubReturn(null);
        replay(xmlResponseHandler);

        Object parse = parser.parse(noContent());

        assertNull(parse);
        verify(xmlResponseHandler);
    }

    @Test
    public void testSelectList() throws Exception {
        List result = parser.selectList(null);
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testSelectEntity() throws Exception {
        Object result = parser.selectEntity(null);
        assertNull(result);
    }

    private InputStream noContent() {
        return new ByteArrayInputStream(new byte[0]);
    }

}

package no.sr.ringo.common;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: andy
 * Date: 1/30/12
 * Time: 11:28 AM
 */
public class XmlHelperTest {

    private XmlHelper<String> xmlHelper;

    @BeforeMethod
    protected void setUp() throws Exception {
        xmlHelper = new XmlHelper<String>( new XmlSpecification<String>() {
            public String getName() {
                return "test";
            }
            public String getXPath() {
                return "//message";
            }
            public String extractEntity(Element element) throws Exception {
                return element.getText();
            }
        });
    }

    @Test
    public void testSelectList() throws Exception {

        InputStream inputStream = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><messages><message>test</message><message>test2</message></messages>".getBytes(RingoConstants.DEFAULT_CHARACTER_SET));
        Document doc = new SAXBuilder().build(inputStream);

        final List<String> strings = xmlHelper.selectList(doc);

        assertNotNull(strings);
        assertEquals(strings.get(0), "test");
        assertEquals(strings.get(1),"test2");
    }

    @Test
    public void testSelectSingleNode() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><messages><message>test</message><message>test2</message></messages>".getBytes(RingoConstants.DEFAULT_CHARACTER_SET));
        Document doc = new SAXBuilder().build(inputStream);

        final String string = xmlHelper.selectSingle(doc);

        assertNotNull(string);
        assertEquals(string, "test");
    }

}

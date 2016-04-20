package no.sr.ringo.peppol;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * User: andy
 * Date: 10/3/12
 * Time: 12:50 PM
 */
public class LocalNameTest {


    @Test
    public void testCreateALocalName() throws Exception {
        LocalName localName = LocalName.valueOf("Invoice");
        assertNotNull(localName);
    }

    @Test
    public void testEquals() throws Exception {
        LocalName localName = LocalName.valueOf("Invoice");
        LocalName localName2 = LocalName.valueOf("Invoice");
        Assert.assertEquals(localName, localName2);
    }

    @Test
    public void testEqualsRespectsCase() throws Exception {
        LocalName localName = LocalName.valueOf("Invoice");
        LocalName localName2 = LocalName.valueOf("invoice");
        Assert.assertNotEquals(localName, localName2);
    }

}

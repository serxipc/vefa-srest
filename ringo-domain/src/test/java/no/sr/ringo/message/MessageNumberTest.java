package no.sr.ringo.message;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: andy
 * Date: 10/5/12
 * Time: 1:33 PM
 */
public class MessageNumberTest {

    @Test
    public void testValidMessageNumber() throws Exception {
        MessageNumber messageNumber = MessageNumber.create(1);
        assertNotNull(messageNumber);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInValidMessageNumber() throws Exception {
        MessageNumber.create(-1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInValidMessageNumberZero() throws Exception {
        MessageNumber.create(0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInValidMessageNumberNull() throws Exception {
        MessageNumber.create((Long)null);
    }

    @Test
    public void testEqualsMessageNumber() throws Exception {
        MessageNumber messageNumber1 = MessageNumber.create(1);
        MessageNumber messageNumber2 = MessageNumber.create(1);
        assertEquals(messageNumber1, messageNumber2);
    }

    @Test
    public void testToString() throws Exception {
        MessageNumber messageNumber1 = MessageNumber.create(1);
        assertEquals(messageNumber1.toString(),"1");
    }

    @Test
    public void testValidValueOf() throws Exception {
        MessageNumber messageNumber = MessageNumber.valueOf("1");
        assertNotNull(messageNumber);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInvalidValueOfException() throws Exception {
        MessageNumber.valueOf("a");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testEmptyStringValueOfException() throws Exception {
        MessageNumber.valueOf("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullStringValueOfException() throws Exception {
        MessageNumber.valueOf(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValidNumberInvalidMessageNumberException() throws Exception {
        MessageNumber.valueOf("-1");
    }

    @Test
    public void testToInt() throws Exception {
        MessageNumber messageNumber = MessageNumber.create(1);
        assertEquals(new Integer(1), messageNumber.toInt());
    }
}

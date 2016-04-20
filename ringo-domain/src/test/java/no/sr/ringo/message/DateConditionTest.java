package no.sr.ringo.message;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * User: andy
 * Date: 10/8/12
 * Time: 1:55 PM
 */
public class DateConditionTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExceptionWhenInvalidCondition() throws Exception {
        MessageSearchParams.DateCondition.fromString("xxx");
    }

    @Test()
    public void testValidConditions() throws Exception {
        assertNotNull(MessageSearchParams.DateCondition.fromString("<"));
        assertNotNull(MessageSearchParams.DateCondition.fromString(">"));
        assertNotNull(MessageSearchParams.DateCondition.fromString("="));
        assertNotNull(MessageSearchParams.DateCondition.fromString(">="));
        assertNotNull(MessageSearchParams.DateCondition.fromString("<="));
    }

    @Test
    public void testValueOf() throws Exception {
        MessageSearchParams.DateCondition condition = MessageSearchParams.DateCondition.valueOf("LESS");
        assertEquals(MessageSearchParams.DateCondition.LESS, condition);
    }

}

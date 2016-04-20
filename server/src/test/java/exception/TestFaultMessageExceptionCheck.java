package exception;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tools.ant.taskdefs.Pack;
import org.testng.annotations.Test;
import org.w3._2009._02.ws_tra.FaultMessage;

import java.util.List;

import static org.testng.Assert.*;

/**
 * The purpose of this test is to make sure that we can know what FaultMessage exception occured
 * regardless of whether it's the first exception or nested on some deeper level
 *
 * User: Adam
 * Date: 9/12/12
 * Time: 10:45 AM
 */
public class TestFaultMessageExceptionCheck {

    @Test
    public void testNestedExceptionCatching(){
        try{
            throwNestedException();
            fail();
        } catch(Exception e) {
            handleException(e);
        }
    }

    @Test
    /**
     * Test we can find the exception if it's the main cause
     */
    public void testNotNestedExceptionCatching(){
        try{
            throwNotNestedException();
            fail();
        } catch(Exception e) {
            handleException(e);
        }
    }

    /**
     * Test we can find the exception if it's nested somewhere inside another exception
     */
    private void handleException(Exception e) {
        int index = ExceptionUtils.indexOfType(e, FaultMessage.class);
        if (index >= 0 ) {
            FaultMessage fm = (FaultMessage) ExceptionUtils.getThrowableList(e).get(index);
            assertEquals("FaultMessageExample", fm.getMessage());
        } else {
            fail();
        }
    }

    private void throwNestedException() {
        //nest FaultMessage on 3rd level
        FaultMessage faultMessageException = new FaultMessage("FaultMessageExample", null);
        IllegalStateException ise = new IllegalStateException(faultMessageException);
        throw new RuntimeException(ise);
    }


    private void throwNotNestedException() throws FaultMessage {
        throw new FaultMessage("FaultMessageExample", null);
    }
}

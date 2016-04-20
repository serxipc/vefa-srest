/* Created by steinar on 15.05.12 at 23:06 */
package no.sr.ringo.common;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SoftwareVersionNumberTest {


    @Test
    public void testConstructionOfVersionNumber() {
        SoftwareVersionNumber softwareVersionNumber = new SoftwareVersionNumber("1.2.3-SNAPSHOT");
        assertEquals(softwareVersionNumber.getMajorRevision().intValue(), 1);
        assertEquals(softwareVersionNumber.getMinorRevision().intValue(),2);
        assertEquals(softwareVersionNumber.getIncrementalRevision().intValue(), 3);
        assertEquals(softwareVersionNumber.getQualifier(),"SNAPSHOT");
    }

    @Test
    public void testConstructionOfVersionNumberWithoutQualifier() {
        SoftwareVersionNumber softwareVersionNumber = new SoftwareVersionNumber("1.2.3");
        assertEquals(softwareVersionNumber.getMajorRevision().intValue(), 1);
        assertEquals(softwareVersionNumber.getMinorRevision().intValue(),2);
        assertEquals(softwareVersionNumber.getIncrementalRevision().intValue(), 3);
        assertNull(softwareVersionNumber.getQualifier());
    }

    @Test
    public void testConstructionOfVersionNumberWithoutIncrementalVersionAndQualifier() {
        SoftwareVersionNumber softwareVersionNumber = new SoftwareVersionNumber("1.2");
        assertEquals(softwareVersionNumber.getMajorRevision().intValue(), 1);
        assertEquals(softwareVersionNumber.getMinorRevision().intValue(),2);
        assertNull(softwareVersionNumber.getIncrementalRevision());
        assertNull(softwareVersionNumber.getQualifier());
    }

    @Test
    public void majorAndMinorAndQualifer() {
        SoftwareVersionNumber softwareVersionNumber = new SoftwareVersionNumber("1.2-SNAPSHOT");
        assertEquals(softwareVersionNumber.getMajorRevision().intValue(), 1);
        assertEquals(softwareVersionNumber.getMinorRevision().intValue(),2);
        assertNull(softwareVersionNumber.getIncrementalRevision());
        assertEquals(softwareVersionNumber.getQualifier(),"SNAPSHOT");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void invalid() {
        SoftwareVersionNumber softwareVersionNumber = new SoftwareVersionNumber("1-SNAPSHOT");
    }

    @Test
    public void diffIncrementalVersionNumbers() {
        SoftwareVersionNumber v1_2_3 = new SoftwareVersionNumber("1.2.3");
        SoftwareVersionNumber v1_2_4 = new SoftwareVersionNumber("1.2.4");
        SoftwareVersionNumber v1_1_6_SNAPSHOT = new SoftwareVersionNumber("1.1.26-SNAPSHOT");
    }

}



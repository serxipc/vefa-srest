package no.sr.ringo.common;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: Adam
 * Date: 2/20/12
 * Time: 12:58 PM
 */
public class FileHelperTest {

    @Test
    public void testFormatForFileName() throws Exception {

        String uuid = "uuid:12345-456:*/dsaa:aaa";
        assertEquals("uuid_12345-456___dsaa_aaa", FileHelper.formatForFileName(uuid));

        String participantId = "9908:976098897";
        assertEquals("9908_976098897", FileHelper.formatForFileName(participantId));
    }

    @Test
    public void testFileNames() throws IOException {
        String fileName = "123-456-789.xml";
        String secondFileName = "123-456-789_1.xml";

        //just delete the file if it exists in order to run the test
        new File(new File("/tmp"), fileName).delete();
        new File(new File("/tmp"), secondFileName).delete();

        //file doesn't exist, so method should return the same name
        assertEquals(fileName, FileHelper.checkFile(new File(""), fileName));

        //create the file
        new File(new File("/tmp"), fileName).createNewFile();
        //file exists, so expect the name with _1
        assertEquals(secondFileName, FileHelper.checkFile(new File("/tmp"), fileName));

        //create the secondFile
        new File(new File("/tmp"), secondFileName).createNewFile();

        //both files exists, so expect the name with _2
        assertEquals("123-456-789_2.xml", FileHelper.checkFile(new File("/tmp"), fileName));

    }

    @Test
    public void testGetProperties() throws Exception {
        final Properties properties = FileHelper.fetchProperties("/client.properties");
        assertNotNull(properties.getProperty("client.version"));
    }
}

package no.sr.ringo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: Adam
 * Date: 2/20/12
 * Time: 12:53 PM
 */
public class FileHelper {

    private static Logger log = LoggerFactory.getLogger(FileHelper.class);

    /**
     * Replaces all special characters with underscore
     */
    public static String formatForFileName(String value) {
        return value.replaceAll("([^\\d\\w-])", "_");
    }

    /**
     * Checks if file with given filename already exists, if it does, modified it by adding a number
     */
    public static String checkFile(File directory, String filename) {

        // if file with given name exists, try to add "_<number>" before ".xml"
        int i = 1;
        File file = new File(directory, filename);
        while (file.exists()) {

            if (log.isDebugEnabled()) {
                log.debug(String.format("File with name %s already exists, trying to add number at the end of file", filename));
            }

            filename = file.getName();
            if (i == 1) {
                filename = filename.replace(".xml", "_1.xml");
            } else {
                filename = filename.replaceAll("_\\d.xml", "_" + i + ".xml");
            }
            i++;
            file = new File(directory, filename);
        }

        //log the message if name has changed
        if (i > 1 && log.isDebugEnabled()) {
            log.debug(String.format("Saving file with new name: %s ", filename));
        }

        return file.getName();

    }

    public static Properties fetchProperties(String resource) throws IOException {
    
        InputStream is = FileHelper.class.getResourceAsStream(resource);
        Properties prop = new Properties();
        try{
            prop.load(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        return prop;
    }
}

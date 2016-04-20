package no.sr.ringo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper methods
 * User: Andy
 */
public class PropertyHelper {

    private static Logger log = LoggerFactory.getLogger(PropertyHelper.class);

    public static Properties fetchProperties(String resource) throws IOException {
    
        InputStream is = PropertyHelper.class.getResourceAsStream(resource);
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

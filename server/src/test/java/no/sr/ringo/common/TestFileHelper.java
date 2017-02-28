/* Created by steinar on 15.01.12 at 13:06 */
package no.sr.ringo.common;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class TestFileHelper {

    protected static final String EHF_TEST_SEND_REGNING_HELSE_VEST2_XML = "ehf-test-SendRegning-HelseVest2.xml";

    /**
     * Given the fact that we are using the standard maven layout, any file located in either src/main/resources or
     * src/test/resources, will be placed into target/classes or target/test-classes after compilation, thus
     * making it available on the classpath.
     *
     * This method will return the root directory of the project by locating a given file amongst the resources, which have been moved
     * during compilation, after which one simply goes ../.. to find the project root.
     *
     * @return File object referencing the project root.
     */
    public static File locateProjectRoot() {
        URL url = TestFileHelper.class.getClassLoader().getResource(EHF_TEST_SEND_REGNING_HELSE_VEST2_XML);
        if (url == null) {
            throw new IllegalStateException("Unable to locate test file " + EHF_TEST_SEND_REGNING_HELSE_VEST2_XML);
        }

        try {
            File result = new File(url.toURI());
            File projectRoot = result.getParentFile().getParentFile().getParentFile();
            return projectRoot;
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to of URI from URI " + url, e);
        }
    }

    /**
     * Provides the absolute path to the WEB-INF/web.xml file in the src/main/webapp directory.
     * 
     * @return File referencing the source instance of web.xml
     */
    public static File sourcePath(String pathRelativeToProjectRoot){
        File projectRoot = locateProjectRoot();

        File result = new File(projectRoot, pathRelativeToProjectRoot);
        return result;
    }
    
    public static void main(String[] args) {
        System.err.println("CWD " + locateProjectRoot());
        System.err.format("web.xml path: %s\n", sourcePath("src/main/webapp/WEB-INF/web.xml"));
    }
}

package no.sr.ringo.guice;

import com.google.inject.servlet.ServletModule;
import no.sr.ringo.servlet.UploadServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is where we configure our servlets...
 *
 * This has nothing to do with the REST API
 *
 * User: andy
 * Date: 1/19/12
 * Time: 11:43 AM
 */
public class RingoWebServletModule extends ServletModule {
    static final Logger log = LoggerFactory.getLogger(RingoWebServletModule.class);

    @Override
    protected void configureServlets() {
        serve("/upload/upload.do").with(UploadServlet.class);
    }

}

package no.sr.ringo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Custom printStream which now only prints the message but also logs it at info level.
 * User: Adam
 * Date: 2/23/12
 * Time: 10:14 AM
 */
public class RingoLoggingStream extends PrintStream {

    private static Logger log = LoggerFactory.getLogger(RingoLoggingStream.class);

    public RingoLoggingStream(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    public void println(String line) {
        log.info(line);
        super.println(line);
    }

    /**
     * Outputs an error message to the output stream and performs a log.error
     * @param e the exception which caused the error
     */
    public void error(Throwable e) {
        //only log the stack trace if debug is enabled
        if(log.isDebugEnabled()){
            log.debug(e.getMessage(), e);
        }
        else {
            log.error(e.getMessage());
        }
        super.println(e.getMessage());
    }
}

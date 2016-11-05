package no.sr.ringo.parser;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * User: adam
 * Date: 3/9/13
 * Time: 2:42 PM
 */
public class CommandLineParserTest {

    @Test
    public void testParse() throws Exception {
        ParserResult params = CommandLineParser.parse(new String [] { "-t", "single", "-q", "1", "-s", "false"});
        Assert.assertEquals(new ParserResult(ParserResult.PROCESSING_TYPE.SINGLE, 1,  false), params);

        params = CommandLineParser.parse(new String [] { "-t", "all",  "-s", "true"});
        Assert.assertEquals(new ParserResult(ParserResult.PROCESSING_TYPE.ALL,  null,  true), params);

    }
}

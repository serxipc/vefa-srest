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
        ParserResult params = CommandLineParser.parse(new String [] {"-h", "localhost",  "-u", "user",  "-p", "pass", "-d", "oxalis_test", "-t", "single", "-q", "1", "-k", "keystore", "-s", "false"});
        Assert.assertEquals(new ParserResult(ParserResult.PROCESSING_TYPE.SINGLE, "user", "pass", "localhost", "oxalis_test", 1, new File("keystore"), false), params);

        params = CommandLineParser.parse(new String [] {"-h", "localhost",  "-u", "user",  "-p", "pass", "-d", "oxalis_test", "-t", "all", "-k", "keystore", "-s", "true"});
        Assert.assertEquals(new ParserResult(ParserResult.PROCESSING_TYPE.ALL, "user", "pass", "localhost", "oxalis_test", null, new File("keystore"), true), params);

    }
}

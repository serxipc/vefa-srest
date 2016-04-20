package no.sr.ringo.parser;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * User: adam
 * Date: 3/9/13
 * Time: 2:05 PM
 */
public class CommandLineParser {

    static final Logger log = LoggerFactory.getLogger(CommandLineParser.class);

    private static OptionSpec<File> keystore;
    private static OptionSpec<String> processingType;
    private static OptionSpec<Integer> queueId;

    private static OptionSpec<String> dbHost;
    private static OptionSpec<String> dbUser;
    private static OptionSpec<String> dbPass;
    private static OptionSpec<String> dbName;

    private static OptionSpec<Boolean> production;

    public static ParserResult parse(String[] args) throws IOException {

        OptionParser optionParser = getOptionParser();

        if (args.length == 0) {
            log.info("");
            optionParser.printHelpOn(System.out);
            log.info("");
            throw new IllegalArgumentException("Arguments are required");
        }

        OptionSet optionSet;

        try {
            optionSet = optionParser.parse(args);
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
            throw new IllegalArgumentException("Exception occurred when trying to parse args");
        }

        ParserResult.PROCESSING_TYPE type = ParserResult.PROCESSING_TYPE.valueOf(processingType.value(optionSet).toUpperCase());

        Integer queueId = null;
        if (ParserResult.PROCESSING_TYPE.SINGLE.equals(type)){
            if (!optionSet.has(CommandLineParser.queueId)) {
                printErrorMessage("-q (queueId) required for single processing type");
            }
            queueId = CommandLineParser.queueId.value(optionSet);
            if (queueId == null) {
                printErrorMessage("Value for -q required");
                throw new IllegalArgumentException("Value for -q required");
            }
        }

        return new ParserResult(type, dbUser.value(optionSet), dbPass.value(optionSet), dbHost.value(optionSet), dbName.value(optionSet), queueId, keystore.value(optionSet), production.value(optionSet));
    }

    private static OptionParser getOptionParser() {
        OptionParser optionParser = new OptionParser();
        queueId = optionParser.accepts("q", "queueId to process").withRequiredArg().ofType(Integer.class);
        processingType = optionParser.accepts("t", "Processing type: ALL or SINGLE").withRequiredArg().ofType(String.class).required();

        dbHost = optionParser.accepts("h", "Database host").withRequiredArg().ofType(String.class).required();
        dbUser = optionParser.accepts("u", "Database user").withRequiredArg().ofType(String.class).required();
        dbPass = optionParser.accepts("p", "Database password").withRequiredArg().ofType(String.class).required();
        dbName = optionParser.accepts("d", "Database name").withRequiredArg().ofType(String.class).required();
        keystore = optionParser.accepts("k", "Keystore file").withRequiredArg().ofType(File.class).required();

        production = optionParser.accepts("s", "Production server").withRequiredArg().ofType(Boolean.class).required();
        return optionParser;
    }


    private static void printErrorMessage(String message) {
        log.info("");
        log.info("*** " + message);
        log.info("");
    }

}

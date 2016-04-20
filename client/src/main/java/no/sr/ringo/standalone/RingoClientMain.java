package no.sr.ringo.standalone;

import no.sr.ringo.client.ClientErrorNotifier;
import no.sr.ringo.client.ErrorNotificationData;
import no.sr.ringo.client.RingoClient;
import no.sr.ringo.client.RingoClientImpl;
import no.sr.ringo.common.RingoLoggingStream;
import no.sr.ringo.exception.NotifyingException;
import no.sr.ringo.response.exception.UnexpectedRestResponse;
import no.sr.ringo.standalone.executor.RingoClientCommandExecutor;
import no.sr.ringo.standalone.parser.RingoClientCommandLineParser;
import no.sr.ringo.standalone.parser.RingoClientConnectionParams;
import no.sr.ringo.standalone.parser.RingoClientParams;
import org.apache.commons.lang.StringUtils;

import static no.sr.ringo.client.ClientErrorNotifier.*;

/**
 * Main entry point to ringo client
 * <p/>
 * User: adam
 * Date: 1/27/12
 * Time: 8:07 AM
 */
public class RingoClientMain {

    static ClientErrorNotifier errorNotifier = new ClientErrorNotifier();
    final static RingoLoggingStream printStream = new RingoLoggingStream(System.out);

    public static void main(String[] args) {
        RingoClientConnectionParams connectionParams = null;
        RingoClientParams params = null;

        String argsString = "";
        for (final String s : args) {
            argsString = argsString.concat(" " + s);
        }

        printStream.println("Starting Ringo client with arguments:" + argsString);

        final RingoClientCommandLineParser parser = new RingoClientCommandLineParser();

        if (args.length == 0) {
            parser.usage();
            return;
        }

        try {
            parser.parseCommandLine(args);
            connectionParams = parser.extractConnectionParams();

            params = parser.extractOperationParams();

            final RingoClient ringoClientImpl = new RingoClientImpl(new DefaultRingoConfig(connectionParams.getAccessPointURI().toASCIIString(), connectionParams.getProxySettings()), connectionParams.getUsername(), connectionParams.getPassword());

            RingoClientCommandExecutor executor = new RingoClientCommandExecutor(printStream, params, ringoClientImpl);
            executor.execute();

            printStream.println("Ringo client complete");
            System.exit(0);

        } catch (NotifyingException e) {
            handleNotifyingException(args, e, connectionParams);
            printStream.error(e);
            System.exit(1);
        } catch (UnexpectedRestResponse unexpectedRestResponse) {
            printStream.error(unexpectedRestResponse);
            System.exit(1);
        } catch (Throwable e) {
            printStream.error(e);
            System.exit(1);
        }

    }

    private static void handleNotifyingException(String[] commandLine, NotifyingException e, RingoClientConnectionParams connectionParams) {

        try {
            if (e != null && e.isNotify()) {
                ErrorNotificationData errorNotificationData = new ErrorNotificationData(StringUtils.join(commandLine, " "), e.getNotificationType(), e.getMessage());
                errorNotifier.sendErrorNotification(errorNotificationData, connectionParams);
            }
        } catch (Exception exception) {
            printStream.print("Error occurred when sending error notification: " + e.getMessage());
        }

    }

    //for test purposes
    protected static void injectErrorNotifier(ClientErrorNotifier errorNotifier_) {
        errorNotifier = errorNotifier_;
    }
}

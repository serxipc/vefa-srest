package no.sr.ringo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.peppol.persistence.guice.OxalisDataSourceModule;
import eu.peppol.persistence.guice.RepositoryModule;
import eu.peppol.persistence.queue.OutboundMessageQueueId;
import eu.peppol.smp.SmpModule;
import eu.peppol.util.OxalisProductionConfigurationModule;
import no.sr.ringo.guice.RingoServiceModule;
import no.sr.ringo.parser.CommandLineParser;
import no.sr.ringo.parser.ParserResult;
import no.sr.ringo.usecase.QueuedMessageSenderResult;
import no.sr.ringo.usecase.SendQueuedMessagesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone app for processing queued outbound messages
 */
public class Main {

    static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Example usage:
     *  -h localhost -u<dbUser> -p<dbPass> -d oxalis_test -t single -m 911 -k /path/to/keystore.jks
     *  -h localhost -u<dbUser> -p<dbPass> -d oxalis_test -t all -k /path/to/keystore.jks
     */
    public static void main(String[] args) throws Exception {

        ParserResult params = null;
        try {
            params = CommandLineParser.parse(args);
        } catch (Exception e) {
            log.info("Exception occurred when parsing arguments: " + e.getMessage());
            System.exit(-1);
        }

        Injector injector = getInjector(params.isProduction());

        SendQueuedMessagesUseCase useCase = injector.getInstance(SendQueuedMessagesUseCase.class);
        QueuedMessageSenderResult result = null;

        switch (params.getProcessingType()) {
            case ALL:
                log.info("Processing all messages");
                result = useCase.handleAllQueuedMessages();
                break;
            case SINGLE:
                log.info("Processing single message " + params.getQueueId());
                result = useCase.handleSingleQueuedMessage(new OutboundMessageQueueId(params.getQueueId()));
                break;
        }

        if (result != null) {
            log.info(result.asXml());
        } else {
            log.error("Did not get a result from processing");
        }

        log.info("Application done!");

        System.exit(0);

    }

    static Injector getInjector(boolean isProduction) {
        return Guice.createInjector(
                    // Needs to be modified
                    // new RingoDataSourceGuiceModule(params.getDbHost(),params.getDbUser(),params.getDbPass(), params.getDbName()),
                    new RingoServiceModule(isProduction),
                    new SmpModule(),

                    new OxalisProductionConfigurationModule(),
                    new OxalisDataSourceModule(),
                    new RepositoryModule()

                );
    }
}

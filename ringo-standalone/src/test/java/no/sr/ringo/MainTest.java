package no.sr.ringo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.typesafe.config.Config;
import no.sr.ringo.config.RingoConfigModule;
import no.sr.ringo.usecase.SendQueuedMessagesUseCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 06.03.2017
 *         Time: 14.41
 */
public class MainTest {


    @Test
    public void testMain() throws Exception {

        final Injector injector = Guice.createInjector(new RingoConfigModule());
        assertNotNull(injector);

        final Config config = injector.getInstance(Config.class);
        assertNotNull(config);


    }

    @Test
    public void testGetInjector() throws Exception {

        final Injector injector = Main.getInjector();
        assertNotNull(injector);
        
        final SendQueuedMessagesUseCase instance = injector.getInstance(SendQueuedMessagesUseCase.class);


    }

}
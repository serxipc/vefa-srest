package no.sr.ringo;

import com.google.inject.Injector;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.Transmitter;
import eu.peppol.security.CommonName;
import eu.peppol.security.KeystoreManager;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author steinar
 *         Date: 04.11.2016
 *         Time: 08.58
 */
@Test(groups = {"integration"})
public class MainTest {

    @Test
    public void testGetInjector() throws Exception {
        Injector injector = Main.getInjector(true);
        OxalisOutboundModule oxalisOutboundModule = injector.getInstance(OxalisOutboundModule.class);
        assertNotNull(oxalisOutboundModule);
    }

}
package no.sr.ringo;

import com.google.inject.Injector;
import eu.peppol.outbound.OxalisOutboundComponent;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

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
        OxalisOutboundComponent oxalisOutboundModule = injector.getInstance(OxalisOutboundComponent.class);
        assertNotNull(oxalisOutboundModule);
    }

}
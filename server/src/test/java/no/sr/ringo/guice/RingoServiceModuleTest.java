package no.sr.ringo.guice;

import no.difi.ringo.UnitTestConfigModule;
import no.sr.ringo.document.DocumentRepository;
import no.sr.ringo.document.PeppolDocumentFactory;
import no.sr.ringo.persistence.jdbc.RingoRepositoryModule;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests the ringo service module.
 *
 * User: andy
 * Date: 1/19/12
 * Time: 11:41 AM
 */
@Guice(modules = {RingoServiceModule.class, UnitTestConfigModule.class,RingoRepositoryModule.class, ServerTestDataSourceModule.class, FakeScopesModule.class})
public class RingoServiceModuleTest {

    @Inject
    PeppolDocumentFactory documentFactory;
    @Inject
    PeppolDocumentFactory documentFactory2;
    @Inject
    DocumentRepository documentRepository;


    @Test
    public void testDocumentFactoryInjected() throws Exception {
        assertNotNull(documentFactory);
    }

    @Test
    public void testSingletons() throws Exception {
        assertTrue(documentFactory == documentFactory2);
    }

    @Test
    public void testFetchDocumentInjected() throws Exception {
        assertNotNull(documentRepository);
    }
}

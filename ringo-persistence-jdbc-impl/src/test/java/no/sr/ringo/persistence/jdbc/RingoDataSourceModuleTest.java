package no.sr.ringo.persistence.jdbc;

import com.google.inject.Inject;
import no.difi.ringo.UnitTestConfigModule;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import javax.sql.DataSource;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author steinar
 *         Date: 30.01.2017
 *         Time: 09.40
 */
@Guice(modules = {UnitTestConfigModule.class, RingoDataSourceModule.class})
public class RingoDataSourceModuleTest {

    @Inject
    DataSource dataSource;

    @Inject
    DataSource dataSource2;

    @Test
    public void testProvideDataSource() throws Exception {
        assertNotNull(dataSource);

        assertEquals(dataSource, dataSource2,"Seems singleton is not working somewhere");
    }

}
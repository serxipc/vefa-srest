/*
 * Copyright 2010-2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package eu.peppol.persistence.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import eu.peppol.persistence.RepositoryConfiguration;
import eu.peppol.persistence.RingoRepositoryModule;
import eu.peppol.persistence.test.InMemoryTestDatabaseModule;
import eu.peppol.util.GlobalConfiguration;
import no.sr.ringo.config.RingoConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IModuleFactory;
import org.testng.ITestContext;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestNG implementation of factory, which provides the Google Guice modules required
 * for running the tests.
 *
 * @author steinar
 *         Date: 16.10.2016
 *         Time: 19.44
 */
public class PersistenceTestModuleFactory implements IModuleFactory {

    public static final Logger log = LoggerFactory.getLogger(PersistenceTestModuleFactory.class);

    public static final String CREATE_OXALIS_DBMS_H2_SQL = "sql/create-oxalis-dbms-h2.sql";


    @Override
    public Module createModule(ITestContext iTestContext, Class<?> aClass) {

        String[] includedGroups = iTestContext.getIncludedGroups();

/*
        if (aClass.equals(OxalisDataSourceFactoryDbcpImplIT.class)) {
            return new TestWithoutInmemoryDatasource();
        } else
*/
            return new MemoryDatabaseModule();
    }


    private class MemoryDatabaseModule extends AbstractModule {
        @Override
        protected void configure() {

            bind(Path.class).annotatedWith(Names.named(RingoConfigProperty.PAYLOAD_BASE_PATH)).toInstance(Paths.get("/var/peppol"));
            binder().install(new RingoRepositoryModule());

            binder().install(new InMemoryTestDatabaseModule());
        }
    }

    /**
     * Guice memory module, which uses the globally configured data source.
     */
    class TestWithoutInmemoryDatasource extends AbstractModule {

        @Override
        protected void configure() {

            binder().install(new RingoRepositoryModule());
            binder().install(new OxalisDataSourceModule());

        }


        @Provides
        RepositoryConfiguration repositoryConfiguration(GlobalConfiguration c) {
            return new RepositoryConfiguration() {
                @Override
                public Path getBasePath() {
                    return Paths.get(c.getInboundMessageStore());
                }

                @Override
                public URI getJdbcConnectionUri() {
                    return URI.create(c.getJdbcConnectionURI());
                }

                @Override
                public String getJdbcDriverClassPath() {
                    return c.getJdbcDriverClassPath();
                }

                @Override
                public String getJdbcDriverClassName() {
                    return c.getJdbcDriverClassName();
                }

                @Override
                public String getJdbcUsername() {
                    return c.getJdbcUsername();
                }

                @Override
                public String getJdbcPassword() {
                    return c.getJdbcPassword();
                }

                @Override
                public String getValidationQuery() {
                    return c.getValidationQuery();
                }
            };
        }


    }

}

package no.sr.ringo.persistence;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.peppol.persistence.jdbc.RingoRepositoryModule;
import no.difi.ringo.UnitTestConfigModule;
import no.sr.ringo.guice.ServerTestDataSourceModule;
import no.sr.ringo.persistence.repo.TestRepo;
import no.sr.ringo.persistence.repo.TestRepoImpl;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

/**
 * Tests THE AOP JDBCTxManager...
 * User: andy
 * Date: 8/22/12
 * Time: 8:29 AM
 */

/**
 * Sets up guice with Test datasource and aop
 */
@Guice(modules = {ServerTestDataSourceModule.class, UnitTestConfigModule.class,ConnectionTest.ConnectionTestModule.class, RingoRepositoryModule.class})
public class ConnectionTest {

    @Inject TestRepo testRepo;

    /**
     * Just checks that making two calls after one another does not throw an exception
     * (n.b. debugging i can see the connection being closed after each call )
     */
    @Test(groups = {"persistence"})
    public void testGetAConnection(){
        testRepo.fetchFirstCustomer();
        testRepo.fetchFirstCustomerDifferentMethod();
    }

    /**
     * Calls a method which calls another method...
     * (n.b. debugging the connection should be reused)
     */
    @Test(groups = {"persistence"})
    public void testCallingNested(){
        testRepo.fetchCallingNested();
    }


    /**
     * Calls a method which is not transactional which calls a method which is transactional
     * this should throw an exception because the existing connection cannot be made transactional.
     */
    @Test(groups = {"persistence"})
    public void testCallingNestedTransactional() {
        try {
            testRepo.fetchCallingNestedTransactional();
            fail("Should have thrown an exception");
        } catch (Exception e) {
            //expected
        }
    }

    /**
     * Sets up GUICE for this test
     */
    public static class ConnectionTestModule extends AbstractModule {
        /**
         * Configures a {@link com.google.inject.Binder} via the exposed methods.
         */
        @Override
        protected void configure() {
            bind(TestRepo.class).to(TestRepoImpl.class).in(Singleton.class);
        }
    }

}

package no.sr.ringo.persistence.repo;

/**
 * Test repository for checking the GUICE AOP transactions etc.
 * User: andy
 * Date: 8/22/12
 * Time: 8:36 AM
 */
public interface TestRepo {
    public String fetchFirstCustomer();

    String fetchFirstCustomerDifferentMethod();

    String fetchCallingNested();

    String fetchCallingNestedTransactional();

    String fetchTransactionalCustomer();
}

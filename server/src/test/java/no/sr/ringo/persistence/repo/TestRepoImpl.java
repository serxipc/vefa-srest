package no.sr.ringo.persistence.repo;

import com.google.inject.Inject;
import no.sr.ringo.persistence.guice.jdbc.JdbcTxManager;
import no.sr.ringo.persistence.guice.jdbc.Repository;
import no.sr.ringo.persistence.guice.jdbc.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Test repository for checking the GUICE AOP transactions etc.
* User: andy
* Date: 8/22/12
* Time: 8:36 AM
*/
@Repository
public class TestRepoImpl implements TestRepo {

    private final JdbcTxManager jdbcTxManager;

    @Inject
    TestRepoImpl(JdbcTxManager jdbcTxManager) {
        this.jdbcTxManager = jdbcTxManager;
    }

    @Override
    public String fetchFirstCustomer() {
        final Connection connection = jdbcTxManager.getConnection();
        try {
            Connection con = jdbcTxManager.getConnection();
            final String sql = "select * from  customer limit 1";
            final PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            return "FAILED";
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    @Transactional
    public String fetchTransactionalCustomer() {
        final Connection connection = jdbcTxManager.getConnection();
        try {
            Connection con = jdbcTxManager.getConnection();
            final String sql = "select * from  customer limit 1";
            final PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            return "FAILED";
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String fetchFirstCustomerDifferentMethod() {
        final Connection connection = jdbcTxManager.getConnection();
        try {
            Connection con = jdbcTxManager.getConnection();
            final String sql = "select * from  customer limit 1";
            final PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            return "FAILED";
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String fetchCallingNested() {
        return fetchFirstCustomer();
    }

    @Override
    public String fetchCallingNestedTransactional() {
        return fetchTransactionalCustomer();
    }

}

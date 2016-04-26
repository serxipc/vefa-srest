package ringo.realm;

import no.sr.ringo.security.HashedPassword;
import no.sr.ringo.security.Hasher;
import no.sr.ringo.security.SaltData;
import org.apache.catalina.realm.DataSourceRealm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This Realm verifies login of users against the "account" table in the database.
 * The Roles are fetched from "account_role" table.
 *
 * ADMIN_USERNAME can log in as any user without knowing their username by prefixing
 * the username with "admin=" and giving the ADMIN_USERNAME password.
 *
 * Eg.
 * If ADMIN_USERNAME has password "admin123" in the database, he could log in as "user1"
 * by writing "admin=user1" and using his own "admin123" password.
 */
public class RingoDataSourceRealm extends DataSourceRealm {

    private static final String ADMIN_USERNAME = "sr-admin";

    private static final Logger log = Logger.getLogger(RingoDataSourceRealm.class.getName());

    private static final String SALT_QUERY = "SELECT id, created_ts FROM account WHERE username = ?";

    private static final String ADMIN_PREFIX = "admin=";

    /**
     * Hashes the password, and passes it to DataSourceRealm
     */
    @Override
    public Principal authenticate(Connection con, String username, String password) {

        if (username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0) {
            //containerLog.trace(sm.getString("dataSourceRealm.authenticateFailure", username));
            log.finest(String.format("Authentication failed for user %s", username));
            return null;
        }

        if (username.startsWith(ADMIN_PREFIX)) {

            // hash the given admin password and compare it to the hashed password from the database
            HashedPassword hashedInputPassword = null;
            try {
                hashedInputPassword = hashPassword(con, ADMIN_USERNAME, password);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }

            HashedPassword hashedDatabasePassword = new HashedPassword(super.getPassword(ADMIN_USERNAME));

            if (hashedInputPassword.equals(hashedDatabasePassword)) {

                // extract the username we wanna log in as
                username = username.substring(ADMIN_PREFIX.length());
                password = super.getPassword(username);

                log.finer("sudo: username: " + username + ", password (hashed): " + password);

                if (password == null) {
                    return null;
                }
                // continue the authentication procedure with the username and password we wanna log in as
                try {
                    return super.authenticate(con, username, password);
                } catch (SQLException e) {
                    return null;
                }
            } else {
                // FAIL, hashed input password doesn't match the hashed password from the database
                //containerLog.trace(sm.getString("dataSourceRealm.authenticateFailure", username));
                log.warning(String.format("Authentication failed for user %s", username));
                return null;
            }
        } else {

            // hash the password
            try {
                return super.authenticate(con, username, hashPassword(con, username, password).toString());
            } catch (SQLException e) {
                return null;
            }
        }
    }

    /**
     * Override the getRoles from DataSourceRealm, since that is looking for a username in the role table. We don't have
     * that, so we have to override it.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected ArrayList getRoles(final Connection con, final String username) {

        final ArrayList<String> result = new ArrayList<String>();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement("select role_name from account_role join account on account.username = account_role.username where account.username=?");

            stmt.setString(1, username);

            rs = stmt.executeQuery();

            while (rs.next()) {

                result.add(rs.getString("role_name"));
            }

            return result;
        } catch (final SQLException e) {
            //containerLog.info(sm.getString("dataSourceRealm.getRoles.exception", username));
            log.fine(String.format("Authentication failed for user", username));
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }
            } catch (final SQLException e) {
                //containerLog.info(sm.getString("dataSourceRealm.getRoles.exception", username));
                log.fine(String.format("Authentication failed for user", username));
            }
        }

        log.finer("Found these roles for user with username " + username + ": " + result);

        return null;
    }

    private HashedPassword hashPassword(Connection con, String username, String password) throws SQLException {

        Hasher h = new Hasher();

        log.finest("Getting user data to create salt");
        SaltData saltData = getSaltData(con, username);

        if (saltData == null) {
            throw new SQLException("Cannot retrieve data for username: " + username);
        }

        String salt = saltData.getSalt();

        HashedPassword hashedPassword;

        try {
            hashedPassword = h.hash(password, salt);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error creating MessageDigest object - wrong algorithm specified");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error creating MessageDigest object - encoding problem");
        }
        return hashedPassword;
    }

    private SaltData getSaltData(Connection con, String username) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;

        SaltData saltData = null;

        try {

            // create statement to retrieve account id and created_ts
            stmt = con.prepareStatement(SALT_QUERY);
            stmt.setString(1, username);

            log.finer("Retrieving originator data");

            rs = stmt.executeQuery();

            if (rs.next() == false) {
                if (username != null) {
                    throw new SQLException("Query for username and salt data returned no results for username: " + username);
                } else {
                    throw new SQLException("Query for username and salt data returned no results for username, which was null");
                }

            } else {
                Integer accountId = rs.getInt(1);
                Timestamp created_ts = rs.getTimestamp(2);

                if (accountId == null || created_ts == null) {
                    throw new SQLException("Not enough data to create salt");
                }

                Date registrationDate = new Date(created_ts.getTime());
                saltData = new SaltData(accountId, registrationDate);

            }
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stmt != null) {
                stmt.close();
            }
        }

        return saltData;
    }
}

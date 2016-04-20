package ringo.realm;

import no.sr.ringo.security.HashedPassword;
import no.sr.ringo.security.Hasher;
import no.sr.ringo.security.SaltData;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.ParseException;

import static org.testng.Assert.assertEquals;

/**
 * User: Adam
 * Date: 2/24/12
 * Time: 4:29 PM
 */
public class RingoDataSourceRealmTest {

    private static final String SALT_QUERY = "SELECT id, created_ts FROM account WHERE username = ?";

    @Test(enabled = false)
    /**
     * Use this test to generate password for any account on any database
     */
    public void generatePasswordTest() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {

        String dbHost = "localhost";
        String dbName = "oxalis_test";
        String dbUser = "skrue";
        String dbPass = "vable";

        String username = "sr";
        String passwordToHash = "ringo";

        Connection connection = getConnection(dbHost, dbName, dbUser, dbPass);
        SaltData saltData = getSaltData(connection, username);

        Hasher hasher = new Hasher();

        HashedPassword password = hasher.hash(passwordToHash, saltData.getSalt());

        //uncomment this line to see the hashed password in test failure
        assertEquals("hash", password.toString());


    }

    @Test
    /**
     * This test is run locally on oxalis_test database, to generate password for different account on different db use generatePasswordTest
     */
    public void testPassword() throws ParseException, NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {

        updateTestAccount();
        String user = "sr";
        String password = "ringo";

        SaltData saltData = getSaltData(getConnection("localhost", "oxalis_test", "skrue", "vable"), user);

        Hasher h = new Hasher();

        HashedPassword hash = h.hash(password, saltData.getSalt());


        System.out.println("THE PASSWORD IS " + hash);
        assertEquals("51024cf513a4c9a670acc91900861c94", hash.toString());
    }

	private SaltData getSaltData(Connection con, String username) throws SQLException {

		PreparedStatement stmt = null;
		ResultSet rs = null;

		SaltData saltData = null;

		try {

			// create statement to retrieve account id and created_ts
			stmt = con.prepareStatement(SALT_QUERY);
			stmt.setString(1, username);

			rs = stmt.executeQuery();

			if(rs.next() == false) {
				if (username != null) {
					throw new SQLException("Query for username and salt data returned no results for username: " + username);
				} else {
					throw new SQLException("Query for username and salt data returned no results for username, which was null");
				}

			}
			else {
				Integer accountId = rs.getInt(1);
				Timestamp created_ts = rs.getTimestamp(2);

				if(accountId == null || created_ts == null) {
					throw new SQLException("Not enough data to create salt");
				}

				Date registrationDate = new Date(created_ts.getTime());
				saltData = new SaltData(accountId, registrationDate);

			}
		}
		finally {
			if(rs != null) {
				rs.close();
			}

			if(stmt != null) {
				stmt.close();
			}
		}

		return saltData;
	}

    private void updateTestAccount() {
        Connection connection = getConnection("localhost", "oxalis_test", "skrue", "vable");
        String sql = "update account set created_ts = '2012-01-30 07:59:09', password = '51024cf513a4c9a670acc91900861c94' where username = 'sr'";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(sql + " failed " + e, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new IllegalStateException("Unable to close connection " + e, e);
                }
            }
        }
    }

    private Connection getConnection(String host, String dbName, String username, String password) {
        Connection conn = null;
        String url = "jdbc:mysql://"+host+":3306/";
        String driver = "com.mysql.jdbc.Driver";

        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + dbName, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }


}

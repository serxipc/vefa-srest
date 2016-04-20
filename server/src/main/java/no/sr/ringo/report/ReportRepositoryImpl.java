package no.sr.ringo.report;

import com.google.inject.Inject;
import no.sr.ringo.peppol.RingoUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user: orby
 * Date: 15.05.12
 * Time: 11.50
 */
public class ReportRepositoryImpl implements ReportRepository {

    private DataSource dataSource;

    @Inject
    public ReportRepositoryImpl(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    @Override
    public ReportData getReport(final Integer year, final Integer month) {

        Connection con = null;
        PreparedStatement ps = null;

        ReportData result = new ReportData(year, month);

        try {

            con = dataSource.getConnection();
            ps = con.prepareStatement(RingoUtils.getResourceFromJar(this.getClass(), "/sql/monthlyReport.sql").toString());

            ps.setInt(1, year);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setInt(4, month);

            ResultSet rs = ps.executeQuery();

            rs.beforeFirst();

            while(rs.next()) {

                result.addEntry(rs.getString("CustomizationID"), rs.getInt("Mottatt"), rs.getInt("Sendt"));
            }
        }
        catch(SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            if(con != null) {
                try {
                    con.close();
                }
                catch(SQLException e) {
                }
            }
        }

        return result;
    }
}

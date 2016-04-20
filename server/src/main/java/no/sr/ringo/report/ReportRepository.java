package no.sr.ringo.report;

/**
 * Created by user: orby
 * Date: 15.05.12
 * Time: 11.49
 */
public interface ReportRepository {

    ReportData getReport(Integer year, Integer month);
}

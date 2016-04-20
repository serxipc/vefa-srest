package no.sr.ringo.report;

import com.google.inject.Inject;
import no.sr.ringo.email.EmailService;

/**
 * Created by user: orby
 * Date: 15.05.12
 * Time: 11.24
 */
public class SendReportUseCase {

    private ReportRepository reportRepository;
    private EmailService emailService;

    @Inject
    public SendReportUseCase(ReportRepository reportRepository, EmailService emailService) {
        this.reportRepository = reportRepository;
        this.emailService = emailService;
    }

    public String sendReport(final Integer year, final Integer month, final String email) {
        ReportData reportData = reportRepository.getReport(year, month);
        String csvReport = reportData.toCsv();
        this.emailService.sendReport(email, csvReport);
        return csvReport;
    }

}

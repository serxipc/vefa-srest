package no.sr.ringo.report;

import java.util.Calendar;

/**
 * Created by user: orby
 * Date: 15.05.12
 * Time: 13.58
 */
public class RingoReportUtils {

    /**
     * @return current year. If current month is January, return previous year.
     */
    public static Integer getDefaultYearForReport() {

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        if(month == 0) {
            return year - 1;
        }
        else {
            return year;
        }
    }

    /**
     * January = 1, etc...
     * @return previous month. Returns December if current month is January.
     */
    public static Integer getPreviousMonth() {

        Calendar cal = Calendar.getInstance();

        int month = cal.get(Calendar.MONTH);

        if(month == 0) {
            return 12;
        }
        else {
            return month; // java using 0 for january so I can just return that value. No point of subtract one since Java already have done it for me :)
        }

    }
}

package no.sr.ringo.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user: orby
 * Date: 15.05.12
 * Time: 11.28
 */
public class ReportData {

    private final List<Entry> entries;
    private final Integer year;
    private final Integer month;

    public ReportData(final Integer year, final Integer month) {
        this.year = year;
        this.month = month;

        entries = new ArrayList<Entry>();
    }

    public String toCsv() {

        StringBuilder result = new StringBuilder("M\u00E5ned,Aksesspunkt,CustomizationID,Mottatt,Sendt\n");

        for(Entry entry : entries) {

            result.append(String.format("%s,SendRegning,%s", yearMonth(year, month), entry.getCsvLine()));
        }

        return result.toString();
    }

    /**
     * @return yyyy-mm even if month is less than 10
     */
    private String yearMonth(final Integer year, final Integer month) {

        String formattedMonth = String.valueOf(month);

        if(month < 10) {
            formattedMonth = String.format("0%d", month);
        }


        return String.format("%d-%s", year, formattedMonth);
    }

    public void addEntry(final String customizationID, final Integer noOfReceived, final Integer noOfSent) {

        entries.add(new Entry(customizationID, noOfReceived, noOfSent));
    }

    private class Entry {

        private final String customizationID;
        private final Integer noOfReceived;
        private final Integer noOfSent;

        private Entry(final String customizationID, final Integer noOfReceived, final Integer noOfSent) {

            if(customizationID == null) {
                this.customizationID = "";
            }
            else {
                this.customizationID = customizationID;
            }
            this.noOfReceived = noOfReceived;
            this.noOfSent = noOfSent;
        }

        public String getCsvLine() {
            return String.format("%s,%d,%d\n", customizationID, noOfReceived, noOfSent);
        }
    }
}
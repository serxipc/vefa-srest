/* Created by steinar on 15.05.12 at 23:00 */
package no.sr.ringo.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
public class SoftwareVersionNumber {

    Integer majorRevision;
    Integer minorRevision;
    Integer incrementalRevision;
    String qualifier;

    /**
     * Regex pattern which will always create 4 groups, numbered from 1 through 4. Group 0 is the entire pattern.
     * Group number 3 and 4 are optional (incremental version number and qualifier)
     */
    Pattern versionNumberPattern = Pattern.compile(
            "(\\d+)"                    // Major revision number
            + "(?:\\.(\\d+))"           // Minor revision number
            + "(?:(?:\\.(\\d+))?"       // Incremental revision number  (optional)
            +"(?:\\-(\\w*))?)?");       // Qualifier (optional)


    public SoftwareVersionNumber(String versionNumber) {
        Matcher m = versionNumberPattern.matcher(versionNumber);
        if (m.matches()) {

            majorRevision = Integer.parseInt(m.group(1));
            minorRevision = Integer.parseInt(m.group(2));
            if (m.group(3) != null)
                incrementalRevision = Integer.parseInt(m.group(3));
            if (m.group(4) != null) {
                qualifier = m.group(4);
            }
        } else
            throw new IllegalArgumentException("Invalid version number: " + versionNumber);
    }

    public Integer getMajorRevision() {
        return majorRevision;
    }

    public Integer getMinorRevision() {
        return minorRevision;
    }

    /** Optional incremental revision number, i.e. this could possibly return null */
    public Integer getIncrementalRevision() {
        return incrementalRevision;
    }

    public String getQualifier() {
        return qualifier;
    }

    /**
     * Checks if version is up to date comparing minor and major revisions only.
     *
     * Example usage boolean outOfDate = serverVersion.isOutOfDate(clientVersion);
     *
     * @param another
     * @return
     */
    public boolean isOutOfDate(SoftwareVersionNumber another) {
        return another.majorRevision != majorRevision || minorRevision != another.minorRevision;
    }


}

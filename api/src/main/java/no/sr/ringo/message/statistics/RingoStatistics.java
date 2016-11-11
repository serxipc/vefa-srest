package no.sr.ringo.message.statistics;



import eu.peppol.persistence.AccountId;

import java.util.List;

/**
 * Statistics for all accounts on Ringo server
 *
 * User: andy
 * Date: 9/5/12
 * Time: 2:32 PM
 */
public class RingoStatistics {
    private final List<RingoAccountStatistics> accountStatistics;

    public RingoStatistics(List<RingoAccountStatistics> accountStatistics) {
        this.accountStatistics = accountStatistics;
    }

    public List<RingoAccountStatistics> getAccountStatistics() {
        return accountStatistics;
    }


    /**
     * Finds the statistics for a given account from the list of available accounts
     * Usefull for testing
     */
    public RingoAccountStatistics findByAccountId(AccountId id) {
        //This obviously can be improved if needed
        for (RingoAccountStatistics accountStatistic : accountStatistics) {
            if (accountStatistic.getRingoAccountId().equals(id)) {
                return accountStatistic;
            }
        }
        return null;
    }

    public String asXml() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<statistics>");
        for (RingoAccountStatistics accountStatistic : accountStatistics) {
            sb.append(accountStatistic.toXml());
        }
        sb.append("</statistics>");
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RingoStatistics that = (RingoStatistics) o;

        if (accountStatistics != null ? !accountStatistics.equals(that.accountStatistics) : that.accountStatistics != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return accountStatistics != null ? accountStatistics.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RingoStatistics");
        sb.append("{accountStatistics=").append(accountStatistics);
        sb.append('}');
        return sb.toString();
    }
}

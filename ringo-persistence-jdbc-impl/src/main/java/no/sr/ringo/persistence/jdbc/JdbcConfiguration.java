package no.sr.ringo.persistence.jdbc;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.Optional;

import static no.sr.ringo.config.RingoConfigProperty.*;
/**
 * @author steinar
 *         Date: 30.01.2017
 *         Time: 08.55
 */
public class JdbcConfiguration {

    URI jdbcConnectionUri;

    Optional<String> jdbcDriverClassPath = Optional.empty();

    String jdbcDriverClassName;

    String jdbcUsername;

    String jdbcPassword;

    Optional<String> validationQuery = Optional.empty();


    @Inject
    JdbcConfiguration(@Named(JDBC_CONNECTION_URI)           String  jdbcConnectionUri,
                             @Named(JDBC_CLASS_PATH)        String  jdbcDriverClassPath,
                             @Named(JDBC_DRIVER_CLASS)      String  jdbcDriverClassName,
                             @Named(JDBC_USER)              String  jdbcUsername,
                             @Named(JDBC_PASSWORD)          String  jdbcPassword,
                             @Named(JDBC_VALIDATION_QUERY)  String validationQuery) {


        this.jdbcConnectionUri = URI.create(jdbcConnectionUri);
        this.jdbcDriverClassPath = jdbcDriverClassPath != null && jdbcDriverClassPath.trim().length() > 0 ? Optional.of(jdbcDriverClassPath) : Optional.empty();
        this.jdbcDriverClassName = jdbcDriverClassName;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
        this.validationQuery = validationQuery != null && validationQuery.trim().length() > 0 ? Optional.of(validationQuery) : Optional.empty();
    }

    public URI getJdbcConnectionUri() {
        return jdbcConnectionUri;
    }

    public Optional<String> getJdbcDriverClassPath() {
        return jdbcDriverClassPath;
    }

    public String getJdbcDriverClassName() {
        return jdbcDriverClassName;
    }

    public String getJdbcUsername() {
        return jdbcUsername;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public Optional<String> getValidationQuery() {
        return validationQuery;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JdbcConfiguration{");
        sb.append("jdbcConnectionUri=").append(jdbcConnectionUri);
        sb.append(", jdbcDriverClassPath=").append(jdbcDriverClassPath);
        sb.append(", jdbcDriverClassName='").append(jdbcDriverClassName).append('\'');
        sb.append(", jdbcUsername='").append(jdbcUsername).append('\'');
        sb.append(", jdbcPassword='").append(jdbcPassword).append('\'');
        sb.append(", validationQuery=").append(validationQuery);
        sb.append('}');
        return sb.toString();
    }
}

package no.sr.ringo.parser;

import java.io.File;

/**
 * User: adam
 * Date: 3/9/13
 * Time: 2:06 PM
 */
public class ParserResult {



    public enum PROCESSING_TYPE {ALL, SINGLE}

    private final PROCESSING_TYPE processingType;
    private final String dbUser;
    private final String dbPass;
    private final String dbHost;
    private final String dbName;
    private final Integer msgNo;
    private final File keystore;
    private final boolean production;

    public ParserResult(PROCESSING_TYPE processing_type, String dbUser, String dbPass, String dbHost, String dbName, Integer msgNo, File keystore, boolean production) {
        this.processingType = processing_type;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.dbHost = dbHost;
        this.msgNo = msgNo;
        this.dbName = dbName;
        this.keystore = keystore;
        this.production = production;
    }

    public PROCESSING_TYPE getProcessingType() {
        return processingType;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public String getDbHost() {
        return dbHost;
    }

    public Integer getQueueId() {
        return msgNo;
    }
    public String getDbName() {
        return dbName;
    }

    public File getKeystore() {
        return keystore;
    }

    public boolean isProduction() {
        return production;
    }

    @Override
    public String toString() {
        return "ParserResult{" +
                "processingType=" + processingType +
                ", dbUser='" + dbUser + '\'' +
                ", dbPass='" + dbPass + '\'' +
                ", dbHost='" + dbHost + '\'' +
                ", dbName='" + dbName + '\'' +
                ", msgNo=" + msgNo +
                ", keystore=" + keystore +
                ", production=" + production +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParserResult that = (ParserResult) o;

        if (production != that.production) return false;
        if (dbHost != null ? !dbHost.equals(that.dbHost) : that.dbHost != null) return false;
        if (dbName != null ? !dbName.equals(that.dbName) : that.dbName != null) return false;
        if (dbPass != null ? !dbPass.equals(that.dbPass) : that.dbPass != null) return false;
        if (dbUser != null ? !dbUser.equals(that.dbUser) : that.dbUser != null) return false;
        if (keystore != null ? !keystore.equals(that.keystore) : that.keystore != null) return false;
        if (msgNo != null ? !msgNo.equals(that.msgNo) : that.msgNo != null) return false;
        if (processingType != that.processingType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = processingType != null ? processingType.hashCode() : 0;
        result = 31 * result + (dbUser != null ? dbUser.hashCode() : 0);
        result = 31 * result + (dbPass != null ? dbPass.hashCode() : 0);
        result = 31 * result + (dbHost != null ? dbHost.hashCode() : 0);
        result = 31 * result + (dbName != null ? dbName.hashCode() : 0);
        result = 31 * result + (msgNo != null ? msgNo.hashCode() : 0);
        result = 31 * result + (keystore != null ? keystore.hashCode() : 0);
        result = 31 * result + (production ? 1 : 0);
        return result;
    }
}

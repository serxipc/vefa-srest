package no.sr.ringo.parser;

/**
 * User: adam
 * Date: 3/9/13
 * Time: 2:06 PM
 */
public class ParserResult {

    public enum PROCESSING_TYPE {ALL, SINGLE}

    private final PROCESSING_TYPE processingType;
    private final Integer msgNo;
    private final boolean production;

    public ParserResult(PROCESSING_TYPE processing_type,  Integer msgNo,  boolean production) {
        this.processingType = processing_type;
        this.msgNo = msgNo;
        this.production = production;
    }

    public PROCESSING_TYPE getProcessingType() {
        return processingType;
    }


    public Integer getQueueId() {
        return msgNo;
    }

    @Override
    public String toString() {
        return "ParserResult{" +
                "processingType=" + processingType +
                ", msgNo=" + msgNo +
                ", production=" + production +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParserResult that = (ParserResult) o;

        if (production != that.production) return false;
        if (msgNo != null ? !msgNo.equals(that.msgNo) : that.msgNo != null) return false;
        if (processingType != that.processingType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = processingType != null ? processingType.hashCode() : 0;
        result = 31 * result + (msgNo != null ? msgNo.hashCode() : 0);
        result = 31 * result + (production ? 1 : 0);
        return result;
    }
}

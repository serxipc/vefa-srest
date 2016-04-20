package no.sr.ringo.request;

/**
 * Interface for deciding what to do when the response from the service
 * indicates a temporary failure.
 *
 * User: andy
 * Date: 2/23/12
 * Time: 1:06 PM
 */
public interface RetryStrategy {

    /**
     * The maximum number of times to retry the request
     * @return
     */
    int getMaxRetryCount();

    /**
     * The time in seconds to wait between each request
     * @return
     */
    int pauseLength();

    public RetryStrategy ONCE = new RetryStrategy() {
        public int getMaxRetryCount() {
            return 1;
        }

        public int pauseLength() {
            return 1;
        }
    };

    /**
     * Do not retry
     */
    public RetryStrategy NONE = new RetryStrategy() {
        public int getMaxRetryCount() {
            return 0;
        }
        public int pauseLength() {
            return 0;
        }
    };

    /**
     * Retry forever waiting 3 minutes between requests.
      */
    public RetryStrategy FOREVER = new RetryStrategy() {
        public int getMaxRetryCount() {
            return -1;
        }

        public int pauseLength() {
            return 60*3;
        }
    };

    /**
     * The default strategy, which is to retry 10 times
     * with a 3 minute pause between each request. This gives
     * a total time of 30 minutes before the request will fail.
     */
    public RetryStrategy DEFAULT = new RetryStrategy() {
        public int getMaxRetryCount() {
            return 10;
        }
        public int pauseLength() {
            return 60*3;
        }
    };


}

package no.sr.ringo.common;

/**
 * User: andy
 * Date: 11/16/12
 * Time: 12:58 PM
 */
public class IsProductionServer {
    private final boolean productionServer;

    public static final IsProductionServer FALSE = new IsProductionServer(false);
    public static final IsProductionServer TRUE = new IsProductionServer(true);

    public IsProductionServer(boolean productionServer) {
        this.productionServer = productionServer;
    }

    public boolean isProductionServer() {
        return productionServer;
    }
}

package fileio;

/**
 * This calls contains input data of a cost change during a month
 */
public final class ChangesInputData {
    /**
     * Id of distributor on which cost change is performed
     */
    private final int id;
    /**
     * Changed infrastructure cost of distributor
     */
    private final int infrastructureCost;
    /**
     * Changed production cost of distributor
     */
    private final int productionCost;

    public ChangesInputData(final int id, final int infrastructureCost, final int productionCost) {
        this.id = id;
        this.infrastructureCost = infrastructureCost;
        this.productionCost = productionCost;
    }

    public int getId() {
        return id;
    }

    public int getInfrastructureCost() {
        return infrastructureCost;
    }

    public int getProductionCost() {
        return productionCost;
    }
}

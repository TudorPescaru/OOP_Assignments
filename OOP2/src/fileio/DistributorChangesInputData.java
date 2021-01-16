package fileio;

/**
 * This class contains input data of a distributor change during a month
 */
public final class DistributorChangesInputData {
    /**
     * Id of distributor on which cost change is performed
     */
    private final int id;
    /**
     * Changed infrastructure cost of distributor
     */
    private final int infrastructureCost;

    public DistributorChangesInputData(final int id, final int infrastructureCost) {
        this.id = id;
        this.infrastructureCost = infrastructureCost;
    }

    public int getId() {
        return id;
    }

    public int getInfrastructureCost() {
        return infrastructureCost;
    }
}

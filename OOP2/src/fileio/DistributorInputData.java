package fileio;

/**
 * This class contains initial input data for a distributor
 */
public final class DistributorInputData {
    /**
     * Distributor's id
     */
    private final int id;
    /**
     * Distributor's contract length offer
     */
    private final int contractLength;
    /**
     * Distributor's initial budget
     */
    private final int initialBudget;
    /**
     * Distributor's initial infrastructure cost
     */
    private final int initialInfrastructureCost;
    /**
     * Distributor's initial production cost
     */
    private final int initialProductionCost;

    public DistributorInputData(final int id, final int contractLength, final int initialBudget,
                                final int initialInfrastructureCost,
                                final int initialProductionCost) {
        this.id = id;
        this.contractLength = contractLength;
        this.initialBudget = initialBudget;
        this.initialInfrastructureCost = initialInfrastructureCost;
        this.initialProductionCost = initialProductionCost;
    }

    public int getId() {
        return id;
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public int getInitialInfrastructureCost() {
        return initialInfrastructureCost;
    }

    public int getInitialProductionCost() {
        return initialProductionCost;
    }

    @Override
    public String toString() {
        return "DistributorInputData{"
                + "id=" + id
                + ", contractLength=" + contractLength
                + ", initialBudget=" + initialBudget
                + ", initialInfrastructureCost=" + initialInfrastructureCost
                + ", initialProductionCost=" + initialProductionCost
                + '}';
    }
}

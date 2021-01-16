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
     * Distributor's quantity of required energy for every month
     */
    private final int energyNeededKW;
    /**
     * Distributor's strategy of choice based on which producer is chosen
     */
    private final String producerStrategy;

    public DistributorInputData(final int id, final int contractLength, final int initialBudget,
                                final int initialInfrastructureCost, final int energyNeededKW,
                                final String producerStrategy) {
        this.id = id;
        this.contractLength = contractLength;
        this.initialBudget = initialBudget;
        this.initialInfrastructureCost = initialInfrastructureCost;
        this.energyNeededKW = energyNeededKW;
        this.producerStrategy = producerStrategy;
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

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public String getProducerStrategy() {
        return producerStrategy;
    }
}

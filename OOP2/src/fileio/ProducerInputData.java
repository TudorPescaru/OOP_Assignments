package fileio;

/**
 * This class contains initial input data for a producer
 */
public final class ProducerInputData {
    /**
     * Producer's id
     */
    private final int id;
    /**
     * Producer's type of produced energy
     */
    private final String energyType;
    /**
     * Producer's maximum number of distributors to whom it can offer
     */
    private final int maxDistributors;
    /**
     * Producer's price per KW of energy
     */
    private final double priceKW;
    /**
     * Producer's maximum quantity of energy it can offer a distributor
     */
    private final int energyPerDistributor;

    public ProducerInputData(final int id, final String energyType, final int maxDistributors,
                             final double priceKW, final int energyPerDistributor) {
        this.id = id;
        this.energyType = energyType;
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
    }

    public int getId() {
        return id;
    }

    public String getEnergyType() {
        return energyType;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public double getPriceKW() {
        return priceKW;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }
}

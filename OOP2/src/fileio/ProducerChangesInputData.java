package fileio;

/**
 * This class contains input data of a producer change during a month
 */
public final class ProducerChangesInputData {
    /**
     * Id of producer on which cost change is performed
     */
    private final int id;
    /**
     * Changed quantity of energy a producer can offer
     */
    private final int energyPerDistributor;

    public ProducerChangesInputData(final int id, final int energyPerDistributor) {
        this.id = id;
        this.energyPerDistributor = energyPerDistributor;
    }

    public int getId() {
        return id;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }
}

package entities;

/**
 * This class defines a contract issued by a distributor to a consumer
 */
public final class Contract {
    /**
     * Id of consumer who has accepted this contract
     */
    private final int consumerId;
    /**
     * Length of the contract
     */
    private int contractLength;
    /**
     * Monthly rate imposed by contract
     */
    private final int monthlyCost;
    /**
     * Distributor that has issued the contract
     */
    private final Distributor distributor;

    public Contract(final int consumerId, final int contractLength, final int monthlyCost,
                    final Distributor distributor) {
        this.consumerId = consumerId;
        this.contractLength = contractLength;
        this.monthlyCost = monthlyCost;
        this.distributor = distributor;
    }

    public int getConsumerId() {
        return consumerId;
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getMonthlyCost() {
        return monthlyCost;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    /**
     * Decrease the length of this contract
     */
    public void decreaseLength() {
        contractLength--;
    }

    @Override
    public String toString() {
        return "Contract{"
                + "contractLength=" + contractLength
                + ", monthlyCost=" + monthlyCost
                + ", distributor=" + distributor
                + '}';
    }
}

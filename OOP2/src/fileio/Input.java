package fileio;

import java.util.List;

/**
 * This class contains all the input data given
 */
public final class Input {
    /**
     * Number of turns for the game
     */
    private final int numberOfTurns;
    /**
     * List of objects containing data about consumers
     */
    private final List<ConsumerInputData> consumerData;
    /**
     * List of objects containing data about distributors
     */
    private final List<DistributorInputData> distributorData;
    /**
     * List of objects containing data about updates
     */
    private final List<UpdatesInputData> updatesData;

    public Input() {
        this.numberOfTurns = 0;
        this.consumerData = null;
        this.distributorData = null;
        this.updatesData = null;
    }

    public Input(final int numberOfTurns, final List<ConsumerInputData> consumerData,
                 final List<DistributorInputData> distributorData,
                 final List<UpdatesInputData> updatesData) {
        this.numberOfTurns = numberOfTurns;
        this.consumerData = consumerData;
        this.distributorData = distributorData;
        this.updatesData = updatesData;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public List<ConsumerInputData> getConsumerData() {
        return consumerData;
    }

    public List<DistributorInputData> getDistributorData() {
        return distributorData;
    }

    public List<UpdatesInputData> getUpdatesData() {
        return updatesData;
    }
}

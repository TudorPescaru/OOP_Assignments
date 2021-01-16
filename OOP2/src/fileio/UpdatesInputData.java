package fileio;

import java.util.List;

/**
 * This class contains input data of monthly changes to be performed
 */
public final class UpdatesInputData {
    /**
     * List of new customers added in this update
     */
    private final List<ConsumerInputData> newConsumers;
    /**
     * List of changes made to distributors in this update
     */
    private final List<DistributorChangesInputData> distributorChanges;
    /**
     * List of changes made to distributors in this update
     */
    private final List<ProducerChangesInputData> producerChanges;

    public UpdatesInputData() {
        this.newConsumers = null;
        this.distributorChanges = null;
        this.producerChanges = null;
    }

    public UpdatesInputData(final List<ConsumerInputData> newConsumers,
                            final List<DistributorChangesInputData> distributorChanges,
                            final List<ProducerChangesInputData> producerChanges) {
        this.newConsumers = newConsumers;
        this.distributorChanges = distributorChanges;
        this.producerChanges = producerChanges;
    }

    public List<ConsumerInputData> getNewConsumers() {
        return newConsumers;
    }

    public List<DistributorChangesInputData> getDistributorChanges() {
        return distributorChanges;
    }

    public List<ProducerChangesInputData> getProducerChanges() {
        return producerChanges;
    }
}

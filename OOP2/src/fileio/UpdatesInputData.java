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
     * List of changes made to costs in this update
     */
    private final List<ChangesInputData> costsChanges;

    public UpdatesInputData() {
        this.newConsumers = null;
        this.costsChanges = null;
    }

    public UpdatesInputData(final List<ConsumerInputData> newConsumers,
                            final List<ChangesInputData> costsChanges) {
        this.newConsumers = newConsumers;
        this.costsChanges = costsChanges;
    }

    public List<ConsumerInputData> getNewConsumers() {
        return newConsumers;
    }

    public List<ChangesInputData> getCostsChanges() {
        return costsChanges;
    }
}

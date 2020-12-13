package entities;

import fileio.ConsumerInputData;
import fileio.DistributorInputData;

/**
 * This class defines a factory used to create consumer and distributor objects
 */
public final class EntityFactory {
    /**
     * Available types of entities
     */
    public enum EntityType {
        CONSUMER, DISTRIBUTOR
    }

    private EntityFactory() {
    }

    /**
     * Create a consumer or distributor entity based on requested type and input data
     * @param entityType type of entity to be created
     * @param inputObject object containing input data
     * @return new consumer or distributor entity
     */
    public static Entity createEntity(final EntityType entityType, final Object inputObject) {
        switch (entityType) {
            case CONSUMER:
                ConsumerInputData consumerInputData = (ConsumerInputData) inputObject;
                return new Consumer(consumerInputData.getId(),
                        consumerInputData.getInitialBudget(),
                        consumerInputData.getMonthlyIncome());
            case DISTRIBUTOR:
                DistributorInputData distributorInputData = (DistributorInputData) inputObject;
                return new Distributor(distributorInputData.getId(),
                        distributorInputData.getContractLength(),
                        distributorInputData.getInitialBudget(),
                        distributorInputData.getInitialInfrastructureCost(),
                        distributorInputData.getInitialProductionCost());
            default:
                return null;
        }
    }
}
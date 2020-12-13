package database;

import entities.Entity;
import entities.EntityFactory;
import entities.Consumer;
import entities.Distributor;
import fileio.Input;
import fileio.ConsumerInputData;
import fileio.DistributorInputData;
import fileio.UpdatesInputData;
import fileio.ChangesInputData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class defines a singleton database which will be the main driver of the program
 */
public final class Database {
    /**
     * Input processed by the database
     */
    private Input input;
    /**
     * Mpa of consumer objects in the database
     */
    private final Map<Integer, Consumer> consumersMap = new LinkedHashMap<>();
    /**
     * Map of distributor objects in the database
     */
    private final Map<Integer, Distributor> distributorsMap = new LinkedHashMap<>();

    private Database() {
    }

    /**
     * Bill Pugh Singleton implementation
     */
    private static class Singleton {
        private static final Database INSTANCE = new Database();
    }

    /**
     * Instantiate singleton if not already instantiated and return instance
     * @return instance of singleton database
     */
    public static Database getInstance() {
        return Singleton.INSTANCE;
    }

    public Input getInput() {
        return input;
    }

    public Map<Integer, Consumer> getConsumersMap() {
        return consumersMap;
    }

    public Map<Integer, Distributor> getDistributorsMap() {
        return distributorsMap;
    }

    /**
     * Take an input and populate the database with usable entities
     * @param givenInput input to be processed
     */
    public void processInput(final Input givenInput) {
        this.input = givenInput;
        this.convertInput();
    }

    /**
     * Convert the input given to the database to usable entities
     */
    private void convertInput() {
        for (ConsumerInputData consumerInputData : input.getConsumerData()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.CONSUMER,
                    consumerInputData);
            if (entity != null) {
                Consumer consumer = (Consumer) entity;
                consumersMap.put(consumer.getId(), consumer);
            }
        }
        for (DistributorInputData distributorInputData : input.getDistributorData()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.DISTRIBUTOR,
                    distributorInputData);
            if (entity != null) {
                Distributor distributor = (Distributor) entity;
                distributorsMap.put(distributor.getId(), distributor);
            }
        }
    }

    /**
     * Run game based on given input
     */
    public void runGame() {
        /* TODO
        * run initial round
        *   compute contract prices
        *   consumers process month
        *   distributors process month
        * for num turns
        *   make updates
        *   compute contract prices
        *   consumers process month
        *   distributors process month
        */
        if (checkAllDistributorsBankrupt()) {
            return;
        }
        runRound();
        for (int i = 0; i < input.getNumberOfTurns(); i++) {
            // System.out.println("Month: " + (i + 1));
            if (checkAllDistributorsBankrupt()) {
                return;
            }
            runRoundWithUpdates(input.getUpdatesData().get(i));
        }
    }

    /**
     * Perform consumer and distributor monthly actions
     */
    private void runRound() {
        for (Distributor distributor : distributorsMap.values()) {
            distributor.calculateContractRate();
        }
        for (Consumer consumer : consumersMap.values()) {
            consumer.processMonth();
        }
        for (Distributor distributor : distributorsMap.values()) {
            distributor.processMonth();
        }
    }

    /**
     * For rounds that contain updates, first perform updates and then perform operations
     * @param thisMonthUpdates updates that need to happen this month
     */
    private void runRoundWithUpdates(final UpdatesInputData thisMonthUpdates) {
        processUpdates(thisMonthUpdates);
        runRound();
    }

    /**
     * Process the updates given for current month
     * @param thisMonthUpdates updates to be performed for this month
     */
    private void processUpdates(final UpdatesInputData thisMonthUpdates) {
        for (ConsumerInputData consumerInputData : thisMonthUpdates.getNewConsumers()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.CONSUMER,
                    consumerInputData);
            if (entity != null) {
                Consumer consumer = (Consumer) entity;
                consumersMap.put(consumer.getId(), consumer);
            }
        }
        for (ChangesInputData changesInputData : thisMonthUpdates.getCostsChanges()) {
            Distributor distributor = distributorsMap.get(changesInputData.getId());
            if (!distributor.isBankrupt()) {
                distributor.setInfrastructureCost(changesInputData.getInfrastructureCost());
                distributor.setProductionCost(changesInputData.getProductionCost());
            }
        }
    }

    /**
     * Check if all distributors are bankrupt
     * @return T/F if all distributors are bankrupt
     */
    private boolean checkAllDistributorsBankrupt() {
        for (Distributor distributor : distributorsMap.values()) {
            if (!distributor.isBankrupt()) {
                return false;
            }
        }
        return true;
    }
}

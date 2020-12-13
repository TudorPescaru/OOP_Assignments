package database;

import entities.Consumer;
import entities.Distributor;
import entities.Entity;
import entities.EntityFactory;
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
        runRound();
        for (int i = 0; i < input.getNumberOfTurns(); i++) {
            runRoundWithUpdates(input.getUpdatesData().get(i));
        }
    }

    private void runRound() {
        for (Consumer consumer : consumersMap.values()) {
            consumer.processMonth();
        }
//        System.out.println("Consumer: " + consumersMap.get(0).getBudget() + " "
//                + consumersMap.get(0).isBankrupt());
//        System.out.println("Consumer: " + consumersMap.get(1).getBudget() + " "
//                + consumersMap.get(1).isBankrupt());
        for (Distributor distributor : distributorsMap.values()) {
            distributor.processMonth();
        }
//        System.out.println("Distributor: " + distributorsMap.get(0).getBudget());
//        if (distributorsMap.get(0).getContracts().size() > 0) {
//            System.out.println("Contract: " + distributorsMap.get(0).getContracts().get(0));
//        }
    }

    private void runRoundWithUpdates(final UpdatesInputData thisMonthUpdates) {
        processUpdates(thisMonthUpdates);
        runRound();
    }

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
            distributor.setInfrastructureCost(changesInputData.getInfrastructureCost());
            distributor.setProductionCost(changesInputData.getProductionCost());
        }
    }
}

package database;

import entities.Entity;
import entities.EntityFactory;
import entities.Consumer;
import entities.Distributor;
import entities.Producer;
import fileio.Input;
import fileio.ConsumerInputData;
import fileio.DistributorInputData;
import fileio.ProducerInputData;
import fileio.UpdatesInputData;
import fileio.DistributorChangesInputData;
import fileio.ProducerChangesInputData;

import java.util.Comparator;
import java.util.ArrayList;
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
    /**
     * Map of producer objects in the database
     */
    private final Map<Integer, Producer> producersMap = new LinkedHashMap<>();

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

    public Map<Integer, Producer> getProducersMap() {
        return producersMap;
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
        // Clear current database storage
        consumersMap.clear();
        distributorsMap.clear();
        producersMap.clear();
        // Convert consumer input to usable consumer objects using factory and add them to map
        for (ConsumerInputData consumerInputData : input.getConsumerData()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.CONSUMER,
                    consumerInputData);
            if (entity != null) {
                Consumer consumer = (Consumer) entity;
                consumersMap.put(consumer.getId(), consumer);
            }
        }
        // Convert distributor input to usable distributor objects using factory and add them to map
        for (DistributorInputData distributorInputData : input.getDistributorData()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.DISTRIBUTOR,
                    distributorInputData);
            if (entity != null) {
                Distributor distributor = (Distributor) entity;
                distributorsMap.put(distributor.getId(), distributor);
            }
        }
        // Convert producer input to usable producer objects using factory and add them to map
        for (ProducerInputData producerInputData : input.getProducerData()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.PRODUCER,
                    producerInputData);
            if (entity != null) {
                Producer producer = (Producer) entity;
                producersMap.put(producer.getId(), producer);
            }
        }
    }

    /**
     * Run game based on given input
     */
    public void runGame() {
        // End game if all distributors start bankrupt
        if (checkAllDistributorsBankrupt()) {
            return;
        }
        // Run initial round
        runInitialRound();
        // Run rounds with updates
        for (int i = 0; i < input.getNumberOfTurns(); i++) {
            // Stop game if all distributors turn bankrupt
            if (checkAllDistributorsBankrupt()) {
                return;
            }
            // Run a round with given updates
            runRound(input.getUpdatesData().get(i));
        }
    }

    /**
     * Perform actions for initial round
     */
    private void runInitialRound() {
        // Sort distributors in order of id
        ArrayList<Distributor> idSort = new ArrayList<>(distributorsMap.values());
        idSort.sort(Comparator.comparingInt(Distributor::getId));
        // Apply strategy, calculate production cost and contract cost for distributors in id order
        for (Distributor distributor : idSort) {
            if (!distributor.isBankrupt()) {
                distributor.applyStrategy();
                distributor.calculateContractRate();
            }
        }
        // Process month for all consumers
        for (Consumer consumer : consumersMap.values()) {
            // Collect income, pick distributor for contract, pay distributor
            consumer.processMonth();
        }
        // Process month for all distributors
        for (Distributor distributor : distributorsMap.values()) {
            // Pay monthly costs
            distributor.processMonth();
        }
        // Process month for all producers
        for (Producer producer : producersMap.values()) {
            // Save list of distributors for this month
            producer.processMonth();
        }
    }

    /**
     * Run a normal round that has updates
     * @param thisMonthUpdates updates to be made for current month
     */
    private void runRound(final UpdatesInputData thisMonthUpdates) {
        // Perform updates for new consumers and distributors
        processUpdates(thisMonthUpdates);
        // Update distributor contract prices for current month
        for (Distributor distributor : distributorsMap.values()) {
            if (!distributor.isBankrupt()) {
                distributor.calculateContractRate();
            }
        }
        // Process month for all consumers
        for (Consumer consumer : consumersMap.values()) {
            // Collect income, pick distributor for contract, pay distributor
            consumer.processMonth();
        }
        // Process month for all distributors
        for (Distributor distributor : distributorsMap.values()) {
            // Pay monthly costs
            distributor.processMonth();
        }
        // Perform updates for producers and reapply strategies for distributors
        processProducerUpdates(thisMonthUpdates);
        // Process month for all producers
        for (Producer producer : producersMap.values()) {
            // Save list of distributors for this month
            producer.processMonth();
        }
    }

    /**
     * Process the consumer and distributor updates given for current month
     * @param thisMonthUpdates updates to be performed for this month
     */
    private void processUpdates(final UpdatesInputData thisMonthUpdates) {
        // Convert input data for new consumers to usable consumer objects and add them to map
        for (ConsumerInputData consumerInputData : thisMonthUpdates.getNewConsumers()) {
            Entity entity = EntityFactory.createEntity(EntityFactory.EntityType.CONSUMER,
                    consumerInputData);
            if (entity != null) {
                Consumer consumer = (Consumer) entity;
                consumersMap.put(consumer.getId(), consumer);
            }
        }
        // Perform updates on distributor objects accessed through map
        for (DistributorChangesInputData change : thisMonthUpdates.getDistributorChanges()) {
            Distributor distributor = distributorsMap.get(change.getId());
            if (!distributor.isBankrupt()) {
                distributor.setInfrastructureCost(change.getInfrastructureCost());
            }
        }
    }

    /**
     * Process the producer updates for current month and reapply strategies for distributors
     * @param thisMonthUpdates updates to be performed this month
     */
    private void processProducerUpdates(final UpdatesInputData thisMonthUpdates) {
        // Perform updates on producers and alert their distributors of changes
        for (ProducerChangesInputData change : thisMonthUpdates.getProducerChanges()) {
            Producer producer = producersMap.get(change.getId());
            producer.setEnergyPerDistributor(change.getEnergyPerDistributor());
        }
        // Sort distributors by id
        ArrayList<Distributor> toUpdate = new ArrayList<>(distributorsMap.values());
        toUpdate.sort(Comparator.comparingInt(Distributor::getId));
        // Reapply strategy for distributors who require it in id order
        for (Distributor distributor : toUpdate) {
            if (distributor.isToUpdate() && !distributor.isBankrupt()) {
                distributor.applyStrategy();
                // Set producer update flag back to false
                distributor.setToUpdate(false);
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

package entities;

import strategies.EnergyChoiceStrategy;
import strategies.EnergyChoiceStrategyFactory;
import strategies.EnergyChoiceStrategyType;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This class defines the distributor entity
 */
public final class Distributor implements Entity, Observer {
    /**
     * Distributor's given id
     */
    private final int id;
    /**
     * Distributor's current contract length
     */
    private final int contractLength;
    /**
     * Distributor's current budget
     */
    private int budget;
    /**
     * Distributor's current infrastructure cost
     */
    private int infrastructureCost;
    /**
     * Distributor's amount of needed energy
     */
    private final int energyNeededKW;
    /**
     * Distributor's strategy based on which producers are chosen
     */
    private final EnergyChoiceStrategy producerStrategy;
    /**
     * Distributor's current production cost
     */
    private int productionCost;
    /**
     * Distributor's current profit
     */
    private int profit;
    /**
     * Distributor's bankruptcy state
     */
    private boolean isBankrupt;
    /**
     * Distributor's list of issued contracts that are currently active
     */
    private final List<Contract> contracts;
    /**
     * Distributor's contract rate to be offered for current month
     */
    private int currentContractRate;
    /**
     * Distributor's list of producers
     */
    private final List<Producer> producers;
    /**
     * Distributor's flag which indicates if producers need to be updated
     */
    private boolean toUpdate;

    public Distributor(final int id, final int contractLength, final int initialBudget,
                       final int initialInfrastructureCost, final int energyNeededKW,
                       final String producerStrategy) {
        this.id = id;
        this.contractLength = contractLength;
        this.budget = initialBudget;
        this.infrastructureCost = initialInfrastructureCost;
        this.energyNeededKW = energyNeededKW;
        EnergyChoiceStrategyType strategyType = Utils.convertToStrategy(producerStrategy);
        this.producerStrategy = EnergyChoiceStrategyFactory.createStrategy(strategyType, this);
        this.isBankrupt = false;
        this.contracts = new ArrayList<>();
        this.producers = new ArrayList<>();
        this.toUpdate = false;
    }

    public int getId() {
        return id;
    }

    public int getContractLength() {
        return contractLength;
    }

    public int getBudget() {
        return budget;
    }

    public int getInfrastructureCost() {
        return infrastructureCost;
    }

    public int getEnergyNeededKW() {
        return energyNeededKW;
    }

    public EnergyChoiceStrategy getProducerStrategy() {
        return producerStrategy;
    }

    public int getProductionCost() {
        return productionCost;
    }

    public int getProfit() {
        return profit;
    }

    public boolean isBankrupt() {
        return isBankrupt;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public boolean isToUpdate() {
        return toUpdate;
    }

    public int getCurrentContractRate() {
        return currentContractRate;
    }

    /**
     * Updates infrastructure cost
     * @param infrastructureCost new infrastructure cost
     */
    public void setInfrastructureCost(final int infrastructureCost) {
        this.infrastructureCost = infrastructureCost;
    }

    /**
     * Updates production cost and profit
     * @param productionCost new production cost
     */
    public void setProductionCost(final int productionCost) {
        this.productionCost = productionCost;
        // Recalculate profit for new production cost
        profit = Utils.getProfit(productionCost);
    }

    @Override
    public void processMonth() {
        // Do nothing if bankrupt
        if (isBankrupt) {
            return;
        }
        // Decrement length of all contracts
        decrementContracts();
        // Pay monthly costs
        budget -= Utils.getMonthlyCost(infrastructureCost, productionCost,
                getNumActiveContracts());
        // Remove contracts that have ended
        removeEndedContracts();
        // Declare bankruptcy and clear contracts if budget goes negative
        if (budget < 0) {
            isBankrupt = true;
            contracts.clear();
            for (Producer producer : producers) {
                producer.getDistributors().remove(this);
            }
            producers.clear();
        }
    }

    /**
     * Take a payment form a consumer
     * @param payment amount payed by consumer
     */
    protected void takePayment(final int payment) {
        budget += payment;
    }

    /**
     * Remove contracts that have been fulfilled or whose consumers are bankrupt
     */
    private void removeEndedContracts() {
        // Remove contracts if remaining months is -1 (instead of 0 to be able to display contracts
        // that have ended in final round) or if consumer is bankrupt
        contracts.removeIf(contract -> contract.getContractLength() == -1
                || contract.getConsumer().isBankrupt());
    }

    /**
     * Decrement contract length at the end of each month
     */
    private void decrementContracts() {
        // Decrease number of months remaining on all contracts
        for (Contract contract : contracts) {
            contract.decreaseLength();
        }
    }

    /**
     * Calculate number of contracts which have 0 or higher length
     * @return number of active contracts
     */
    private int getNumActiveContracts() {
        int num = 0;
        // Get number of contracts with remaining months greater than 0
        for (Contract contract : contracts) {
            if (contract.getContractLength() >= 0) {
                num++;
            }
        }
        return num;
    }

    /**
     * Calculate the monthly rate this distributor can give on contract
     */
    public void calculateContractRate() {
        if (getNumActiveContracts() == 0) {
            // Calculate using no consumer formula
            currentContractRate =  Utils.getFinalPriceNoConsumers(infrastructureCost,
                    productionCost, profit);
        } else {
            // Calculate using number of active contracts
            currentContractRate =  Utils.getContractFinalPrice(infrastructureCost,
                    getNumActiveContracts(), productionCost, profit);
        }
    }

    /**
     * Creates a contract to be given to a consumer who requests it
     * @param consumer consumer requesting contract
     * @return contract for consumer
     */
    protected Contract generateContract(final Consumer consumer) {
        // Create new contract for given consumer and add to list of contracts
        Contract contract = new Contract(consumer, contractLength, currentContractRate, this);
        contracts.add(contract);
        return contract;
    }

    /**
     * Reapplies producer picking strategy
     */
    public void applyStrategy() {
        for (Producer producer : producers) {
            producer.getDistributors().remove(this);
        }
        producers.clear();
        producerStrategy.pickProducers();
    }

    /**
     * Calculates production cost for this round
     */
    public void calculateProductionCost() {
        int[] energy = new int[producers.size()];
        double[] price = new double[producers.size()];
        int i = 0, cost;
        for (Producer producer : producers) {
            energy[i] = producer.getEnergyPerDistributor();
            price[i] = producer.getPriceKW();
            i++;
        }
        cost = Utils.getProductionCost(energy, price);
        setProductionCost(cost);
    }

    @Override
    public void update(Observable o, Object arg) {
        toUpdate = true;
    }

    /**
     * Perform updates if flag is true
     */
    public void performUpdate() {
        if (!toUpdate) {
            return;
        }
        applyStrategy();
        calculateProductionCost();
    }
}

package entities;

import utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the distributor entity
 */
public final class Distributor implements Entity {
    /**
     * Distributor's given id
     */
    private final int id;
    /**
     * Distributor's current contract length
     */
    private int contractLength;
    /**
     * Distributor's current budget
     */
    private int budget;
    /**
     * Distributor's current infrastructure cost
     */
    private int infrastructureCost;
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
    private int currentContractRate;

    public Distributor(final int id, final int contractLength, final int initialBudget,
                       final int initialInfrastructureCost, final int initialProductionCost) {
        this.id = id;
        this.contractLength = contractLength;
        this.budget = initialBudget;
        this.infrastructureCost = initialInfrastructureCost;
        this.productionCost = initialProductionCost;
        this.profit = Utils.getProfit(initialProductionCost);
        this.isBankrupt = false;
        this.contracts = new ArrayList<>();
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

    public int getCurrentContractRate() {
        return currentContractRate;
    }

    /**
     * Updates contract length
     * @param contractLength new contract length
     */
    public void setContractLength(final int contractLength) {
        this.contractLength = contractLength;
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
        profit = Utils.getProfit(productionCost);
    }

    @Override
    public void processMonth() {
        if (isBankrupt) {
            return;
        }
        decrementContracts();
        budget -= Utils.getMonthlyCost(infrastructureCost, productionCost,
                getNumActiveContracts());
        removeEndedContracts();
        if (budget < 0) {
            isBankrupt = true;
            contracts.clear();
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
        contracts.removeIf(contract -> contract.getContractLength() == -1
                || contract.getConsumer().isBankrupt());
    }

    /**
     * Decrement contract length at the end of each month
     */
    private void decrementContracts() {
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
            currentContractRate =  Utils.getFinalPriceNoConsumers(infrastructureCost,
                    productionCost, profit);
        } else {
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
        Contract contract = new Contract(consumer, contractLength, currentContractRate, this);
        contracts.add(contract);
        return contract;
    }
}

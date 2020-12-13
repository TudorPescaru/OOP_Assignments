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

    /**
     * Take a payment form a consumer
     * @param payment amount payed by consumer
     */
    protected void takePayment(final int payment) {
        budget += payment;
    }

    /**
     * Remove a contract when it has been fulfilled
     * @param contract contract to be removed
     */
    protected void removeContract(final Contract contract) {
        contracts.remove(contract);
    }

    /**
     * Calculate the monthly rate this distributor can give on contract
     * @return monthly rate of contract
     */
    protected int getContractRate() {
        if (contracts.size() == 0) {
            return Utils.getFinalPriceNoConsumers(infrastructureCost, productionCost, profit);
        } else {
            return Utils.getContractFinalPrice(infrastructureCost, contracts.size(),
                    productionCost, profit);
        }
    }

    /**
     * Creates a contract to be given to a consumer who requests it
     * @param consumerId id of consumer requesting contract
     * @return contract for consumer
     */
    protected Contract generateContract(final int consumerId) {
        int contractRate = getContractRate();
        Contract contract = new Contract(consumerId, contractLength, contractRate, this);
        contracts.add(contract);
        return contract;
    }

    @Override
    public void processMonth() {
        int monthlyCost = Utils.getMonthlyCost(infrastructureCost, productionCost,
                                                contracts.size());
        if (monthlyCost <= budget) {
            budget -= monthlyCost;
        } else {
            isBankrupt = true;
        }
    }
}

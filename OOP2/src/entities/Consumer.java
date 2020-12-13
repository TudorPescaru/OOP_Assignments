package entities;

import database.Database;
import utils.Utils;

/**
 * This class defines the consumer entity
 */
public final class Consumer implements Entity {
    /**
     * Consumer's given id
     */
    private final int id;
    /**
     * Consumer's current budget
     */
    private int budget;
    /**
     * Consumer's given monthly income
     */
    private final int monthlyIncome;
    /**
     * Consumer's bankruptcy state
     */
    private boolean isBankrupt;
    /**
     * Consumer's overdue payment state
     */
    private boolean hasMissedMonth;
    /**
     * Consumer's current contract
     */
    private Contract contract;
    /**
     * Consumer's previously paid monthly contract cost
     */
    private int previousCost;

    public Consumer(final int id, final int initialBudget, final int monthlyIncome) {
        this.id = id;
        this.budget = initialBudget;
        this.monthlyIncome = monthlyIncome;
        this.isBankrupt = false;
        this.hasMissedMonth = false;
        this.contract = null;
    }

    public int getId() {
        return id;
    }

    public int getBudget() {
        return budget;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    public boolean isBankrupt() {
        return isBankrupt;
    }

    public boolean isHasMissedMonth() {
        return hasMissedMonth;
    }

    public Contract getContract() {
        return contract;
    }

    @Override
    public void processMonth() {
        if (isBankrupt) {
            return;
        }
        addMonthlyIncome();
        if (contract == null || contract.getContractLength() == 0) {
            getNewContract();
        }
        payMonthlyRate();
    }

    /**
     * Add monthly income to current budget
     */
    private void addMonthlyIncome() {
        budget += monthlyIncome;
    }

    /**
     * Get a new contract for this consumer
     */
    private void getNewContract() {
        int lowestRate = Integer.MAX_VALUE;
        Distributor lowestRateDistributor = null;
        Database database = Database.getInstance();
        for (Distributor distributor : database.getDistributorsMap().values()) {
            if (distributor.getContractRate() < lowestRate) {
                lowestRate = distributor.getContractRate();
                lowestRateDistributor = distributor;
            }
        }
        if (lowestRateDistributor != null) {
            contract = lowestRateDistributor.generateContract(id);
        }
    }

    /**
     * Subtract monthly contract rate from current budget
     */
    private void payMonthlyRate() {
        if (this.hasMissedMonth) {
            int penalty = Utils.getPenaltyPayment(previousCost, contract.getMonthlyCost());
            if (penalty > budget) {
                isBankrupt = true;
            } else {
                budget -= penalty;
                contract.getDistributor().takePayment(penalty);
                hasMissedMonth = false;
            }
        } else {
            if (contract.getMonthlyCost() <= budget) {
                budget -= contract.getMonthlyCost();
                contract.getDistributor().takePayment(contract.getMonthlyCost());
            } else {
                hasMissedMonth = true;
            }
        }
        previousCost = contract.getMonthlyCost();
        contract.decreaseLength();
    }
}

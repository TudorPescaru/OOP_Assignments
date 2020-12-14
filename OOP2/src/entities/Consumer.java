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
    /**
     * Consumer's previous distributor
     */
    private Distributor previousDistributor;

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
        // Do nothing if bankrupt
        if (isBankrupt) {
            return;
        }
        // Add monthly income to budget
        addMonthlyIncome();
        // Get new contract if consumer has no contract or current contract has expired
        if (contract == null || contract.getContractLength() == 0) {
            getNewContract();
        }
        // Get new contract and clear existing penalty if distributor goes bankrupt
        if (contract.getDistributor().isBankrupt()) {
            getNewContract();
            hasMissedMonth = false;
        }
        // Pay monthly rate of contract
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
        // Declare initial values used for comparison
        int lowestRate = Integer.MAX_VALUE;
        Distributor lowestRateDistributor = null;
        // Access database
        Database database = Database.getInstance();
        // Iterate through distributors in database and find distributor with lowest rate that is
        // not bankrupt
        for (Distributor distributor : database.getDistributorsMap().values()) {
            if (distributor.getCurrentContractRate() < lowestRate && !distributor.isBankrupt()) {
                lowestRate = distributor.getCurrentContractRate();
                lowestRateDistributor = distributor;
            }
        }
        // If distributor has been found get new contract form distributor
        if (lowestRateDistributor != null) {
            contract = lowestRateDistributor.generateContract(this);
        }
    }

    /**
     * Subtract monthly contract rate from current budget
     */
    private void payMonthlyRate() {
        // Check if consumer has to pay a penalty
        if (hasMissedMonth) {
            // Calculate penalty to be paid
            int penalty = Utils.getPenaltyPayment(previousCost, contract.getMonthlyCost());
            // Declare bankruptcy if penalty cannot be paid
            if (penalty > budget) {
                isBankrupt = true;
            } else {
                // Pay penalty
                budget -= penalty;
                // Check if month missed belongs to the same contract or distributor
                if (previousDistributor == contract.getDistributor()) {
                    // Give the entire sum to the current distributor
                    contract.getDistributor().takePayment(penalty);
                } else {
                    // Pay previous distributor the increased amount for missed month
                    previousDistributor.takePayment(penalty - contract.getMonthlyCost());
                    // Pay current distributor the cost of current month
                    contract.getDistributor().takePayment(contract.getMonthlyCost());
                }
                hasMissedMonth = false;
            }
        } else {
            // Check if consumer can pay cost of current month
            if (contract.getMonthlyCost() <= budget) {
                // Pay the cost of current month to distributor
                budget -= contract.getMonthlyCost();
                contract.getDistributor().takePayment(contract.getMonthlyCost());
            } else {
                // Apply penalty for next month
                hasMissedMonth = true;
            }
        }
        // Keep track of previous distributor and month cost
        previousCost = contract.getMonthlyCost();
        previousDistributor = contract.getDistributor();
    }
}

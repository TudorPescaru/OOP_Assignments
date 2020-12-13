package utils;

/**
 * This class contains methods used to calculate various elements
 */
public final class Utils {
    private Utils() {
    }

    /**
     * Calculate the payment required for current month in case of
     * failure to pay during previous month
     * @param oldBill sum required to pay for previous month
     * @param newBill sum required to pay for current month
     * @return payment with penalty
     */
    public static int getPenaltyPayment(final int oldBill, final int newBill) {
        return (int) Math.round(Math.floor(Constants.PAYMENTPENALTY * oldBill)) + newBill;
    }

    /**
     * Calculates final price of a contract offered by distributor
     * @param infrastructureCost cost of infrastructure
     * @param numCurrentConsumers number of current consumers the distributor has
     * @param productionCost cost of production
     * @param profit profit the distributor makes
     * @return final price of contract
     */
    public static int getContractFinalPrice(final int infrastructureCost,
                                            final int numCurrentConsumers,
                                            final int productionCost,
                                            final int profit) {
        return (int) Math.round(Math.floor((float) infrastructureCost / numCurrentConsumers)
                + productionCost + profit);
    }

    /**
     * Calculates final price of a contract offered by distributors if it has no consumers
     * @param infrastructureCost cost of infrastructure
     * @param productionCost number of current consumers the distributor has
     * @param profit cost of production
     * @return final price of contract
     */
    public static int getFinalPriceNoConsumers(final int infrastructureCost,
                                               final int productionCost,
                                               final int profit) {
        return infrastructureCost + productionCost + profit;
    }

    /**
     * Calculates profit of a distributor
     * @param productionCost cost of production
     * @return profit of a distributor
     */
    public static int getProfit(final int productionCost) {
        return (int) Math.round(Math.floor(Constants.PROFITMULTIPLIER * productionCost));
    }

    /**
     * Calculates the monthly cost of a distributor
     * @param infrastructureCost cost of infrastructure
     * @param productionCost cost of production
     * @param numCurrentConsumers number of current consumers the distributor has
     * @return monthly cost of distributor
     */
    public static int getMonthlyCost(final int infrastructureCost, final int productionCost,
                                     final int numCurrentConsumers) {
        return infrastructureCost + productionCost * numCurrentConsumers;
    }
}

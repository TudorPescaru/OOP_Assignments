package fileio;

/**
 * This class contains input data of an initial or new consumer
 */
public final class ConsumerInputData {
    /**
     * Consumer's id
     */
    private final int id;
    /**
     * Consumer's initial budget
     */
    private final int initialBudget;
    /**
     * Consumer's monthly income
     */
    private final int monthlyIncome;

    public ConsumerInputData(final int id, final int initialBudget, final int monthlyIncome) {
        this.id = id;
        this.initialBudget = initialBudget;
        this.monthlyIncome = monthlyIncome;
    }

    public int getId() {
        return id;
    }

    public int getInitialBudget() {
        return initialBudget;
    }

    public int getMonthlyIncome() {
        return monthlyIncome;
    }

    @Override
    public String toString() {
        return "ConsumerInputData{"
                + "id=" + id
                + ", initialBudget=" + initialBudget
                + ", monthlyIncome=" + monthlyIncome
                + '}';
    }
}

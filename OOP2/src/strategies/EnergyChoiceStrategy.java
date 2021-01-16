package strategies;

/**
 * This interface defines the generic strategy for picking producers
 */
public interface EnergyChoiceStrategy {
    /**
     * Picks producers for a distributor based on an algorithm
     */
    void pickProducers();

    /**
     * Return the type of strategy applied
     * @return type of strategy
     */
    EnergyChoiceStrategyType getStrategyType();
}

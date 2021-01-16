package strategies;

import entities.Distributor;

/**
 * This class implements a factory for strategy implementations
 */
public final class EnergyChoiceStrategyFactory {
    private EnergyChoiceStrategyFactory() {
    }

    /**
     * Factory method for creating strategy instance
     * @param strategyType type of implementation for strategy
     * @param distributor distributor who will apply strategy
     * @return strategy instance
     */
    public static EnergyChoiceStrategy createStrategy(EnergyChoiceStrategyType strategyType,
                                               Distributor distributor) {
        return switch (strategyType) {
            case GREEN -> new GreenStrategy(strategyType, distributor);
            case PRICE -> new PriceStrategy(strategyType, distributor);
            case QUANTITY -> new QuantityStrategy(strategyType, distributor);
        };
    }
}

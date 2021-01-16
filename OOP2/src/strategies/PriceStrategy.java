package strategies;

import database.Database;
import entities.Distributor;
import entities.Producer;

import java.util.ArrayList;

/**
 * This class implements the price strategy for picking producers
 */
public final class PriceStrategy implements EnergyChoiceStrategy {
    /**
     * Distributor who applies this strategy
     */
    private final Distributor distributor;
    /**
     * Type of strategy this class implements
     */
    private final EnergyChoiceStrategyType strategyType;

    public PriceStrategy(final EnergyChoiceStrategyType strategyType,
                         final Distributor distributor) {
        this.strategyType = strategyType;
        this.distributor = distributor;
    }

    @Override
    public void pickProducers() {
        Database database = Database.getInstance();
        ArrayList<Producer> producers = new ArrayList<>(database.getProducersMap().values());
        producers.sort((p1, p2) -> {
            int priceCompare = Double.compare(p1.getPriceKW(), p2.getPriceKW());
            if (priceCompare != 0) {
                return priceCompare;
            } else {
                int quantityCompare = Integer.compare(p1.getEnergyPerDistributor(),
                                                        p2.getEnergyPerDistributor());
                if (quantityCompare != 0) {
                    return -quantityCompare;
                } else {
                    return Integer.compare(p1.getId(), p2.getId());
                }
            }
        });
        int needed = distributor.getEnergyNeededKW();
        for (Producer producer : producers) {
            if (needed < 0) {
                break;
            }
            if (producer.getDistributors().size() < producer.getMaxDistributors()) {
                needed -= producer.getEnergyPerDistributor();
                producer.getDistributors().add(distributor);
                producer.addObserver(distributor);
                distributor.getProducers().add(producer);
            }
        }
    }

    public EnergyChoiceStrategyType getStrategyType() {
        return strategyType;
    }
}

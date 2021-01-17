package strategies;

import database.Database;
import entities.Distributor;
import entities.Producer;

import java.util.ArrayList;

/**
 * This class implements the green strategy for picking producers
 */
public final class GreenStrategy implements EnergyChoiceStrategy {
    /**
     * Distributor who applies this strategy
     */
    private final Distributor distributor;
    /**
     * Type of strategy this class implements
     */
    private final EnergyChoiceStrategyType strategyType;

    public GreenStrategy(final EnergyChoiceStrategyType strategyType,
                         final Distributor distributor) {
        this.strategyType = strategyType;
        this.distributor = distributor;
    }

    @Override
    public void pickProducers() {
        // Access database instance
        Database database = Database.getInstance();
        // Get list of producers from database
        ArrayList<Producer> producers = new ArrayList<>(database.getProducersMap().values());
        // Sort based on green strategy
        producers.sort((p1, p2) -> {
            // Compare producer energy type renewability
            int boolCompare = Boolean.compare(p1.getEnergyType().isRenewable(),
                                                p2.getEnergyType().isRenewable());
            if (boolCompare != 0) {
                // Move producers with renewable energy to front
                return -boolCompare;
            } else {
                // Compare producer price if same renewability
                int priceCompare = Double.compare(p1.getPriceKW(), p2.getPriceKW());
                if (priceCompare != 0) {
                    // Move producers with lowest price to front
                    return priceCompare;
                } else {
                    // Compare producer quantity if same renewability and price
                    int quantityCompare = Integer.compare(p1.getEnergyPerDistributor(),
                                                            p2.getEnergyPerDistributor());
                    if (quantityCompare != 0) {
                        // Move producers with most quantity to front
                        return -quantityCompare;
                    } else {
                        // If same renewability, price and quantity sort by id
                        return Integer.compare(p1.getId(), p2.getId());
                    }
                }
            }
        });
        // Store amount of energy needed to decrease it while adding producers
        int needed = distributor.getEnergyNeededKW();
        // Iterate over producers and find the appropriate ones
        for (Producer producer : producers) {
            // Stop when distributor gets enough energy
            if (needed <= 0) {
                break;
            }
            // If producer can accept more distributors process producer
            if (producer.getDistributors().size() < producer.getMaxDistributors()) {
                // Subtract offered energy from energy needed
                needed -= producer.getEnergyPerDistributor();
                // Add distributor to producer's list of distributor
                producer.getDistributors().add(distributor);
                // Add distributor as an observer for this producer
                producer.addObserver(distributor);
                // Add producer to distributor's list of producers
                distributor.getProducers().add(producer);
            }
        }
    }

    public EnergyChoiceStrategyType getStrategyType() {
        return strategyType;
    }
}

package entities;

import utils.Utils;

import java.util.ArrayList;
import java.util.Observable;

/**
 * This class defines the distributor entity
 */
public final class Producer extends Observable implements Entity {
    /**
     * Producer's given id
     */
    private final int id;
    /**
     * Producer's type of energy produced
     */
    private final EnergyType energyType;
    /**
     * Producer's maximum number of distributors to whom it can offer
     */
    private final int maxDistributors;
    /**
     * Producer's price for energy produced
     */
    private final double priceKW;
    /**
     * Producer's amount of energy it can offer tot distributors
     */
    private int energyPerDistributor;
    /**
     * Producer's list of distributors
     */
    private final ArrayList<Distributor> distributors;
    /**
     * Producer's history of distributors for each month
     */
    private final ArrayList<ArrayList<Distributor>> monthlyStats;

    public Producer(final int id, final String energyType, final int maxDistributors,
                    final double priceKW, final int energyPerDistributor) {
        this.id = id;
        this.energyType = Utils.convertToEnergyType(energyType);
        this.maxDistributors = maxDistributors;
        this.priceKW = priceKW;
        this.energyPerDistributor = energyPerDistributor;
        this.distributors = new ArrayList<>();
        this.monthlyStats = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public EnergyType getEnergyType() {
        return energyType;
    }

    public int getMaxDistributors() {
        return maxDistributors;
    }

    public double getPriceKW() {
        return priceKW;
    }

    public int getEnergyPerDistributor() {
        return energyPerDistributor;
    }

    /**
     * Update energyPerDistributor and perform observer operations
     * @param energyPerDistributor new energyPerDistributor value
     */
    public void setEnergyPerDistributor(int energyPerDistributor) {
        this.energyPerDistributor = energyPerDistributor;
        this.setChanged();
        this.notifyObservers();
    }

    public ArrayList<Distributor> getDistributors() {
        return distributors;
    }

    public ArrayList<ArrayList<Distributor>> getMonthlyStats() {
        return monthlyStats;
    }

    @Override
    public void processMonth() {
        monthlyStats.add(new ArrayList<>(distributors));
    }
}

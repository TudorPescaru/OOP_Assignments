package fileio;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import database.Database;
import entities.Consumer;
import entities.Contract;
import entities.Distributor;
import entities.Producer;
import utils.Constants;

import java.nio.file.Paths;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class writes simulation results in JSON format to an output file
 */
public final class Writer {
    /**
     * The file where the data will be written
     */
    private final String outFile;

    public Writer(final String path) {
        this.outFile = path;
    }

    /**
     * Writes output of game simulation to output file and closes it
     */
    public void writeOutput() {
        // Create json object to be written
        Map<String, Object> jsonOutput = new LinkedHashMap<>();
        // Get array of json output objects for consumers
        List<Map<String, Object>> jsonConsumers = writeConsumers();
        // Add array for consumers to output object
        jsonOutput.put(Constants.CONSUMERS, jsonConsumers);
        // Get array of json output objects for distributors
        List<Map<String, Object>> jsonDistributors = writeDistributors();
        // Add array for distributors to output objects
        jsonOutput.put(Constants.DISTRIBUTORS, jsonDistributors);
        // Get array of json output objects for producers
        List<Map<String, Object>> jsonProducers = writeProducers();
        // Add array for producers to output objects
        jsonOutput.put(Constants.ENERGYPRODUCERS, jsonProducers);
        // Write to output file and close
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            writer.writeValue(Paths.get(outFile).toFile(), jsonOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create array of consumer jsons with data from database
     * @return array of json objects containing consumer data
     */
    private List<Map<String, Object>> writeConsumers() {
        // Create json array for consumers
        List<Map<String, Object>> jsonConsumers = new ArrayList<>();
        Database database = Database.getInstance();

        // Convert each consumer object to json object
        for (Consumer consumer : database.getConsumersMap().values()) {
            Map<String, Object> jsonConsumer = new LinkedHashMap<>();

            jsonConsumer.put(Constants.ID, consumer.getId());
            jsonConsumer.put(Constants.ISBANKRUPT, consumer.isBankrupt());
            jsonConsumer.put(Constants.BUDGET, consumer.getBudget());

            jsonConsumers.add(jsonConsumer);
        }
        return jsonConsumers;
    }

    /**
     * Create array of distributor jsons with data from database
     * @return array of json objects containing distributor data
     */
    private List<Map<String, Object>> writeDistributors() {
        // Create json array for distributors
        List<Map<String, Object>> jsonDistributors = new ArrayList<>();
        Database database = Database.getInstance();

        // Convert each distributor object to json object
        for (Distributor distributor : database.getDistributorsMap().values()) {
            Map<String, Object> jsonDistributor = new LinkedHashMap<>();

            jsonDistributor.put(Constants.ID, distributor.getId());
            jsonDistributor.put(Constants.ENERGYNEEDEDKW, distributor.getEnergyNeededKW());
            jsonDistributor.put(Constants.CONTRACTCOST, distributor.getCurrentContractRate());
            jsonDistributor.put(Constants.BUDGET, distributor.getBudget());
            jsonDistributor.put(Constants.PRODUCERSTRATEGY, distributor.getProducerStrategy()
                                                            .getStrategyType().getLabel());
            jsonDistributor.put(Constants.ISBANKRUPT, distributor.isBankrupt());

            // Convert distributor's contract objects to json objects
            List<Map<String, Object>> jsonContracts = writeContracts(distributor);

            jsonDistributor.put(Constants.CONTRACTS, jsonContracts);

            jsonDistributors.add(jsonDistributor);
        }
        return jsonDistributors;
    }

    /**
     * Create array of contract jsons with data from a distributor's contract list
     * @param distributor distributor whose contracts are to be converted to json
     * @return array of json objects containing contract data
     */
    private List<Map<String, Object>> writeContracts(final Distributor distributor) {
        // Create json array for contracts
        List<Map<String, Object>> jsonContracts = new ArrayList<>();

        // Convert each of the given distributor's contract objects to json objects
        for (Contract contract : distributor.getContracts()) {
            Map<String, Object> jsonContract = new LinkedHashMap<>();

            jsonContract.put(Constants.CONSUMERID, contract.getConsumer().getId());
            jsonContract.put(Constants.PRICE, contract.getMonthlyCost());
            jsonContract.put(Constants.REMAINEDCONTRACTMONTHS, contract.getContractLength());

            jsonContracts.add(jsonContract);
        }
        return jsonContracts;
    }

    /**
     * Create array of producer jsons with data from database
     * @return array of json objects containing distributor data
     */
    private List<Map<String, Object>> writeProducers() {
        // Create json array for producers
        List<Map<String, Object>> jsonProducers = new ArrayList<>();
        Database database = Database.getInstance();

        // Convert each distributor object to json object
        for (Producer producer : database.getProducersMap().values()) {
            Map<String, Object> jsonProducer = new LinkedHashMap<>();

            jsonProducer.put(Constants.ID, producer.getId());
            jsonProducer.put(Constants.MAXDISTRIBUTORS, producer.getMaxDistributors());
            jsonProducer.put(Constants.PRICEKW, producer.getPriceKW());
            jsonProducer.put(Constants.ENERGYTYPE, producer.getEnergyType().getLabel());
            jsonProducer.put(Constants.ENERGYPERDISTRIBUTOR, producer.getEnergyPerDistributor());

            // Convert producer's monthly stats to json objects
            List<Map<String, Object>> jsonMonthlyStats = writeStats(producer);

            jsonProducer.put(Constants.MONTHLYSTATS, jsonMonthlyStats);

            jsonProducers.add(jsonProducer);
        }
        return jsonProducers;
    }

    /**
     * Create array of monthly stats jsons with data from a producer's monthly stats
     * @param producer producer whose monthly stats are to be converted to json
     * @return array of json objects containing monthly stats data
     */
    private List<Map<String, Object>> writeStats(final Producer producer) {
        // Create json array for monthly stats
        List<Map<String, Object>> jsonMonthlyStats = new ArrayList<>();

        // Convert each of the producer's monthly stats to json objects
        for (int i = 1; i < producer.getMonthlyStats().size(); i++) {
            Map<String, Object> stat = new LinkedHashMap<>();
            ArrayList<Integer> ids = new ArrayList<>();

            for (Distributor distributor : producer.getMonthlyStats().get(i)) {
                ids.add(distributor.getId());
            }

            Collections.sort(ids);

            stat.put(Constants.MONTH, i);
            stat.put(Constants.DISTRIBUTORSID, ids);

            jsonMonthlyStats.add(stat);
        }
        return jsonMonthlyStats;
    }
}

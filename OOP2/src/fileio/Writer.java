package fileio;

import database.Database;
import entities.Consumer;
import entities.Contract;
import entities.Distributor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This class writes simulation results in JSON format to an output file
 */
public final class Writer {
    /**
     * The file where the data will be written
     */
    private final FileWriter file;

    public Writer(final String path) throws IOException {
        this.file = new FileWriter(path);
    }

    /**
     * Writes output of game simulation to output file and closes it
     */
    public void writeOutput() {
        // Create json object to be written
        JSONObject jsonOutput = new JSONObject();
        // Get array of json output objects for consumers
        JSONArray jsonConsumers = writeConsumers();
        // Add array for consumers to output object
        jsonOutput.put("consumers", jsonConsumers);
        // Get array of json output objects for distributors
        JSONArray jsonDistributors = writeDistributors();
        // Add array for distributors to output objects
        jsonOutput.put("distributors", jsonDistributors);
        // Write to output file and close
        try {
            file.write(jsonOutput.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create array of consumer jsons with data from database
     * @return array of json objects containing consumer data
     */
    private JSONArray writeConsumers() {
        // Create json array for consumers
        JSONArray jsonConsumers = new JSONArray();
        Database database = Database.getInstance();

        // Convert each consumer object to json object
        for (Consumer consumer : database.getConsumersMap().values()) {
            JSONObject jsonConsumer = new JSONObject();

            jsonConsumer.put("id", consumer.getId());
            jsonConsumer.put("isBankrupt", consumer.isBankrupt());
            jsonConsumer.put("budget", consumer.getBudget());

            jsonConsumers.add(jsonConsumer);
        }
        return jsonConsumers;
    }

    /**
     * Create array of distributor jsons with data from database
     * @return array of json objects containing distributor data
     */
    private JSONArray writeDistributors() {
        // Create json array for distributors
        JSONArray jsonDistributors = new JSONArray();
        Database database = Database.getInstance();

        // Convert each distributor object to json object
        for (Distributor distributor : database.getDistributorsMap().values()) {
            JSONObject jsonDistributor = new JSONObject();

            jsonDistributor.put("id", distributor.getId());
            jsonDistributor.put("budget", distributor.getBudget());
            jsonDistributor.put("isBankrupt", distributor.isBankrupt());

            // Convert distributor's contract objects to json objects
            JSONArray jsonContracts = writeContracts(distributor);

            jsonDistributor.put("contracts", jsonContracts);

            jsonDistributors.add(jsonDistributor);
        }
        return jsonDistributors;
    }

    /**
     * Create array of contract jsons with data from a distributor's contract list
     * @param distributor distributor whose contracts are to be converted to json
     * @return array of json objects containing contract data
     */
    private JSONArray writeContracts(final Distributor distributor) {
        // Create json array for contracts
        JSONArray jsonContracts = new JSONArray();

        // Convert each of the given distributor's contract objects to json objects
        for (Contract contract : distributor.getContracts()) {
            JSONObject jsonContract = new JSONObject();

            jsonContract.put("consumerId", contract.getConsumer().getId());
            jsonContract.put("price", contract.getMonthlyCost());
            jsonContract.put("remainedContractMonths", contract.getContractLength());

            jsonContracts.add(jsonContract);
        }
        return jsonContracts;
    }
}

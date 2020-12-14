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
        JSONObject jsonOutput = new JSONObject();
        JSONArray jsonConsumers = writeConsumers();
        jsonOutput.put("consumers", jsonConsumers);
        JSONArray jsonDistributors = writeDistributors();
        jsonOutput.put("distributors", jsonDistributors);
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
        JSONArray jsonConsumers = new JSONArray();
        Database database = Database.getInstance();

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
        JSONArray jsonDistributors = new JSONArray();
        Database database = Database.getInstance();

        for (Distributor distributor : database.getDistributorsMap().values()) {
            JSONObject jsonDistributor = new JSONObject();

            jsonDistributor.put("id", distributor.getId());
            jsonDistributor.put("budget", distributor.getBudget());
            jsonDistributor.put("isBankrupt", distributor.isBankrupt());

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
        JSONArray jsonContracts = new JSONArray();

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

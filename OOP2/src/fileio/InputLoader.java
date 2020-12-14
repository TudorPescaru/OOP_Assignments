package fileio;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.Constants;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class loads the input from given file containing JSON data
 */
public final class InputLoader {
    /**
     * Path to file containing JSON data
     */
    private final String inputPath;

    public InputLoader(final String inputPath) {
        this.inputPath = inputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    /**
     * Parse the input JSON data and create separate objects with input
     * @return Input-type object containing objects created from input
     */
    public Input readData() {
        // Instantiate parser and elements which will form the input
        JSONParser jsonParser = new JSONParser();
        int numberOfTurns = 0;
        List<UpdatesInputData> updates = null;
        List<ConsumerInputData> consumers = new ArrayList<>();
        List<DistributorInputData> distributors = new ArrayList<>();

        try {
            // Parse json input file
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(inputPath));
            // Get number of turns
            numberOfTurns = Integer.parseInt(jsonObject.get(Constants.NUMTURNS).toString());
            // Get initial data
            JSONObject initialData = (JSONObject) jsonObject.get(Constants.INITIALDATA);
            // Get consumer data from initial data
            JSONArray jsonConsumers = (JSONArray) initialData.get(Constants.CONSUMERS);
            // Get distributor data from initial data
            JSONArray jsonDistributors = (JSONArray) initialData.get(Constants.DISTRIBUTORS);

            // Check if consumers were given in initial data
            if (jsonConsumers != null) {
                for (Object jsonConsumer : jsonConsumers) {
                    // Create consumer input data object from consumer json data
                    consumers.add(new ConsumerInputData(
                            Integer.parseInt(((JSONObject) jsonConsumer)
                                    .get(Constants.ID).toString()),
                            Integer.parseInt(((JSONObject) jsonConsumer)
                                    .get(Constants.INITIALBUDGET).toString()),
                            Integer.parseInt(((JSONObject) jsonConsumer)
                                    .get(Constants.MONTHLYINCOME).toString())
                    ));
                }
            } else {
                System.out.println("NO CONSUMERS GIVEN!");
            }

            // Check if consumers were given in initial data
            if (jsonDistributors != null) {
                for (Object jsonDistributor : jsonDistributors) {
                    // Create distributor input data object from distributor json data
                    distributors.add(new DistributorInputData(
                            Integer.parseInt(((JSONObject) jsonDistributor)
                                    .get(Constants.ID).toString()),
                            Integer.parseInt(((JSONObject) jsonDistributor)
                                    .get(Constants.CONTRACTLENGTH).toString()),
                            Integer.parseInt(((JSONObject) jsonDistributor)
                                    .get(Constants.INITIALBUDGET).toString()),
                            Integer.parseInt(((JSONObject) jsonDistributor)
                                    .get(Constants.INITIALINFRASTRUCTURECOST).toString()),
                            Integer.parseInt(((JSONObject) jsonDistributor)
                                    .get(Constants.INITIALPRODUCTIONCOST).toString())
                    ));
                }
            } else {
                System.out.println("NO DISTRIBUTORS GIVEN!");
            }

            // Get list of monthly update objects
            updates = readUpdates(jsonObject);

            // Set consumer and distributor lists as null to prevent errors
            if (jsonConsumers == null) {
                consumers = null;
            }

            if (jsonDistributors == null) {
                distributors = null;
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        // Return new input data object
        return new Input(numberOfTurns, consumers, distributors, updates);
    }

    /**
     * Read the monthly updates from the input data
     * @param jsonObject input data in JSON format
     * @return list of monthly updates
     */
    public List<UpdatesInputData> readUpdates(final JSONObject jsonObject) {
        // Initialise list of update data objects
        List<UpdatesInputData> updates = new ArrayList<>();
        // Get list of json updates
        JSONArray jsonUpdates = (JSONArray) jsonObject.get(Constants.MONTHLYUPDATES);

        // Check if updates were given in initial data
        if (jsonUpdates != null) {
            for (Object jsonIterator : jsonUpdates) {
                // Initialise lists of new consumers and distributor cost changes for each month
                List<ConsumerInputData> newConsumers = new ArrayList<>();
                List<ChangesInputData> costsChanges = new ArrayList<>();

                // Get json data for new consumers and cost changes
                JSONArray jsonConsumers = (JSONArray) ((JSONObject) jsonIterator)
                        .get(Constants.NEWCONSUMERS);
                JSONArray jsonChanges = (JSONArray) ((JSONObject) jsonIterator)
                        .get(Constants.COSTSCHANGES);

                // Check if new consumers were given in input data
                if (jsonConsumers != null) {
                    for (Object jsonConsumer : jsonConsumers) {
                        // Create consumer data object from json data
                        newConsumers.add(new ConsumerInputData(
                                Integer.parseInt(((JSONObject) jsonConsumer)
                                        .get(Constants.ID).toString()),
                                Integer.parseInt(((JSONObject) jsonConsumer)
                                        .get(Constants.INITIALBUDGET).toString()),
                                Integer.parseInt(((JSONObject) jsonConsumer)
                                        .get(Constants.MONTHLYINCOME).toString())
                        ));
                    }
                } else {
                    newConsumers = null;
                }

                // Check if cost changes were given in input data
                if (jsonChanges != null) {
                    for (Object jsonChange : jsonChanges) {
                        // Create cost change object from json data
                        costsChanges.add(new ChangesInputData(
                                Integer.parseInt(((JSONObject) jsonChange)
                                        .get(Constants.ID).toString()),
                                Integer.parseInt(((JSONObject) jsonChange)
                                        .get(Constants.INFRASTRUCTURECOST).toString()),
                                Integer.parseInt(((JSONObject) jsonChange)
                                        .get(Constants.PRODUCTIONCOST).toString())
                        ));
                    }
                } else {
                    costsChanges = null;
                }

                // Add updates object to list of updates
                updates.add(new UpdatesInputData(newConsumers, costsChanges));
            }
        } else {
            updates = null;
        }
        return updates;
    }
}

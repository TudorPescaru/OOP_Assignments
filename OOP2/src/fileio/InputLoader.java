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
        JSONParser jsonParser = new JSONParser();
        int numberOfTurns = 0;
        List<UpdatesInputData> updates = null;
        List<ConsumerInputData> consumers = new ArrayList<>();
        List<DistributorInputData> distributors = new ArrayList<>();

        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(inputPath));
            numberOfTurns = Integer.parseInt(jsonObject.get(Constants.NUMTURNS).toString());
            JSONObject initialData = (JSONObject) jsonObject.get(Constants.INITIALDATA);
            JSONArray jsonConsumers = (JSONArray) initialData.get(Constants.CONSUMERS);
            JSONArray jsonDistributors = (JSONArray) initialData.get(Constants.DISTRIBUTORS);

            if (jsonConsumers != null) {
                for (Object jsonConsumer : jsonConsumers) {
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

            if (jsonDistributors != null) {
                for (Object jsonDistributor : jsonDistributors) {
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

            updates = readUpdates(jsonObject);

            if (jsonConsumers == null) {
                consumers = null;
            }

            if (jsonDistributors == null) {
                distributors = null;
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return new Input(numberOfTurns, consumers, distributors, updates);
    }

    /**
     * Read the monthly updates from the input data
     * @param jsonObject input data in JSON format
     * @return list of monthly updates
     */
    public List<UpdatesInputData> readUpdates(final JSONObject jsonObject) {
        List<UpdatesInputData> updates = new ArrayList<>();
        JSONArray jsonUpdates = (JSONArray) jsonObject.get(Constants.MONTHLYUPDATES);

        if (jsonUpdates != null) {
            for (Object jsonIterator : jsonUpdates) {
                List<ConsumerInputData> newConsumers = new ArrayList<>();
                List<ChangesInputData> costsChanges = new ArrayList<>();

                JSONArray jsonConsumers = (JSONArray) ((JSONObject) jsonIterator)
                        .get(Constants.NEWCONSUMERS);
                JSONArray jsonChanges = (JSONArray) ((JSONObject) jsonIterator)
                        .get(Constants.COSTSCHANGES);

                if (jsonConsumers != null) {
                    for (Object jsonConsumer : jsonConsumers) {
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

                if (jsonChanges != null) {
                    for (Object jsonChange : jsonChanges) {
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

                updates.add(new UpdatesInputData(newConsumers, costsChanges));
            }
        } else {
            updates = null;
        }
        return updates;
    }
}

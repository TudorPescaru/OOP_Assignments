package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import database.Database;
import fileio.ActionInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


/**
 * The entry point to this homework. It runs the checker that tests your implementation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        // Instantiate database singleton, give it the input and convert it to usable objects
        Database database = Database.getInstance();
        database.processInput(input);

        // Iterate over actions in input and perform them
        for (ActionInputData action : input.getCommands()) {
            // Initialize resulting JSONObject for current action
            JSONObject resultObject = null;
            // Check action type
            switch (action.getActionType()) {
                case "command":
                    // Check command type
                    switch (action.getType()) {
                        case "view":
                            String viewMessage = database.userViewVideo(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", viewMessage);
                            break;
                        case "favorite":
                            String favoriteMessage = database.userFavoriteVideo(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", favoriteMessage);
                            break;
                        case "rating":
                            String rateMessage = database.userRateVideo(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", rateMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                case "query":
                    // Check type of object the query will be performed on
                    switch (action.getObjectType()) {
                        case "actors":
                            // Get main query criteria
                            switch (action.getCriteria()) {
                                case "average":
                                    String averageMessage = database.queryAverageActors(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", averageMessage);
                                    break;
                                case "awards":
                                    String awardsMessage = database.queryAwardsActors(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", awardsMessage);
                                    break;
                                case "filter_description":
                                    String descMessage = database.queryDescriptionActors(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", descMessage);
                                    break;
                                default:
                                    break;
                            }
                        case "movies": case "shows":
                            // Get main query criteria
                            switch (action.getCriteria()) {
                                case "ratings":
                                    String ratingsMessage = database.queryRatingVideo(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", ratingsMessage);
                                    break;
                                case "favorite":
                                    String favoriteMessage = database.queryFavoriteVideo(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", favoriteMessage);
                                    break;
                                case "longest":
                                    String longestMessage = database.queryLongestVideo(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", longestMessage);
                                    break;
                                case "most_viewed":
                                    String viewsMessage = database.queryMostViewedVideo(action);
                                    resultObject = fileWriter.writeFile(action.getActionId(),
                                                                "", viewsMessage);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "users":
                            String usersMessage = database.queryUsers(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", usersMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                case "recommendation":
                    // Get type of recommendation strategy to be applied
                    switch (action.getType()) {
                        case "standard":
                            String standardMessage = database.recommendStandard(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", standardMessage);
                            break;
                        case "best_unseen":
                            String bestMessage = database.recommendBestUnseen(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", bestMessage);
                            break;
                        case "popular":
                            String popularMessage = database.recommendPopular(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", popularMessage);
                            break;
                        case "favorite":
                            String favoriteMessage = database.recommendFavorite(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", favoriteMessage);
                            break;
                        case "search":
                            String searchMessage = database.recommendSearch(action);
                            resultObject = fileWriter.writeFile(action.getActionId(),
                                                        "", searchMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            // Add the resulting object to the output JSONArray
            if (resultObject != null) {
                arrayResult.add(resultObject);
            }
        }

        fileWriter.closeJSON(arrayResult);
    }
}

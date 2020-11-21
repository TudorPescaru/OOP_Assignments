package main;

import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import entertainment.Genre;
import entities.Actor;
import entities.Movie;
import entities.Show;
import entities.User;
import entities.Video;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

        //TODO add here the entry point to your implementation
        Map<String, Actor> actorsMap = new HashMap<>();
        Map<String, Video> videosMap = new HashMap<>();
        Map<String, User> usersMap = new HashMap<>();
        for (MovieInputData movieData : input.getMovies()) {
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : movieData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            Movie movie = new Movie(movieData.getTitle(), movieData.getYear(),
                                    movieData.getCast(), genres, movieData.getDuration());
            videosMap.put(movie.getTitle(), movie);
        }
        for (SerialInputData showData : input.getSerials()) {
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : showData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            Show show = new Show(showData.getTitle(), showData.getYear(),
                    showData.getCast(), genres, showData.getNumberSeason(), showData.getSeasons());
            videosMap.put(show.getTitle(), show);
        }
        for (ActorInputData actorData : input.getActors()) {
            ArrayList<Video> starredIn = new ArrayList<>();
            for (Video video : videosMap.values()) {
                if (video.getCast().contains(actorData.getName())) {
                    starredIn.add(video);
                }
            }
            Actor actor = new Actor(actorData.getName(), actorData.getCareerDescription(),
                                    starredIn, actorData.getAwards());
            actorsMap.put(actor.getName(), actor);
        }
        for (UserInputData userData : input.getUsers()) {
            Map<Video, Integer> seen = new HashMap<>();
            ArrayList<Video> favorites = new ArrayList<>();
            for (Video video : videosMap.values()) {
                if (userData.getHistory().containsKey(video.getTitle())) {
                    seen.put(video, userData.getHistory().get(video.getTitle()));
                }
                if (userData.getFavoriteMovies().contains(video.getTitle())) {
                    favorites.add(video);
                }
            }
            User user = new User(userData.getUsername(), userData.getSubscriptionType(),
                                    seen, favorites);
            usersMap.put(user.getUsername(), user);
        }

        for (ActionInputData action : input.getCommands()) {
            JSONObject resultObject = null;
            switch (action.getActionType()) {
                case "command":
                    User user = usersMap.get(action.getUsername());
                    Video video = videosMap.get(action.getTitle());
                    Double grade = action.getGrade();
                    int seasonNumber = action.getSeasonNumber();
                    switch (action.getType()) {
                        case "view":
                            String viewMessage = user.viewVideo(video);
                            resultObject = fileWriter.writeFile(action.getActionId(), "",
                                                                viewMessage);
                            break;
                        case "favorite":
                            String favoriteMessage = user.favoriteVideo(video);
                            resultObject = fileWriter.writeFile(action.getActionId(), "",
                                                                favoriteMessage);
                            break;
                        case "rating":
                            String rateMessage;
                            if (seasonNumber == 0) {
                                rateMessage = user.rateVideo((Movie) video, grade);
                            } else {
                                rateMessage = user.rateVideo((Show) video, seasonNumber,
                                                                        grade);
                            }
                            resultObject = fileWriter.writeFile(action.getActionId(), "",
                                                                rateMessage);
                            break;
                        default:
                            break;
                    }
                    break;
                case "query":
                    break;
                case "recommendation":
                    break;
                default:
                    break;
            }
            if (resultObject != null) {
                arrayResult.add(resultObject);
            }
        }

        fileWriter.closeJSON(arrayResult);
    }
}

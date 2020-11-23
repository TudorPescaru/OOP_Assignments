package database;

import entertainment.Genre;
import entities.Actor;
import entities.User;
import entities.Movie;
import entities.Show;
import entities.Video;
import fileio.Input;
import fileio.ActionInputData;
import fileio.ActorInputData;
import fileio.UserInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import utils.Utils;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public final class Database {

    /**
     * Original database input
     */
    private Input input;
    /**
     * Map of actor objects in database
     */
    private Map<String, Actor> actorsMap = new HashMap<>();
    /**
     * Map of video objects in database
     */
    private Map<String, Video> videosMap = new HashMap<>();
    /**
     * Map of user objects in database
     */
    private Map<String, User> usersMap = new HashMap<>();

    private Database() {
    }

    /**
     * Bill Pugh Singleton implementation
     */
    private static class Singleton {
        private static final Database INSTANCE = new Database();
    }

    public static Database getInstance() {
        return Singleton.INSTANCE;
    }

    public Input getInput() {
        return input;
    }

    public Map<String, Actor> getActorsMap() {
        return actorsMap;
    }

    public Map<String, Video> getVideosMap() {
        return videosMap;
    }

    public Map<String, User> getUsersMap() {
        return usersMap;
    }

    public void setInput(final Input input) {
        this.input = input;
    }

    /**
     * Converts elements given in input to more useful objects stored in maps
     */
    public void convertInput() {
        actorsMap.clear();
        videosMap.clear();
        usersMap.clear();
        for (MovieInputData movieData : this.input.getMovies()) {
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : movieData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            Movie movie = new Movie(movieData.getTitle(), movieData.getYear(),
                    movieData.getCast(), genres, movieData.getDuration());
            this.videosMap.put(movie.getTitle(), movie);
        }
        for (SerialInputData showData : this.input.getSerials()) {
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : showData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            Show show = new Show(showData.getTitle(), showData.getYear(),
                    showData.getCast(), genres, showData.getNumberSeason(), showData.getSeasons());
            this.videosMap.put(show.getTitle(), show);
        }
        for (ActorInputData actorData : this.input.getActors()) {
            ArrayList<Video> starredIn = new ArrayList<>();
            for (Video video : videosMap.values()) {
                if (video.getCast().contains(actorData.getName())) {
                    starredIn.add(video);
                }
            }
            Actor actor = new Actor(actorData.getName(), actorData.getCareerDescription(),
                    starredIn, actorData.getAwards());
            this.actorsMap.put(actor.getName(), actor);
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
            this.usersMap.put(user.getUsername(), user);
        }
    }

    /**
     * Performs the action of viewing a video for a certain user
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String userViewVideo(final ActionInputData action) {
        User user = this.usersMap.get(action.getUsername());
        Video video = this.videosMap.get(action.getTitle());
        return user.viewVideo(video);
    }

    /**
     * Performs the action of favoriting a video for a certain user
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String userFavoriteVideo(final ActionInputData action) {
        User user = this.usersMap.get(action.getUsername());
        Video video = this.videosMap.get(action.getTitle());
        return user.favoriteVideo(video);
    }

    /**
     * Performs the action of rating a video for a certain user
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String userRateVideo(final ActionInputData action) {
        User user = this.usersMap.get(action.getUsername());
        Video video = this.videosMap.get(action.getTitle());
        Double rating = action.getGrade();
        if (action.getSeasonNumber() == 0) {
            return user.rateVideo((Movie) video, rating);
        } else {
            return user.rateVideo((Show) video, action.getSeasonNumber(), rating);
        }
    }

    /**
     * Performs a query on actors based on average
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryAverageActors(final ActionInputData action) {
        int n = action.getNumber();
        ArrayList<Actor> actorsToSort = new ArrayList<>(this.actorsMap.values());
        actorsToSort.sort(Comparator.comparingDouble(
                            Actor::getFilmographyAverageRating).thenComparing(Actor::getName));
        actorsToSort.removeIf(actor -> actor.getFilmographyAverageRating().isNaN());
        StringBuilder averageMessage = new StringBuilder("Query result: [");
        if (action.getSortType().equals("desc")) {
            Collections.reverse(actorsToSort);
        }
        if (actorsToSort.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                averageMessage.append(actorsToSort.get(i).getName());
                averageMessage.append(", ");
            }
            averageMessage.append(actorsToSort.get(n - 1).getName());
        } else {
            for (int i = 0; i < actorsToSort.size() - 1; ++i) {
                averageMessage.append(actorsToSort.get(i).getName());
                averageMessage.append(", ");
            }
            averageMessage.append(actorsToSort.get(actorsToSort.size() - 1).getName());
        }
        averageMessage.append("]");
        return averageMessage.toString();
    }

    /**
     * Performs a query on actors based on awards
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryAwardsActors(final ActionInputData action) {
        StringBuilder awardsMessage = new StringBuilder("Query result: [");
        ArrayList<Actor> actorsWithAwards = new ArrayList<>();
        int awardsIndex = action.getFilters().size() - 1;
        ArrayList<String> filterAwards = new ArrayList<>(action.getFilters().get(awardsIndex));
        for (ActorInputData actorData : this.input.getActors()) {
            boolean containsAll = true;
            for (String award : filterAwards) {
                if (!actorData.getAwards().containsKey(Utils.stringToAwards(award))) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                actorsWithAwards.add(this.actorsMap.get(actorData.getName()));
            }
        }
        if (actorsWithAwards.size() != 0) {
            actorsWithAwards.sort(Comparator.comparingInt(Actor::getTotalAwards));
            appendToMessage(action, awardsMessage, actorsWithAwards);
        }
        awardsMessage.append("]");
        return awardsMessage.toString();
    }

    /**
     * Performs a query on actors based on description
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryDescriptionActors(final ActionInputData action) {
        StringBuilder descMessage = new StringBuilder("Query result: [");
        ArrayList<Actor> actorsMatchDesc = new ArrayList<>();
        int wordsIndex = action.getFilters().size() - 2;
        ArrayList<String> filterWords = new ArrayList<>(action.getFilters().get(wordsIndex));
        for (ActorInputData actorData : input.getActors()) {
            boolean containsAll = true;
            for (String word : filterWords) {
                if (!actorData.getCareerDescription().toLowerCase().contains(word)) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                actorsMatchDesc.add(this.actorsMap.get(actorData.getName()));
            }
        }
        if (actorsMatchDesc.size() != 0) {
            actorsMatchDesc.sort(Comparator.comparing(Actor::getName));
            appendToMessage(action, descMessage, actorsMatchDesc);
        }
        descMessage.append("]");
        return descMessage.toString();
    }

    /**
     * Helper function for queries above
     * @param action details of action to be performed
     * @param descMessage message to append to
     * @param actorsMatchDesc list of actors from which to append
     */
    private void appendToMessage(final ActionInputData action, final StringBuilder descMessage,
                                 final ArrayList<Actor> actorsMatchDesc) {
        if (action.getSortType().equals("desc")) {
            Collections.reverse(actorsMatchDesc);
        }
        for (int i = 0; i < actorsMatchDesc.size() - 1; ++i) {
            descMessage.append(actorsMatchDesc.get(i).getName());
            descMessage.append(", ");
        }
        descMessage.append(actorsMatchDesc.get(actorsMatchDesc.size()
                - 1).getName());
    }
}

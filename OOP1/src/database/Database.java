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

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
            actorsWithAwards.sort(Comparator.comparingInt(Actor::getTotalAwards)
                                            .thenComparing(Actor::getName));
            if (action.getSortType().equals("desc")) {
                Collections.reverse(actorsWithAwards);
            }
            for (int i = 0; i < actorsWithAwards.size() - 1; ++i) {
                awardsMessage.append(actorsWithAwards.get(i).getName());
                awardsMessage.append(", ");
            }
            awardsMessage.append(actorsWithAwards.get(actorsWithAwards.size() - 1).getName());
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
            if (action.getSortType().equals("desc")) {
                Collections.reverse(actorsMatchDesc);
            }
            for (int i = 0; i < actorsMatchDesc.size() - 1; ++i) {
                descMessage.append(actorsMatchDesc.get(i).getName());
                descMessage.append(", ");
            }
            descMessage.append(actorsMatchDesc.get(actorsMatchDesc.size() - 1).getName());
        }
        descMessage.append("]");
        return descMessage.toString();
    }

    /**
     * Performs a query on videos based on rating
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryRatingVideo(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder ratingMessage = new StringBuilder("Query result: [");
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        ArrayList<Video> videosToSort = new ArrayList<>();
        for (Video video : videosMap.values()) {
            if (year != null && year != video.getYear()) {
                continue;
            }
            if (genre != null && !video.getGenres().contains(genre)) {
                continue;
            }
            if (action.getObjectType().equals("movies") && video instanceof Show) {
                continue;
            }
            if (action.getObjectType().equals("shows") && video instanceof Movie) {
                continue;
            }
            if (Double.compare(video.getAverageRating(), 0.0) == 0) {
                continue;
            }
            videosToSort.add(video);
        }
        if (videosToSort.size() == 0) {
            ratingMessage.append("]");
            return ratingMessage.toString();
        }
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating)
                                    .thenComparing(Video::getTitle));
        if (videosToSort.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                ratingMessage.append(videosToSort.get(i).getTitle());
                ratingMessage.append(", ");
            }
            ratingMessage.append(videosToSort.get(n - 1).getTitle());
        } else {
            for (int i = 0; i < videosToSort.size() - 1; ++i) {
                ratingMessage.append(videosToSort.get(i).getTitle());
                ratingMessage.append(", ");
            }
            ratingMessage.append(videosToSort.get(videosToSort.size() - 1).getTitle());
        }
        ratingMessage.append("]");
        return ratingMessage.toString();
    }

    /**
     * Performs a query on videos based on number of favorites
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryFavoriteVideo(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder favoriteMessage = new StringBuilder("Query result: [");
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        Map<String, Integer> videosToFavorites = new LinkedHashMap<>();
        for (User user : usersMap.values()) {
            for (Video video : user.getFavorites()) {
                if (year != null && year != video.getYear()) {
                    continue;
                }
                if (genre != null && !video.getGenres().contains(genre)) {
                    continue;
                }
                if (action.getObjectType().equals("movies") && video instanceof Show) {
                    continue;
                }
                if (action.getObjectType().equals("shows") && video instanceof Movie) {
                    continue;
                }
                if (videosToFavorites.containsKey(video.getTitle())) {
                    videosToFavorites.put(video.getTitle(),
                            videosToFavorites.get(video.getTitle()) + 1);
                } else {
                    videosToFavorites.put(video.getTitle(), 1);
                }
            }
        }
        if (videosToFavorites.size() == 0) {
            favoriteMessage.append("]");
            return favoriteMessage.toString();
        }
        Map<String, Integer> videosToSort = new LinkedHashMap<>();
        if (action.getSortType().equals("desc")) {
            videosToFavorites.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            videosToFavorites.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        }
        ArrayList<String> sortedVideos = new ArrayList<>(videosToSort.keySet());
        if (videosToFavorites.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                favoriteMessage.append(sortedVideos.get(i));
                favoriteMessage.append(", ");
            }
            favoriteMessage.append(sortedVideos.get(n - 1));
        } else {
            for (int i = 0; i < sortedVideos.size() - 1; ++i) {
                favoriteMessage.append(sortedVideos.get(i));
                favoriteMessage.append(", ");
            }
            favoriteMessage.append(sortedVideos.get(sortedVideos.size() - 1));
        }
        favoriteMessage.append("]");
        return favoriteMessage.toString();
    }

    /**
     * Performs a query on videos based on length
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryLongestVideo(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder lengthMessage = new StringBuilder("Query result: [");
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        ArrayList<Video> videosToSort = new ArrayList<>();
        for (Video video : videosMap.values()) {
            if (year != null && year != video.getYear()) {
                continue;
            }
            if (genre != null && !video.getGenres().contains(genre)) {
                continue;
            }
            if (action.getObjectType().equals("movies") && video instanceof Show) {
                continue;
            }
            if (action.getObjectType().equals("shows") && video instanceof Movie) {
                continue;
            }
            videosToSort.add(video);
        }
        if (videosToSort.size() == 0) {
            lengthMessage.append("]");
            return lengthMessage.toString();
        }
        videosToSort.sort(Comparator.comparingInt(Video::getDuration)
                .thenComparing(Video::getTitle));
        if (action.getSortType().equals("desc")) {
            Collections.reverse(videosToSort);
        }
        if (videosToSort.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                lengthMessage.append(videosToSort.get(i).getTitle());
                lengthMessage.append(", ");
            }
            lengthMessage.append(videosToSort.get(n - 1).getTitle());
        } else {
            for (int i = 0; i < videosToSort.size() - 1; ++i) {
                lengthMessage.append(videosToSort.get(i).getTitle());
                lengthMessage.append(", ");
            }
            lengthMessage.append(videosToSort.get(videosToSort.size() - 1).getTitle());
        }
        lengthMessage.append("]");
        return lengthMessage.toString();
    }

    /**
     * Performs a query on videos based on number of views
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryMostViewedVideo(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder viewsMessage = new StringBuilder("Query result: [");
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        Map<String, Integer> videosToViews = new LinkedHashMap<>();
        for (User user : usersMap.values()) {
            for (Video video : user.getHistory().keySet()) {
                if (year != null && year != video.getYear()) {
                    continue;
                }
                if (genre != null && !video.getGenres().contains(genre)) {
                    continue;
                }
                if (action.getObjectType().equals("movies") && video instanceof Show) {
                    continue;
                }
                if (action.getObjectType().equals("shows") && video instanceof Movie) {
                    continue;
                }
                if (videosToViews.containsKey(video.getTitle())) {
                    videosToViews.put(video.getTitle(), videosToViews.get(video.getTitle())
                            + user.getHistory().get(video));
                } else {
                    videosToViews.put(video.getTitle(), user.getHistory().get(video));
                }
            }
        }
        if (videosToViews.size() == 0) {
            viewsMessage.append("]");
            return viewsMessage.toString();
        }
        Map<String, Integer> videosToSort = new LinkedHashMap<>();
        if (action.getSortType().equals("desc")) {
            videosToViews.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            videosToViews.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        }
        ArrayList<String> sortedVideos = new ArrayList<>(videosToSort.keySet());
        if (videosToViews.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                viewsMessage.append(sortedVideos.get(i));
                viewsMessage.append(", ");
            }
            viewsMessage.append(sortedVideos.get(n - 1));
        } else {
            for (int i = 0; i < sortedVideos.size() - 1; ++i) {
                viewsMessage.append(sortedVideos.get(i));
                viewsMessage.append(", ");
            }
            viewsMessage.append(sortedVideos.get(sortedVideos.size() - 1));
        }
        viewsMessage.append("]");
        return viewsMessage.toString();
    }

    /**
     * Performs a query on users based on most activity
     * @param action details of action to be performed
     * @return message corresponding to action
     */
    public String queryUsers(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder usersMessage = new StringBuilder("Query result: [");
        ArrayList<User> usersToSort = new ArrayList<>(this.usersMap.values());
        usersToSort.sort(Comparator.comparingInt(User::getNumRatings).thenComparing(
                                                                    User::getUsername));
        usersToSort.removeIf(user -> user.getNumRatings() == 0);
        if (action.getSortType().equals("desc")) {
            Collections.reverse(usersToSort);
        }
        if (usersToSort.size() >= n) {
            for (int i = 0; i < n - 1; ++i) {
                usersMessage.append(usersToSort.get(i).getUsername());
                usersMessage.append(", ");
            }
            usersMessage.append(usersToSort.get(n - 1).getUsername());
        } else {
            for (int i = 0; i < usersToSort.size() - 1; ++i) {
                usersMessage.append(usersToSort.get(i).getUsername());
                usersMessage.append(", ");
            }
            usersMessage.append(usersToSort.get(usersToSort.size() - 1).getUsername());
        }
        usersMessage.append("]");
        return usersMessage.toString();
    }
}

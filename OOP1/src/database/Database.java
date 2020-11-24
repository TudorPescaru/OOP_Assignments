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
import java.util.Arrays;

public final class Database {

    /**
     * Original database input
     */
    private Input input;
    /**
     * Map of actor objects in database
     */
    private Map<String, Actor> actorsMap = new LinkedHashMap<>();
    /**
     * Map of video objects in database
     */
    private Map<String, Video> videosMap = new LinkedHashMap<>();
    /**
     * Map of user objects in database
     */
    private Map<String, User> usersMap = new LinkedHashMap<>();

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
     * @return success or failure message corresponding to action
     */
    public String userViewVideo(final ActionInputData action) {
        User user = this.usersMap.get(action.getUsername());
        Video video = this.videosMap.get(action.getTitle());
        return user.viewVideo(video);
    }

    /**
     * Performs the action of favoriting a video for a certain user
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String userFavoriteVideo(final ActionInputData action) {
        User user = this.usersMap.get(action.getUsername());
        Video video = this.videosMap.get(action.getTitle());
        return user.favoriteVideo(video);
    }

    /**
     * Performs the action of rating a video for a certain user
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
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
     * @return success or failure message corresponding to action
     */
    public String queryAverageActors(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder averageMessage = new StringBuilder("Query result: [");
        ArrayList<Actor> actorsToSort = new ArrayList<>(this.actorsMap.values());
        actorsToSort.sort(Comparator.comparingDouble(
                            Actor::getFilmographyAverageRating).thenComparing(Actor::getName));
        actorsToSort.removeIf(actor -> actor.getFilmographyAverageRating().isNaN());
        if (actorsToSort.isEmpty()) {
            averageMessage.append("]");
            return averageMessage.toString();
        }
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
     * @return success or failure message corresponding to action
     */
    public String queryAwardsActors(final ActionInputData action) {
        StringBuilder awardsMessage = new StringBuilder("Query result: [");
        ArrayList<Actor> actorsWithAwards = new ArrayList<>();
        int awardsIndex = action.getFilters().size() - 1;
        ArrayList<String> filterAwards = new ArrayList<>(action.getFilters().get(awardsIndex));
        for (Actor actor : this.actorsMap.values()) {
            boolean containsAll = true;
            for (String award : filterAwards) {
                if (!actor.getAwards().containsKey(Utils.stringToAwards(award))) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                actorsWithAwards.add(actor);
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
     * @return success or failure message corresponding to action
     */
    public String queryDescriptionActors(final ActionInputData action) {
        StringBuilder descMessage = new StringBuilder("Query result: [");
        ArrayList<Actor> actorsMatchDesc = new ArrayList<>();
        int wordsIndex = action.getFilters().size() - 2;
        ArrayList<String> filterWords = new ArrayList<>(action.getFilters().get(wordsIndex));
        for (Actor actor : this.actorsMap.values()) {
            boolean containsAll = true;
            for (String word : filterWords) {
                if (!Arrays.asList(actor.getCareerDescription().toLowerCase().split("[\\s'.,!?-]"))
                                    .contains(word)) {
                    containsAll = false;
                    break;
                }
            }
            if (containsAll) {
                actorsMatchDesc.add(actor);
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
     * @return success or failure message corresponding to action
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
            if (checkMatchesFilters(action, year, genre, video)) {
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
     * @return success or failure message corresponding to action
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
        Map<String, Integer> videosToFavorites = new HashMap<>();
        for (User user : usersMap.values()) {
            for (Video video : user.getFavorites()) {
                if (checkMatchesFilters(action, year, genre, video)) {
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
                    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            videosToFavorites.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
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
     * @return success or failure message corresponding to action
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
            if (checkMatchesFilters(action, year, genre, video)) {
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
     * @return success or failure message corresponding to action
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
        Map<String, Integer> videosToViews = new HashMap<>();
        for (User user : usersMap.values()) {
            for (Video video : user.getHistory().keySet()) {
                if (checkMatchesFilters(action, year, genre, video)) {
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
                    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            videosToViews.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
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
     * Checks if a video matches all given parameters for an action
     * @param action details of action to be performed
     * @param year year filter to match (if existent)
     * @param genre genre filter to match (if existent)
     * @param video type of video filter to match
     * @return boolean on whether the video matches the filters
     */
    private boolean checkMatchesFilters(final ActionInputData action, final Integer year,
                                        final Genre genre, final Video video) {
        if (year != null && year != video.getYear()) {
            return true;
        }
        if (genre != null && !video.getGenres().contains(genre)) {
            return true;
        }
        if (action.getObjectType().equals("movies") && video instanceof Show) {
            return true;
        }
        if (action.getObjectType().equals("shows") && video instanceof Movie) {
            return true;
        }
        return false;
    }

    /**
     * Performs a query on users based on most activity
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String queryUsers(final ActionInputData action) {
        int n = action.getNumber();
        StringBuilder usersMessage = new StringBuilder("Query result: [");
        ArrayList<User> usersToSort = new ArrayList<>(this.usersMap.values());
        usersToSort.sort(Comparator.comparingInt(User::getNumRatings).thenComparing(
                                                                    User::getUsername));
        usersToSort.removeIf(user -> user.getNumRatings() == 0);
        if (usersToSort.isEmpty()) {
            usersMessage.append("]");
            return usersMessage.toString();
        }
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

    /**
     * Retrieves a recommended video based on user's unseen videos
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendStandard(final ActionInputData action) {
        StringBuilder standardMessage = new StringBuilder("StandardRecommendation result: ");
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "StandardRecommendation cannot be applied!";
        }
        Video firstUnseen = null;
        for (Video video : videosMap.values()) {
            if (!user.getHistory().containsKey(video)) {
                firstUnseen = video;
                break;
            }
        }
        if (firstUnseen != null) {
            standardMessage.append(firstUnseen.getTitle());
        } else {
            return "StandardRecommendation cannot be applied!";
        }
        return standardMessage.toString();
    }

    /**
     * Retrieves a recommended video based on user's best unseen videos rating wise
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendBestUnseen(final ActionInputData action) {
        StringBuilder bestUnseenMessage
                = new StringBuilder("BestRatedUnseenRecommendation result: ");
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }
        ArrayList<Video> videosToSort = new ArrayList<>(videosMap.values());
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating));
        videosToSort.removeIf(video -> Double.compare(video.getAverageRating(), 0.0) == 0);
        Collections.reverse(videosToSort);
        if (videosToSort.isEmpty()) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }
        Video firstBestUnseen = null;
        for (Video video : videosToSort) {
            if (!user.getHistory().containsKey(video)) {
                firstBestUnseen = video;
                break;
            }
        }
        if (firstBestUnseen != null) {
            bestUnseenMessage.append(firstBestUnseen.getTitle());
        } else {
            Video firstUnseen = null;
            for (Video video : videosMap.values()) {
                if (!user.getHistory().containsKey(video)) {
                    firstUnseen = video;
                    break;
                }
            }
            if (firstUnseen != null) {
                bestUnseenMessage.append(firstUnseen.getTitle());
            } else {
                return "BestRatedUnseenRecommendation cannot be applied!";
            }
        }
        return bestUnseenMessage.toString();
    }

    /**
     * Retrieves a recommended video based on first unseen video from most popular genre
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendPopular(final ActionInputData action) {
        StringBuilder popularMessage = new StringBuilder("PopularRecommendation result: ");
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "PopularRecommendation cannot be applied!";
        }
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "PopularRecommendation cannot be applied!";
        }
        Map<Video, Integer> videosToViews = new HashMap<>();
        for (User userToCheck : usersMap.values()) {
            for (Video video : userToCheck.getHistory().keySet()) {
                if (videosToViews.containsKey(video)) {
                    videosToViews.put(video, videosToViews.get(video)
                                                + userToCheck.getHistory().get(video));
                } else {
                    videosToViews.put(video, userToCheck.getHistory().get(video));
                }
            }
        }
        Map<Genre, Integer> genresToViews = new LinkedHashMap<>();
//        for (Genre genre : Genre.values()) {
//            for (Video video : videosMap.values()) {
//                if (videosToViews.containsKey(video)) {
//                    if (genresToViews.containsKey(genre)) {
//                        genresToViews.put(genre, genresToViews.get(genre)
//                                                    + videosToViews.get(video));
//                    } else {
//                        genresToViews.put(genre, videosToViews.get(video));
//                    }
//                }
//            }
//        }
        for (Video video : videosMap.values()) {
            for (Genre genre : video.getGenres()) {
                if (videosToViews.containsKey(video)) {
                    if (genresToViews.containsKey(genre)) {
                        genresToViews.put(genre, genresToViews.get(genre)
                                                    + videosToViews.get(video));
                    } else {
                        genresToViews.put(genre, videosToViews.get(video));
                    }
                }
            }
        }

        Map<Genre, Integer> genresToSort = new LinkedHashMap<>();
        genresToViews.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(genre -> genresToSort.put(genre.getKey(), genre.getValue()));
        Video mostPopular = null;
        for (Genre genre : genresToSort.keySet()) {
            for (Video video : videosMap.values()) {
                if (video.getGenres().contains(genre) && !user.getHistory().containsKey(video)) {
                    mostPopular = video;
                    break;
                }
            }
            if (mostPopular != null) {
                break;
            }
        }
        if (mostPopular != null) {
            popularMessage.append(mostPopular.getTitle());
        } else {
            return "PopularRecommendation cannot be applied!";
        }
        return popularMessage.toString();
    }

    /**
     * Retrieves a recommended video based on most favorited unseen videos
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendFavorite(final ActionInputData action) {
        StringBuilder favoriteMessage = new StringBuilder("FavoriteRecommendation result: ");
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "FavoriteRecommendation cannot be applied!";
        }
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "FavoriteRecommendation cannot be applied!";
        }
        Map<Video, Integer> videosToFavorites = new LinkedHashMap<>();
        for (Video video : videosMap.values()) {
            if (!user.getHistory().containsKey(video)) {
                videosToFavorites.put(video, 0);
            }
        }
        if (videosToFavorites.isEmpty()) {
            return "FavoriteRecommendation cannot be applied!";
        }
        for (User userToCheck : usersMap.values()) {
            for (Video video : userToCheck.getFavorites()) {
                if (videosToFavorites.containsKey(video)) {
                    videosToFavorites.put(video, videosToFavorites.get(video) + 1);
                }
            }
        }
        Video mostFavorited = videosToFavorites.entrySet()
                                                .stream()
                                                .max(Comparator.comparingInt(Map.Entry::getValue))
                                                .get().getKey();
        favoriteMessage.append(mostFavorited.getTitle());
        return favoriteMessage.toString();
    }

    /**
     * Retrieves a set of recommended videos from a certain genre, sorted by rating and that
     * have not been viewed
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendSearch(final ActionInputData action) {
        StringBuilder searchMessage = new StringBuilder("SearchRecommendation result: ");
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "SearchRecommendation cannot be applied!";
        }
        Genre genre = Utils.stringToGenre(action.getGenre());
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "SearchRecommendation cannot be applied!";
        }
        ArrayList<Video> videosToSort = new ArrayList<>();
        for (Video video : videosMap.values()) {
            if (!video.getGenres().contains(genre) || user.getHistory().containsKey(video)) {
                continue;
            }
            videosToSort.add(video);
        }
        if (videosToSort.isEmpty()) {
            return "SearchRecommendation cannot be applied!";
        }
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating)
                                    .thenComparing(Video::getTitle));
        searchMessage.append('[');
        for (int i = 0; i < videosToSort.size() - 1; ++i) {
            searchMessage.append(videosToSort.get(i).getTitle());
            searchMessage.append(", ");
        }
        searchMessage.append(videosToSort.get(videosToSort.size() - 1).getTitle());
        searchMessage.append(']');
        return searchMessage.toString();
    }
}

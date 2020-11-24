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
import java.util.Map.Entry;
import java.util.Optional;
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
    private final Map<String, Actor> actorsMap = new LinkedHashMap<>();
    /**
     * Map of video objects in database
     */
    private final Map<String, Video> videosMap = new LinkedHashMap<>();
    /**
     * Map of user objects in database
     */
    private final Map<String, User> usersMap = new LinkedHashMap<>();

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
        // Make sure maps are clear before processing new input
        actorsMap.clear();
        videosMap.clear();
        usersMap.clear();
        // Convert string-based objects from input into usable movie objects
        for (MovieInputData movieData : this.input.getMovies()) {
            // Convert movie's genres from string to enum values
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : movieData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            // Create object for current movie
            Movie movie = new Movie(movieData.getTitle(), movieData.getYear(),
                    movieData.getCast(), genres, movieData.getDuration());
            // Add object to parent class map using title as key for faster lookup and
            // upcasted object for generality
            this.videosMap.put(movie.getTitle(), movie);
        }
        // Convert string-based objects from input into usable show objects
        for (SerialInputData showData : this.input.getSerials()) {
            // Convert show's genres from string to enum values
            ArrayList<Genre> genres = new ArrayList<>();
            for (String genre : showData.getGenres()) {
                genres.add(Utils.stringToGenre(genre));
            }
            // Create object for current show
            Show show = new Show(showData.getTitle(), showData.getYear(),
                    showData.getCast(), genres, showData.getNumberSeason(), showData.getSeasons());
            // Add object to parent class map using title as key for faster lookup and
            // upcasted object for generality
            this.videosMap.put(show.getTitle(), show);
        }
        // Convert string-based objects from input into usable actor objects
        for (ActorInputData actorData : this.input.getActors()) {
            // Create list of video objects based on actor's filmography
            ArrayList<Video> starredIn = new ArrayList<>();
            for (Video video : videosMap.values()) {
                if (video.getCast().contains(actorData.getName())) {
                    starredIn.add(video);
                }
            }
            // Create object for current actor
            Actor actor = new Actor(actorData.getName(), actorData.getCareerDescription(),
                    starredIn, actorData.getAwards());
            // Add object to map using name as key for faster lookup
            this.actorsMap.put(actor.getName(), actor);
        }
        // Convert string-based objects from input into usable username objects
        for (UserInputData userData : input.getUsers()) {
            // Create map of viewed videos as objects and number of views
            // Create list of favorite videos as objects
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
            // Create object for current user
            User user = new User(userData.getUsername(), userData.getSubscriptionType(),
                    seen, favorites);
            // Add object to map using username as key for faster lookup
            this.usersMap.put(user.getUsername(), user);
        }
    }

    /**
     * Performs the action of viewing a video for a certain user
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String userViewVideo(final ActionInputData action) {
        // Get user object from user map using given username
        User user = this.usersMap.get(action.getUsername());
        // Get video object from video map using given video title
        Video video = this.videosMap.get(action.getTitle());
        return user.viewVideo(video);
    }

    /**
     * Performs the action of adding a video to favorites list for a certain user
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String userFavoriteVideo(final ActionInputData action) {
        // Get user object from user map using given username
        User user = this.usersMap.get(action.getUsername());
        // Get video object from video map using given video title
        Video video = this.videosMap.get(action.getTitle());
        return user.favoriteVideo(video);
    }

    /**
     * Performs the action of rating a video for a certain user
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String userRateVideo(final ActionInputData action) {
        // Get user object from user map using given username
        User user = this.usersMap.get(action.getUsername());
        // Get video object from video map using given video title
        Video video = this.videosMap.get(action.getTitle());
        Double rating = action.getGrade();
        // Check if video to be rated is a show or a movie
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
        // Get number of actors to be returned
        int n = action.getNumber();
        StringBuilder averageMessage = new StringBuilder("Query result: [");
        // Get initial list of actors in database
        ArrayList<Actor> actorsToSort = new ArrayList<>(this.actorsMap.values());
        // Sort list of actors by rating and name
        actorsToSort.sort(Comparator.comparingDouble(
                            Actor::getFilmographyAverageRating).thenComparing(Actor::getName));
        // Remove all actors with invalid filmography ratings
        actorsToSort.removeIf(actor -> actor.getFilmographyAverageRating().isNaN());
        if (actorsToSort.isEmpty()) {
            averageMessage.append("]");
            return averageMessage.toString();
        }
        // Reverse list if needed
        if (action.getSortType().equals("desc")) {
            Collections.reverse(actorsToSort);
        }
        // Check if n actors can be returned or return all available
        if (actorsToSort.size() >= n) {
            // Append actor names to the return message
            for (int i = 0; i < n - 1; ++i) {
                averageMessage.append(actorsToSort.get(i).getName());
                averageMessage.append(", ");
            }
            averageMessage.append(actorsToSort.get(n - 1).getName());
        } else {
            // Append actor names to the return message
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
        // Get list of awards given as filter
        int awardsIndex = action.getFilters().size() - 1;
        ArrayList<String> filterAwards = new ArrayList<>(action.getFilters().get(awardsIndex));
        // Iterate over all actors in database
        for (Actor actor : this.actorsMap.values()) {
            // Check if actor has earned all filter awards
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
        // If we have found eligible actors sort them
        if (actorsWithAwards.size() != 0) {
            // Sort list of actors based on total number of awards and then name
            actorsWithAwards.sort(Comparator.comparingInt(Actor::getTotalAwards)
                                            .thenComparing(Actor::getName));
            // Reverse list if needed
            if (action.getSortType().equals("desc")) {
                Collections.reverse(actorsWithAwards);
            }
            // Append all actor names in order to message
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
        // Get list of filter words
        int wordsIndex = action.getFilters().size() - 2;
        ArrayList<String> filterWords = new ArrayList<>(action.getFilters().get(wordsIndex));
        // Iterate over actors in database
        for (Actor actor : this.actorsMap.values()) {
            // Check if actor has all filter words in description
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
        // If we have found eligible actors sort them
        if (actorsMatchDesc.size() != 0) {
            // Sort list of actors by name
            actorsMatchDesc.sort(Comparator.comparing(Actor::getName));
            // Reverse list if needed
            if (action.getSortType().equals("desc")) {
                Collections.reverse(actorsMatchDesc);
            }
            // Append actor names to message
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
        // Get number of videos to be returned
        int n = action.getNumber();
        StringBuilder ratingMessage = new StringBuilder("Query result: [");
        // Get year filter if given
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        // Get genre filter if given
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        ArrayList<Video> videosToSort = new ArrayList<>();
        // Iterate over videos in database
        for (Video video : videosMap.values()) {
            // Check if message meets given criteria
            if (checkMatchesFilters(action, year, genre, video)) {
                continue;
            }
            // Check if video has a valid rating
            if (Double.compare(video.getAverageRating(), 0.0) == 0) {
                continue;
            }
            videosToSort.add(video);
        }
        // Check if there any eligible videos were found
        if (videosToSort.isEmpty()) {
            ratingMessage.append("]");
            return ratingMessage.toString();
        }
        // Sort list of videos by rating then title
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating)
                                    .thenComparing(Video::getTitle));
        // Return required number of videos if possible or return all videos found
        if (videosToSort.size() >= n) {
            // Append to message all video titles
            for (int i = 0; i < n - 1; ++i) {
                ratingMessage.append(videosToSort.get(i).getTitle());
                ratingMessage.append(", ");
            }
            ratingMessage.append(videosToSort.get(n - 1).getTitle());
        } else {
            // Append to message all video titles
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
        // Get number of videos to be returned
        int n = action.getNumber();
        StringBuilder favoriteMessage = new StringBuilder("Query result: [");
        // Get year filter if given
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        // Get genre filter if given
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        // Use a map to efficiently track number of times video has been added to favorites
        Map<String, Integer> videosToFavorites = new HashMap<>();
        // Iterate over favorites list for all users in database
        for (User user : usersMap.values()) {
            for (Video video : user.getFavorites()) {
                // Check if video matches given criteria
                if (checkMatchesFilters(action, year, genre, video)) {
                    continue;
                }
                // If video has already been added to map, update number of favorites appearances
                if (videosToFavorites.containsKey(video.getTitle())) {
                    videosToFavorites.put(video.getTitle(),
                            videosToFavorites.get(video.getTitle()) + 1);
                } else {
                    videosToFavorites.put(video.getTitle(), 1);
                }
            }
        }
        // Check if any eligible videos were found
        if (videosToFavorites.size() == 0) {
            favoriteMessage.append("]");
            return favoriteMessage.toString();
        }
        // Create a map to store contents of initial map in sorted order
        Map<String, Integer> videosToSort = new LinkedHashMap<>();
        // Check if we have to sort in reverse
        if (action.getSortType().equals("desc")) {
            // Use streams to sort; sort alphabetically first and then by number of favorites
            videosToFavorites.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            // Use streams to sort; sort alphabetically first and then by number of favorites
            videosToFavorites.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        }
        // Get sorted videos
        ArrayList<String> sortedVideos = new ArrayList<>(videosToSort.keySet());
        // Check if we can return required number of videos or return all available
        if (videosToFavorites.size() >= n) {
            // Append video title to message
            for (int i = 0; i < n - 1; ++i) {
                favoriteMessage.append(sortedVideos.get(i));
                favoriteMessage.append(", ");
            }
            favoriteMessage.append(sortedVideos.get(n - 1));
        } else {
            // Append video title to message
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
        // Get number of videos to be returned
        int n = action.getNumber();
        StringBuilder lengthMessage = new StringBuilder("Query result: [");
        // Get year filter if given
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        // Get genre filter if given
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        ArrayList<Video> videosToSort = new ArrayList<>();
        // Iterate over all videos in database
        for (Video video : videosMap.values()) {
            // Check if video matches given criteria
            if (checkMatchesFilters(action, year, genre, video)) {
                continue;
            }
            videosToSort.add(video);
        }
        // Check if any eligible videos were found
        if (videosToSort.size() == 0) {
            lengthMessage.append("]");
            return lengthMessage.toString();
        }
        // Sort list of videos by video length then title
        videosToSort.sort(Comparator.comparingInt(Video::getDuration)
                .thenComparing(Video::getTitle));
        // Check if we have to reverse list
        if (action.getSortType().equals("desc")) {
            Collections.reverse(videosToSort);
        }
        // Check if we can return required number of videos or return all available
        if (videosToSort.size() >= n) {
            // Append video title to message
            for (int i = 0; i < n - 1; ++i) {
                lengthMessage.append(videosToSort.get(i).getTitle());
                lengthMessage.append(", ");
            }
            lengthMessage.append(videosToSort.get(n - 1).getTitle());
        } else {
            // Append video title to message
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
        // Get number of videos to return
        int n = action.getNumber();
        StringBuilder viewsMessage = new StringBuilder("Query result: [");
        // Get year filter if given
        Integer year = null;
        if (action.getFilters().get(0).get(0) != null) {
            year = Integer.parseInt(action.getFilters().get(0).get(0));
        }
        // Get genre filter if given
        Genre genre = null;
        if (action.getFilters().get(1).get(0) != null) {
            genre = Utils.stringToGenre(action.getFilters().get(1).get(0));
        }
        // Use map to effectively track number of views for videos
        Map<String, Integer> videosToViews = new HashMap<>();
        // Iterate over history for users in database
        for (User user : usersMap.values()) {
            for (Video video : user.getHistory().keySet()) {
                // Check if video matches given criteria
                if (checkMatchesFilters(action, year, genre, video)) {
                    continue;
                }
                // If video has already been added to map, update number of views
                if (videosToViews.containsKey(video.getTitle())) {
                    videosToViews.put(video.getTitle(), videosToViews.get(video.getTitle())
                            + user.getHistory().get(video));
                } else {
                    videosToViews.put(video.getTitle(), user.getHistory().get(video));
                }
            }
        }
        // Check if any eligible videos were found
        if (videosToViews.size() == 0) {
            viewsMessage.append("]");
            return viewsMessage.toString();
        }
        //Create a map to store contents of initial map in sorted order
        Map<String, Integer> videosToSort = new LinkedHashMap<>();
        // Check if we have to sort in reveres
        if (action.getSortType().equals("desc")) {
            // Use streams to sort; sort alphabetically first and then by number of views
            videosToViews.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        } else {
            // Use streams to sort; sort alphabetically first and then by number of views
            videosToViews.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(video -> videosToSort.put(video.getKey(), video.getValue()));
        }
        // Get list of sorted videos
        ArrayList<String> sortedVideos = new ArrayList<>(videosToSort.keySet());
        // Check if we can return required number of videos or return all available videos
        if (videosToViews.size() >= n) {
            // Append video title to message
            for (int i = 0; i < n - 1; ++i) {
                viewsMessage.append(sortedVideos.get(i));
                viewsMessage.append(", ");
            }
            viewsMessage.append(sortedVideos.get(n - 1));
        } else {
            // Append video title to message
            for (int i = 0; i < sortedVideos.size() - 1; ++i) {
                viewsMessage.append(sortedVideos.get(i));
                viewsMessage.append(", ");
            }
            viewsMessage.append(sortedVideos.get(sortedVideos.size() - 1));
        }
        viewsMessage.append("]");
        return viewsMessage.toString();
    }

    private boolean checkMatchesFilters(final ActionInputData action, final Integer year,
                                        final Genre genre, final Video video) {
        // Check if video matches year filter if available
        if (year != null && year != video.getYear()) {
            return true;
        }
        // Check if video matches genre filter if available
        if (genre != null && !video.getGenres().contains(genre)) {
            return true;
        }
        // Check if video is a movie if action is performed on movies
        if (action.getObjectType().equals("movies") && video instanceof Show) {
            return true;
        }
        // Check if video is a show if action is performed on shows
        return action.getObjectType().equals("shows") && video instanceof Movie;
    }

    /**
     * Performs a query on users based on most activity
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String queryUsers(final ActionInputData action) {
        // Get number of users to return
        int n = action.getNumber();
        StringBuilder usersMessage = new StringBuilder("Query result: [");
        // Get list of users from database
        ArrayList<User> usersToSort = new ArrayList<>(this.usersMap.values());
        // Sort list of users based on number of ratings given and then username
        usersToSort.sort(Comparator.comparingInt(User::getNumRatings).thenComparing(
                                                                    User::getUsername));
        // Remove inactive users
        usersToSort.removeIf(user -> user.getNumRatings() == 0);
        // Check if any eligible users are left in list
        if (usersToSort.isEmpty()) {
            usersMessage.append("]");
            return usersMessage.toString();
        }
        // Check if we need to reverse the list
        if (action.getSortType().equals("desc")) {
            Collections.reverse(usersToSort);
        }
        // Check if we can return required number of users or return all available users
        if (usersToSort.size() >= n) {
            // Append username to message
            for (int i = 0; i < n - 1; ++i) {
                usersMessage.append(usersToSort.get(i).getUsername());
                usersMessage.append(", ");
            }
            usersMessage.append(usersToSort.get(n - 1).getUsername());
        } else {
            // Append username to message
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
        // Get user object for given username if it exists
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "StandardRecommendation cannot be applied!";
        }
        // Iterate over videos in database
        Video firstUnseen = null;
        for (Video video : videosMap.values()) {
            // Find first video the user has not seen
            if (!user.getHistory().containsKey(video)) {
                firstUnseen = video;
                break;
            }
        }
        // If an unseen video exists return it's title
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
        // Get user object for given username if it exists
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }
        // Get list of videos in database
        ArrayList<Video> videosToSort = new ArrayList<>(videosMap.values());
        // Sort videos by rating
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating));
        // Remove videos with no rating
        videosToSort.removeIf(video -> Double.compare(video.getAverageRating(), 0.0) == 0);
        // Get list of videos in decreasing order by rating
        Collections.reverse(videosToSort);
        // Check if any eligible videos were found
        if (videosToSort.isEmpty()) {
            return "BestRatedUnseenRecommendation cannot be applied!";
        }
        Video firstBestUnseen = null;
        // Iterate over videos in list
        for (Video video : videosToSort) {
            // Find first video in list that has not been viewed
            if (!user.getHistory().containsKey(video)) {
                firstBestUnseen = video;
                break;
            }
        }
        // Return first unseen video by rating if available
        if (firstBestUnseen != null) {
            bestUnseenMessage.append(firstBestUnseen.getTitle());
        } else {
            // Return first unseen video in database regardless of rating
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
        // Get user object for given username if it exists
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "PopularRecommendation cannot be applied!";
        }
        // Check if given user has premium subscription
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "PopularRecommendation cannot be applied!";
        }
        // Use a map to efficiently keep track of views for videos
        Map<Video, Integer> videosToViews = new HashMap<>();
        // Iterate over videos in history of users in database
        for (User userToCheck : usersMap.values()) {
            for (Video video : userToCheck.getHistory().keySet()) {
                // If video has already been added to map, update number of views
                if (videosToViews.containsKey(video)) {
                    videosToViews.put(video, videosToViews.get(video)
                                                + userToCheck.getHistory().get(video));
                } else {
                    videosToViews.put(video, userToCheck.getHistory().get(video));
                }
            }
        }
        // Use a map to efficiently keep track of views for genres
        Map<Genre, Integer> genresToViews = new LinkedHashMap<>();
        // Iterate over videos in database and increase number of views for genre using the map
        for (Video video : videosMap.values()) {
            for (Genre genre : video.getGenres()) {
                // Check if video was viewed
                if (videosToViews.containsKey(video)) {
                    // If genre has already been added to map, update number of views
                    if (genresToViews.containsKey(genre)) {
                        genresToViews.put(genre, genresToViews.get(genre)
                                                    + videosToViews.get(video));
                    } else {
                        genresToViews.put(genre, videosToViews.get(video));
                    }
                }
            }
        }
        //Create a map to store contents of initial map in sorted order
        Map<Genre, Integer> genresToSort = new LinkedHashMap<>();
        genresToViews.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(genre -> genresToSort.put(genre.getKey(), genre.getValue()));
        // Iterate through sorted genres and find first unseen video in database
        Video mostPopular = null;
        for (Genre genre : genresToSort.keySet()) {
            for (Video video : videosMap.values()) {
                // Check if current video in database belongs to current genre and is unseen
                if (video.getGenres().contains(genre) && !user.getHistory().containsKey(video)) {
                    mostPopular = video;
                    break;
                }
            }
            if (mostPopular != null) {
                break;
            }
        }
        // If a eligible video was found return it
        if (mostPopular != null) {
            popularMessage.append(mostPopular.getTitle());
        } else {
            return "PopularRecommendation cannot be applied!";
        }
        return popularMessage.toString();
    }

    /**
     * Retrieves a recommended video based on most times found in favorites list of unseen videos
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendFavorite(final ActionInputData action) {
        StringBuilder favoriteMessage = new StringBuilder("FavoriteRecommendation result: ");
        // Get user object for given username if it exists
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "FavoriteRecommendation cannot be applied!";
        }
        // Check if given user has premium subscription
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "FavoriteRecommendation cannot be applied!";
        }
        // Add all unseen videos in database to a map to later track number of favorites
        Map<Video, Integer> videosToFavorites = new LinkedHashMap<>();
        for (Video video : videosMap.values()) {
            if (!user.getHistory().containsKey(video)) {
                videosToFavorites.put(video, 0);
            }
        }
        // Check if any eligible videos were found
        if (videosToFavorites.isEmpty()) {
            return "FavoriteRecommendation cannot be applied!";
        }
        // Iterate through users in database and increase number of favorites
        // for their favorite videos
        for (User userToCheck : usersMap.values()) {
            for (Video video : userToCheck.getFavorites()) {
                // Check if video is in map
                if (videosToFavorites.containsKey(video)) {
                    videosToFavorites.put(video, videosToFavorites.get(video) + 1);
                }
            }
        }
        // Get the video with the most ammount of favorites
         Optional<Entry<Video, Integer>> maxEntry = videosToFavorites.entrySet()
                                                .stream()
                                                .max(Comparator.comparingInt(Map.Entry::getValue));
        // Check if a maximum is available
        Video mostFavored = null;
        if (maxEntry.isPresent()) {
            mostFavored = maxEntry.get().getKey();
        }
        // Return maximum if found
        if (mostFavored != null) {
            favoriteMessage.append(mostFavored.getTitle());
            return favoriteMessage.toString();
        } else {
            return "FavoriteRecommendation cannot be applied!";
        }
    }

    /**
     * Retrieves a set of recommended videos from a certain genre, sorted by rating and that
     * have not been viewed
     * @param action details of action to be performed
     * @return success or failure message corresponding to action
     */
    public String recommendSearch(final ActionInputData action) {
        StringBuilder searchMessage = new StringBuilder("SearchRecommendation result: ");
        // Get user object for given username if it exists
        User user;
        if (usersMap.containsKey(action.getUsername())) {
            user = usersMap.get(action.getUsername());
        } else {
            return "SearchRecommendation cannot be applied!";
        }
        // Get the genre given as criteria
        Genre genre = Utils.stringToGenre(action.getGenre());
        // Check if given user has premium subscription
        if (!user.getSubscriptionType().equals("PREMIUM")) {
            return "SearchRecommendation cannot be applied!";
        }
        // Add to list all videos from database that match genre and are unseen
        ArrayList<Video> videosToSort = new ArrayList<>();
        for (Video video : videosMap.values()) {
            if (!video.getGenres().contains(genre) || user.getHistory().containsKey(video)) {
                continue;
            }
            videosToSort.add(video);
        }
        // Check if any eligible videos were found
        if (videosToSort.isEmpty()) {
            return "SearchRecommendation cannot be applied!";
        }
        // Sort videos by rating and then name
        videosToSort.sort(Comparator.comparingDouble(Video::getAverageRating)
                                    .thenComparing(Video::getTitle));
        // Append all video titles to message
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

package entities;

import entertainment.Season;

import java.util.ArrayList;
import java.util.Map;

/**
 * Information about an user and user-specific actions
 */
public final class User {
    /**
     * User's username
     */
    private final String username;
    /**
     * User's subscription type
     */
    private final String subscriptionType;
    /**
     * User's list of viewed movies and tv shows alongside number of views
     */
    private final Map<Video, Integer> history;
    /**
     * User's list of favorite movies and tv shows
     */
    private final ArrayList<Video> favorites;
    /**
     * User's list of rated movies
     */
    private final ArrayList<Movie> ratedMovies;
    /**
     * User's list of rated shows
     */
    private final ArrayList<Season> ratedShows;

    public User(final String username, final String subscriptionType,
                final Map<Video, Integer> history,
                final ArrayList<Video> favorites) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favorites = favorites;
        this.ratedMovies = new ArrayList<>();
        this.ratedShows = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<Video, Integer> getHistory() {
        return history;
    }

    public ArrayList<Video> getFavorites() {
        return favorites;
    }

    public ArrayList<Movie> getRatedMovies() {
        return ratedMovies;
    }

    public ArrayList<Season> getRatedShows() {
        return ratedShows;
    }

    public int getNumRatings() {
        return this.ratedMovies.size() + this.ratedShows.size();
    }

    /**
     * Add a viewed video (movie/tv show) to the user's favorites list
     * @param toFavorite video to add
     * @return error or success message
     */
    public String favoriteVideo(final Video toFavorite) {
        // Check if given video exists
        if (toFavorite == null) {
            return "error -> Video is non existent";
        }
        // Check if video has been viewed
        if (this.history.containsKey(toFavorite)) {
            // Check if video is not already in favorites
            if (!this.favorites.contains(toFavorite)) {
                this.favorites.add(toFavorite);
                return "success -> " + toFavorite.getTitle() + " was added as favourite";
            } else {
                return "error -> " + toFavorite.getTitle()
                        + " is already in favourite list";
            }
        } else {
            return "error -> " + toFavorite.getTitle() + " is not seen";
        }
    }

    /**
     * Add a video (movie/tv show) to the user's history
     * @param toView video to add
     * @return error or success message
     */
    public String viewVideo(final Video toView) {
        // Check if given video exists
        if (toView == null) {
            return "error -> Video is non existent";
        }
        // Check if video has not been viewed
        if (this.history.containsKey(toView)) {
            this.history.put(toView, this.history.get(toView) + 1);
            return "success -> " + toView.getTitle() + " was viewed with total views of "
                    + this.history.get(toView);
        } else {
            this.history.put(toView, 1);
            return "success -> " + toView.getTitle() + " was viewed with total views of 1";
        }
    }

    /**
     * Add a rating for a viewed movie
     * @param toRate movie to rate
     * @param rating rating user wants to add
     * @return error or success message
     */
    public String rateVideo(final Movie toRate, final Double rating) {
        // Check if given movie exists
        if (toRate == null) {
            return "error -> Movie is null";
        }
        // Check if movie has been viewed
        if (this.history.containsKey(toRate)) {
            // Check if video is not already rated
            if (!this.ratedMovies.contains(toRate)) {
                this.ratedMovies.add(toRate);
                toRate.addRating(rating);
                return "success -> " + toRate.getTitle() + " was rated with " + rating + " by "
                        + this.username;
            } else {
                return "error -> " + toRate.getTitle() + " has been already rated";
            }
        } else {
            return "error -> " + toRate.getTitle() + " is not seen";
        }
    }

    /**
     * Overloaded method for rating a show
     * @param toRate show to rate
     * @param seasonNumber number of season in rated show
     * @param rating rating user wants to add
     * @return error or success message
     */
    public String rateVideo(final Show toRate, final int seasonNumber, final Double rating) {
        // Check if given show exists
        if (toRate == null) {
            return "error -> Show is null";
        }
        // Check if show has been viewed
        if (this.history.containsKey(toRate)) {
            // Get season from show based on season number
            Season seasonToRate = toRate.getSeasons().get(seasonNumber - 1);
            // Check if season is not already rated
            if (!this.ratedShows.contains(seasonToRate)) {
                this.ratedShows.add(seasonToRate);
                toRate.addSeasonRating(seasonNumber - 1, rating);
                return "success -> " + toRate.getTitle() + " was rated with " + rating + " by "
                        + this.username;
            } else {
                return "error -> " + toRate.getTitle() + " has been already rated";
            }
        } else {
            return "error -> " + toRate.getTitle() + " is not seen";
        }
    }
}

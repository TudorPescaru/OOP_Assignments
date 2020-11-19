package entities;

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
    private Map<Video, Integer> history;
    /**
     * User's list of favorite movies and tv shows
     */
    private ArrayList<Video> favorites;
    /**
     * User's list of rated movies and tv shows
     */
    private ArrayList<Video> rated;

    public User(final String username, final String subscriptionType,
                final Map<Video, Integer> history,
                final ArrayList<Video> favorites) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favorites = favorites;
        this.rated = new ArrayList<>();
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

    /**
     * Add a viewed video (movie/tv show) to the user's favorites list
     * @param toFavorite video to add
     */
    public void favoriteVideo(final Video toFavorite) {

    }

    /**
     * Add a video (movie/tv show) to the user's history
     * @param toView video to add
     */
    public void viewVideo(final Video toView) {

    }

    /**
     * Add a rating for a viewed video (movie/tv show season)
     * @param toRate video to rate
     */
    public void rateVideo(final Video toRate) {

    }
}

package entities;

import entertainment.Genre;
import entertainment.Season;

import java.util.ArrayList;
import java.util.List;

/**
 * Information specific to a tv show
 */
public final class Show extends Video {
    /**
     * Show's number of seasons
     */
    private final int numberOfSeasons;
    /**
     * Show's list of seasons
     */
    private final ArrayList<Season> seasons;
    /**
     * Show's list of average ratings for each season
     */
    private final ArrayList<Double> seasonAverageRatings;
    /**
     * Show's overall average rating
     */
    private Double averageRating;
    /**
     * Show's total duration
     */
    private final int duration;

    public Show(final String title, final int year,
                final ArrayList<String> cast, final ArrayList<Genre> genres,
                final int numberOfSeasons, final ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        this.seasonAverageRatings = new ArrayList<>(numberOfSeasons);
        for (int i = 0; i < numberOfSeasons; ++i) {
            this.seasonAverageRatings.add(0.0);
        }
        this.averageRating = 0.0;
        int totalDuration = 0;
        for (Season season : this.seasons) {
            totalDuration += season.getDuration();
        }
        this.duration = totalDuration;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }

    public ArrayList<Double> getSeasonAverageRatings() {
        return seasonAverageRatings;
    }

    @Override
    public Double getAverageRating() {
        return averageRating;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    protected void addSeasonRating(final int seasonNumber, final Double rating) {
        // Get season from show based on season number
        Season season = this.seasons.get(seasonNumber);
        // Check if season exists
        if (season == null) {
            return;
        }
        // Get list of season ratings and update it
        List<Double> seasonRatings = season.getRatings();
        seasonRatings.add(rating);
        season.setRatings(seasonRatings);
        // Update list of season average ratings
        this.updateSeasonAverageRatings(seasonNumber);
    }

    private void updateSeasonAverageRatings(final int seasonNumber) {
        // Get season for show based on season number
        Season season = this.seasons.get(seasonNumber);
        // Get list of ratings for season
        List<Double> seasonRatings = season.getRatings();
        // Check if season has been rated
        if (seasonRatings.size() == 0) {
            return;
        }
        // Calculate average rating for season
        Double ratingSum = 0.0;
        for (Double rating : seasonRatings) {
            ratingSum += rating;
        }
        // Update average rating for given season in list of average seasons
        Double seasonAverageRating = ratingSum / seasonRatings.size();
        this.seasonAverageRatings.set(seasonNumber, seasonAverageRating);
        this.updateAverageRating();
    }

    private void updateAverageRating() {
        // Check if seasons have average ratings
        if (this.seasonAverageRatings.size() == 0) {
            return;
        }
        // Calculate the average rating for the entire show
        Double ratingSum = 0.0;
        for (Double rating : this.seasonAverageRatings) {
            ratingSum += rating;
        }
        this.averageRating = ratingSum / this.numberOfSeasons;
    }
}

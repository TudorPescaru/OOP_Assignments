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
    private ArrayList<Double> seasonAverageRatings;
    /**
     * Show's overall average rating
     */
    private Double averageRating;

    public Show(final String title, final int year,
                final ArrayList<Actor> cast, final ArrayList<Genre> genres,
                final int numberOfSeasons, final ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
        this.seasonAverageRatings = new ArrayList<>(numberOfSeasons);
        this.averageRating = 0.0;
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

    public Double getAverageRating() {
        return averageRating;
    }

    protected void addSeasonRating(final int seasonNumber, final Double rating) {
        Season season = this.seasons.get(seasonNumber);
        if (season == null) {
            return;
        }
        List<Double> seasonRatings = season.getRatings();
        seasonRatings.add(rating);
        season.setRatings(seasonRatings);
        this.updateSeasonAverageRatings(seasonNumber);
    }

    private void updateSeasonAverageRatings(final int seasonNumber) {
        Season season = this.seasons.get(seasonNumber);
        List<Double> seasonRatings = season.getRatings();
        if (seasonRatings.size() == 0) {
            return;
        }
        Double ratingSum = 0.0;
        for (Double rating : seasonRatings) {
            ratingSum += rating;
        }
        Double seasonAverageRating = ratingSum / seasonRatings.size();
        this.seasonAverageRatings.set(seasonNumber, seasonAverageRating);
        this.updateAverageRating();
    }

    private void updateAverageRating() {
        if (this.seasonAverageRatings.size() == 0) {
            return;
        }
        Double ratingSum = 0.0;
        for (Double rating : this.seasonAverageRatings) {
            ratingSum += rating;
        }
        this.averageRating = ratingSum / this.numberOfSeasons;
    }
}
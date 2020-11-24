package entities;

import entertainment.Genre;

import java.util.ArrayList;

/**
 * Information specific to a movie
 */
public final class Movie extends Video {
    /**
     * Movie's duration in minutes
     */
    private final int duration;
    /**
     * Movie's list of ratings
     */
    private final ArrayList<Double> ratings;
    /**
     * Movie's average rating
     */
    private Double averageRating;

    public Movie(final String title, final int year,
                 final ArrayList<String> cast, final ArrayList<Genre> genres,
                 final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
        this.ratings = new ArrayList<>();
        this.averageRating = 0.0;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    @Override
    public Double getAverageRating() {
        return averageRating;
    }

    protected void addRating(final Double rating) {
        // Add given rating to list of ratings and update average rating
        this.ratings.add(rating);
        this.updateAverageRating();
    }

    private void updateAverageRating() {
        // Check if any rating has been given
        if (this.ratings.size() == 0) {
            return;
        }
        // Iterate over ratings and calculate average rating
        Double ratingSum = 0.0;
        for (Double rating : this.ratings) {
            ratingSum += rating;
        }
        this.averageRating = ratingSum / this.ratings.size();
    }
}

package entities;

import actor.ActorsAwards;

import java.util.ArrayList;
import java.util.Map;

/**
 * Information about an actor and user-specific actions
 */
public final class Actor {
    /**
     * Actor's name
     */
    private final String name;
    /**
     * Actor's career description
     */
    private final String careerDescription;
    /**
     * List of videos the actor appears in
     */
    private final ArrayList<Video> filmography;
    /**
     * List of awards the actor has earned
     */
    private final Map<ActorsAwards, Integer> awards;
    /**
     * Total number of awards an actor has earned
     */
    private final int totalAwards;

    public Actor(final String name, final String careerDescription,
                 final ArrayList<Video> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
        int sumAwards = 0;
        for (int awardsOfType : this.awards.values()) {
            sumAwards += awardsOfType;
        }
        this.totalAwards = sumAwards;
    }

    public String getName() {
        return name;
    }

    public String getCareerDescription() {
        return careerDescription;
    }

    public ArrayList<Video> getFilmography() {
        return filmography;
    }

    public Map<ActorsAwards, Integer> getAwards() {
        return awards;
    }

    public int getTotalAwards() {
        return totalAwards;
    }

    /**
     * Calculate the average rating of all videos an actor has starred in
     * @return average rating of filmography
     */
    public Double getFilmographyAverageRating() {
        double ratingSum = 0.0;
        int ratingsNotZero = 0;
        for (Video video : this.filmography) {
            if (video instanceof Movie) {
                Movie movie = (Movie) video;
                if (movie.getAverageRating() != 0.0) {
                    ratingSum += movie.getAverageRating();
                    ratingsNotZero++;
                }
            } else if (video instanceof Show) {
                Show show = (Show) video;
                if (show.getAverageRating() != 0.0) {
                    ratingSum += show.getAverageRating();
                    ratingsNotZero++;
                }
            }
        }
        return ratingSum / ratingsNotZero;
    }
}

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

    public Movie(final String title, final int year,
                 final ArrayList<Actor> cast, final ArrayList<Genre> genres,
                 final int duration) {
        super(title, year, cast, genres);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}

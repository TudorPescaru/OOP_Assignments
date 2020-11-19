package entities;

import entertainment.Genre;

import java.util.ArrayList;

/**
 * Generic information applicable to all types of video
 */
public abstract class Video {
    /**
     * Video's title
     */
    private final String title;
    /**
     * Video's release year
     */
    private final int year;
    /**
     * Video's list of actors starring
     */
    private final ArrayList<Actor> cast;
    /**
     * Video's list of defining genres
     */
    private final ArrayList<Genre> genres;

    public Video(final String title, final int year,
                 final ArrayList<Actor> cast, final ArrayList<Genre> genres) {
        this.title = title;
        this.year = year;
        this.cast = cast;
        this.genres = genres;
    }

    public final String getTitle() {
        return title;
    }

    public final int getYear() {
        return year;
    }

    public final ArrayList<Actor> getCast() {
        return cast;
    }

    public final ArrayList<Genre> getGenres() {
        return genres;
    }
}

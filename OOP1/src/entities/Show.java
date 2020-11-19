package entities;

import entertainment.Season;
import entertainment.Genre;

import java.util.ArrayList;

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

    public Show(final String title, final int year,
                final ArrayList<Actor> cast, final ArrayList<Genre> genres,
                final int numberOfSeasons, final ArrayList<Season> seasons) {
        super(title, year, cast, genres);
        this.numberOfSeasons = numberOfSeasons;
        this.seasons = seasons;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public ArrayList<Season> getSeasons() {
        return seasons;
    }
}

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

    public Actor(final String name, final String careerDescription,
                 final ArrayList<Video> filmography,
                 final Map<ActorsAwards, Integer> awards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.awards = awards;
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
}

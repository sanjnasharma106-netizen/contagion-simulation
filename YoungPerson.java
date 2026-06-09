import java.awt.*;
// This category of people move fast and have high immunity

public class YoungPerson extends Person {
    // constructs a young person at a specific location
    public YoungPerson(Location coord) {
        super(coord);
    }
    // constructs a young person who is initially sick with a Contagion
    public YoungPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    // Return true if the person is alive
    @Override
    public boolean willMove() {
        return isAlive();
    }
    // Return the string "Young" for data tracking and statistics
    public String getType() {
        return "Young";
    }

    @Override
    public Color getColor() {
        // turns gray if the person is dead
        if (!isAlive()) {
            return Color.GRAY;
        }
        // turns red if the person is infected
        if (isInfected()) {
            return Color.RED;
        }
        // turns blue if the person is vaccinated
        if (isVaccinated()) {
            return Color.BLUE;
        }
        // turns cyan if the person is immune
        if (isImmune()) {
            return Color.CYAN;
        }
        // keeps its own color if none of the above happened
        return new Color(100, 220, 60);
    }
    // No damage penalty
    @Override
    public void attemptRecovery() {
        super.attemptRecovery();
    }
    // Return "Y" in plain text for the young people
    @Override
    public String toString() {
        return "Y";
    }
}

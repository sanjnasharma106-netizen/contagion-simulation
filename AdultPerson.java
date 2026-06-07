import java.awt.*;

public class AdultPerson extends Person {

    // Every time the simulation asks the adult if they want to move, this number goes up by 1.
    private int moveCounter;
    // constructor that creates an adult at a specific location
    public AdultPerson(Location coord) {
        super(coord);
    }
    // constructs an adult who is already sick with a Contagion
    public AdultPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    @Override
    public boolean willMove() {
        // dead person won't move anymore
        if (!isAlive()) {
            return false;
        }

        moveCounter++;

        return moveCounter % 2 == 0;
    }

    public String getType() {
        return "Adult";
    }
    
    @Override
    public void attemptRecovery() {
        // If the adult is currently sick (isInfected()), they automatically take 1 point of health damage
        if (isInfected()) {
            damage(1);
        }
        super.attemptRecovery();
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
        // keep its own color if the person is healthy(none of above happened)
        return new Color(34, 180, 34);
    }

    @Override
    public String toString() {
        return "A";
    }
}

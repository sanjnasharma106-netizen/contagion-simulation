import java.awt.*;

public class ElderlyPerson extends Person {
    // Every time the simulation asks the adult if they want to move, this number goes up by 1
    private int moveCounter;
    // construct an elderly person at a specific location
    public ElderlyPerson(Location coord) {
        super(coord);
    }
    // construct an elderly person with a Contagion
    public ElderlyPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    @Override
    public boolean willMove() {
        // move when the person is alive, but move solwly 
        if (!isAlive()) {
            return false;
        }

        moveCounter++;
        // moves slowest compared to Adults and Young people
        return moveCounter % 4 == 0;
    }

    public String getType() {
        return "Elderly";
    }

    @Override
    public Color getColor() {
        // turns gary if the person is dead
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
        // stays its own color if the person is healthy(none of the above happens)
        return new Color(0, 140, 60);
    }
    @Override
    public void attemptRecovery() {
        // damage 3 points once it's infected because this category has the worst immune system
        if (isInfected()) {
            damage(3);
        }
        // check whether the person is ran out of recoverytime
        super.attemptRecovery();
    }
    // Elderly people are "E" in plain text
    @Override
    public String toString() {
        return "E";
    }
}

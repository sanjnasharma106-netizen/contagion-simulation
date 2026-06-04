import java.awt.*;

public class ElderlyPerson extends Person {

    private int moveCounter;

    public ElderlyPerson(Location coord) {
        super(coord);
    }

    public ElderlyPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    @Override
    public boolean willMove() {

        if (!isAlive()) {
            return false;
        }

        moveCounter++;

        return moveCounter % 4 == 0;
    }

    public String getType() {
        return "Elderly";
    }

    @Override
    public Color getColor() {

        if (!isAlive()) {
            return Color.GRAY;
        }

        if (isInfected()) {
            return Color.RED;
        }

        if (isVaccinated()) {
            return Color.BLUE;
        }

        if (isImmune()) {
            return Color.CYAN;
        }

        return new Color(0, 140, 60);
    }
    @Override
    public void attemptRecovery() {
        if (isInfected()) {
            damage(3);
        }
        super.attemptRecovery();
    }
    @Override
    public String toString() {
        return "E";
    }
}

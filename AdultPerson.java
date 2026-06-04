import java.awt.*;

public class AdultPerson extends Person {

    private int moveCounter;

    public AdultPerson(Location coord) {
        super(coord);
    }

    public AdultPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    @Override
    public boolean willMove() {

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
        if (isInfected()) {
            damage(1);
        }
        super.attemptRecovery();
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

        return new Color(34, 180, 34);
    }

    @Override
    public String toString() {
        return "A";
    }
}

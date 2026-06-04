import java.awt.*;

public class YoungPerson extends Person {

    public YoungPerson(Location coord) {
        super(coord);
    }

    public YoungPerson(Location coord, Contagion disease) {
        super(coord, disease);
    }

    @Override
    public boolean willMove() {
        return isAlive();
    }

    public String getType() {
        return "Young";
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

        return new Color(100, 220, 60);
    }
    @Override
    public void attemptRecovery() {
        super.attemptRecovery();
    }

    @Override
    public String toString() {
        return "Y";
    }
}
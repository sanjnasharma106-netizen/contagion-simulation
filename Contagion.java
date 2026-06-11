import java.util.Random;

// The Contagion class describes the basic methods of
// Contagions and contains various return methods for
// statistics and functionality. Importantly, the Contagion
// class holds the attemptInfection, attemptKill, and
// spread methods, which will be used by all subclasses.

public class Contagion {

    private double infectionRate;
    private double deathRate;
    private int spreadDistance;
    private int damage;

    private Random rand;

    public Contagion() {
        this(1.0, 0.05, 1, 10);
    }

    public Contagion(double infectionRate,
                     double deathRate,
                     int spreadDistance,
                     int damage) {

        this.infectionRate = infectionRate;
        this.deathRate = deathRate;
        this.spreadDistance = spreadDistance;
        this.damage = damage;

        rand = new Random();
    }

    public String getType() {
        return "Generic";
    }

    public int getDamage() {
        return damage;
    }

    public double getInfectionRate() {
        return infectionRate;
    }

    public double getDeathRate() {
        return deathRate;
    }

    public int getSpreadDistance() {
        return spreadDistance;
    }

    // Attempts infection on Person "p". If "p" is already
    // infected or immune/vaccinated, this method has no effect.
    // Otherwise, whether or not infection status is applied
    // is based on infection rate.
    public void attemptInfection(Person p) {
        if (p.isInfected() || p.isImmune() || p.isVaccinated()) {
            return;
        }

        if (rand.nextDouble() < infectionRate) {
            p.infect(this);
        }
    }

    // Attempts to kill host -- Person "p." If "p" is already
    // dead or not infected, this method has no effect.
    // Otherwise, whether or not death status is applied
    // is based on death rate.
    public void attemptKill(Person p) {
        if (!p.isAlive() || !p.isInfected()) {
            return;
        }
        if (rand.nextDouble() < deathRate) {
            p.die();
        }
    }

    // This method looks at all squares surrounding
    // Person "infected" within a radius determined by
    // the contagion's "spreadDistance." For each square
    // with a healthy Person in it, the Contagion attempts
    // infection.
    public void spread(Person infected, Person[][] grid) {

        Location loc = infected.getLocation();

        int x = loc.getX();
        int y = loc.getY();

        for (int row = y - spreadDistance;
             row <= y + spreadDistance;
             row++) {

            for (int col = x - spreadDistance;
                 col <= x + spreadDistance;
                 col++) {

                // Addresses squares on/outside the border.
                if (row < 0 ||
                    row >= grid.length ||
                    col < 0 ||
                    col >= grid[0].length) {
                    continue;
                }

                Person target = grid[row][col];

                if (target != null &&
                    target != infected) {

                    attemptInfection(target);
                }
            }
        }
    }
}

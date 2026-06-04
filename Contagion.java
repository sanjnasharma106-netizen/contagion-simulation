import java.util.Random;

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

    public void attemptInfection(Person p) {

        if (p.isInfected()
                || p.isImmune()
                || p.isVaccinated()) {
            return;
        }

        if (rand.nextDouble() < infectionRate) {
            p.infect(this);
        }
    }

    public void attemptKill(Person p) {

        if (!p.isAlive() || !p.isInfected()) {
            return;
        }

        if (rand.nextDouble() < deathRate) {
            p.die();
        }
    }

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

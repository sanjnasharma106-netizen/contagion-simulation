import java.awt.*;

// The Person class describes the basic methods of
// Persons and contains various return methods for
// statistics and functionality. This class contains
// central methods for Person's functionality, such
// as move() & attemptRecover(), which is inherited
// by subclasses.

public class Person extends Entity {

    private boolean infected;
    private boolean vaccinated;
    private boolean immune;
    private boolean alive;

    private int stepTime;
    private int recoveryTime;
    private int health;
    private boolean hospitalized;

    private Contagion disease;

    private static final int FULL_HEALTH = 100;

    // Healthy person
    public Person(Location coord) {
        super(coord);

        alive = true;
        health = FULL_HEALTH;

        stepTime = 0;
        recoveryTime = 20;
    }

    // Infected person
    public Person(Location coord, Contagion disease) {
        super(coord);

        alive = true;
        health = FULL_HEALTH;

        infected = true;
        this.disease = disease;

        stepTime = 0;
        recoveryTime = 20;
    }

    public boolean isInfected() {
        return infected;
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public boolean isImmune() {
        return immune;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getHealth() {
        return health;
    }

    public int getRecoveryTime() {
        return recoveryTime;
    }

    public Contagion getDisease() {
        return disease;
    }

    public int getStepTime() {
        return stepTime;
    }

    public String getType() {
        return "Person";
    }

    // This method sets vaccinated status to true
    // only if the alive Person is not infected and
    // not immune.
    public void vaccinate() {
        if (alive && !infected && !immune) {
            vaccinated = true;
        }
    }

    public boolean isHospitalized() {
        return hospitalized;
    }

    public void setHospitalized(boolean hospitalized) {
        this.hospitalized = hospitalized;
    }

    // This method determines whether a Person will
    // move. A default Person will always move every
    // "tick" when alive.
    public boolean willMove() {
        return alive;
    }

    // This method attempts to move a person by
    // obtaining its current location, and generating
    // a new location based off Direction returned
    // with the "getDirection()" method. If this new
    // location invalid (not on grid, or already occupied),
    // the Person remains in its current spot, else it is moved.
    public void move(Person[][] grid) {
        if (!willMove()) {
            return;
        }

        Location current = getLocation();
        int x = current.getX();
        int y = current.getY();

        Direction dir = getDirection();
        int newX = x;
        int newY = y;

        switch (dir) {
            case NORTH:
                newY--;
                break;
            case SOUTH:
                newY++;
                break;
            case EAST:
                newX++;
                break;
            case WEST:
                newX--;
                break;
        }

        if (newX < 0 || newX >= grid[0].length ||
                newY < 0 || newY >= grid.length) {
            return;
        }

        if (grid[newY][newX] == null) {
            grid[y][x] = null;
            setLocation(new Location(newX, newY));
            grid[newY][newX] = this;
        }
    }

    // This method applies infection status
    public void infect(Contagion disease) {
        if (!infected && !immune && !vaccinated) {

            this.disease = disease;
            infected = true;
            recoveryTime = 20;
        }
    }

    // This method applies reduces health
    public void damage(int amount) {
        health -= amount;
        if (health <= 0) {
            die();
        }
    }

    // This method looks at infected people,
    // and if they're recoveryTime is up,
    // they will recover. Otherwise, they will
    // take damage.
    public void attemptRecovery() {
        if (!infected || !alive) {
            return;
        }

        if (recoveryTime <= 0) {
            recover();
        }
        else {
            if (disease != null) {
                damage(disease.getDamage());
            }
            recoveryTime--;
        }
    }

    // This method is called when Person is at
    // a hospital. It reduces recovery time and
    // increases health.
    public void treat() {
        if (infected && recoveryTime > 0) {
            recoveryTime -= 3;
            health += 4;
            if (health > FULL_HEALTH) {
                health = FULL_HEALTH;
            }
            if (recoveryTime < 0) {
                recoveryTime = 0;
            }
        }
    }

    // Updates statuses when Person recovers.
    public void recover() {
        infected = false;
        immune = true;
        vaccinated = false;
        disease = null;
        hospitalized = false;
    }

    // Updates statuses when Person dies.
    public void die() {
        alive = false;
        infected = false;
        hospitalized = false;
    }

    // Returns respective colors based off
    // of health statuses.
    public Color getColor() {
        if (!alive) {
            return Color.GRAY;
        }
        if (hospitalized) {
            return Color.MAGENTA;
        }
        if (infected) {
            return Color.RED;
        }
        if (vaccinated) {
            return Color.BLUE;
        }
        if (immune) {
            return Color.CYAN;
        }
        return Color.GREEN;
    }

    public String toString() {
        return "P";
    }
}

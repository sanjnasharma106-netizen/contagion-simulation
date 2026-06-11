import java.awt.*;
import java.util.Random;

// The Entity class provides basic methods for
// objects in this simulation, such as getDirection().

public class Entity {
    private Location coord;
    private Random rand;

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    public Entity(Location coord) {
        this.coord = coord;
        rand = new Random();
    }

    public Location getLocation() {
        return coord;
    }

    public void setLocation(Location coord) {
        this.coord = coord;
    }

    public Direction getDirection() {
        int randDir = rand.nextInt(4);
        if (randDir == 0) {
            return Direction.NORTH;
        }
        else if (randDir == 1) {
            return Direction.EAST;
        }
        else if (randDir == 2) {
            return Direction.SOUTH;
        }
        else {
            return Direction.WEST;
        }
    }

    public boolean willMove() {
        return false;
    }

    public String toString() {
        return "X";
    }

    public Color getColor() {
        return Color.BLACK;
    }
}

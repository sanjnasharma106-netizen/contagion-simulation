import java.awt.*;
import java.util.ArrayList;

/**
 * Hospital — a Building subclass that treats nearby infected people.
 *
 * Each tick it scans all people; those within TREAT_RADIUS cells of the
 * hospital centre are considered "admitted" (up to its capacity) and
 * receive a call to treat(), which speeds up their recovery.
 */
public class Hospital extends Building {

    private static final int TREAT_RADIUS = 4;  // cells around hospital centre
    private static final int VACCINE_RADIUS = 5;
    private int capacity;                        // max patients treated per tick
    private int currentPatients;                 // patients being treated this tick
    private int vaccinationsThisTick;

    public Hospital(int x, int y, int capacity) {
        super(x, y, 3, 3);   // hospitals occupy a 3×3 block of cells
        this.capacity = capacity;
    }

    public int getCapacity()       { return capacity;        }
    public int getCurrentPatients(){ return currentPatients; }
    public int getVaccinationsThisTick(){ return vaccinationsThisTick; }

    /**
     * Admit and treat up to `capacity` infected people that are within
     * TREAT_RADIUS of the hospital's centre cell.
     */
    @Override
    public void update(ArrayList<Person> people) {
        currentPatients = 0;
        vaccinationsThisTick = 0;

        int centreX = getX() + getWidth()  / 2;
        int centreY = getY() + getHeight() / 2;

        for (Person p : people) {
            if (currentPatients >= capacity) break;
            if (!p.isAlive() || !p.isInfected()) continue;

            Location loc = p.getLocation();
            int dx = Math.abs(loc.getX() - centreX);
            int dy = Math.abs(loc.getY() - centreY);

            if (dx <= TREAT_RADIUS && dy <= TREAT_RADIUS) {
                admitPatient(p);
            }
        }

        for (Person p : people) {
            if (currentPatients + vaccinationsThisTick >= capacity) break;
            if (!p.isAlive() || p.isInfected() || p.isImmune() || p.isVaccinated()) continue;

            Location loc = p.getLocation();
            int dx = Math.abs(loc.getX() - centreX);
            int dy = Math.abs(loc.getY() - centreY);

            if (dx <= VACCINE_RADIUS && dy <= VACCINE_RADIUS) {
                p.vaccinate();
                vaccinationsThisTick++;
            }
        }
    }

    /** Place person into treatment. */
    public void admitPatient(Person p) {
        p.setHospitalized(true);
        treatPatient(p);
        currentPatients++;
    }

    /** Apply one round of treatment. */
    public void treatPatient(Person p) {
        p.treat();
    }

    @Override
    public Color getColor() { return new Color(200, 230, 255); }  // light blue

    @Override
    public void render(Graphics g, int cellSize) {
        super.render(g, cellSize);
        // Draw a red cross on the hospital
        int px = getX() * cellSize;
        int py = getY() * cellSize;
        int w  = getWidth()  * cellSize;
        int h  = getHeight() * cellSize;
        g.setColor(new Color(220, 30, 30));
        int thickness = Math.max(2, cellSize / 4);
        // Horizontal bar of cross
        g.fillRect(px + thickness, py + h / 2 - thickness / 2, w - 2 * thickness, thickness);
        // Vertical bar of cross
        g.fillRect(px + w / 2 - thickness / 2, py + thickness, thickness, h - 2 * thickness);
    }
}

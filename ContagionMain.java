import javax.swing.*;
import java.util.*;

public class ContagionMain {
    public static final int COLS = 50;
    public static final int ROWS = 50;
    public static final int POPULATION = 100;

    public static final int MINIMUM_AGE = 5;
    public static final int MAXIMUM_AGE = MINIMUM_AGE + POPULATION;

    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(ContagionGUI::new);
    }

    public static void simulate(ArrayList<Person> people, Person[][] grid) throws InterruptedException {
        // everyone is moved
        for (Person p: people) {
            if (!p.isAlive()) {
                continue;
            }
            p.move(grid);
        }

        // all infected try to recover,
        // spread disease, then disease
        // tries to kill
        for (Person p: people) {
            if (!p.isAlive()) {
                continue;
            }
            if (p.isInfected()) {
                p.attemptRecovery();
                p.getDisease().spread(p, grid);
                p.getDisease().attemptKill(p);
            }
        }
    }
}

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class ContagionGUI extends JFrame {

    // Grid constants
    private static final int COLS = 50;
    private static final int ROWS = 50;
    private static final int CELL = 12;

    // Simulation state
    private Person[][]        grid;
    private ArrayList<Person>   people;
    private ArrayList<Hospital> hospitals;

    // Timer
    private Timer simTimer;

    // Stats labels
    private JLabel lblTick;
    private JLabel lblHealthy;
    private JLabel lblInfected;
    private JLabel lblImmune;
    private JLabel lblVaccinated;
    private JLabel lblDead;
    private JLabel lblHospitalised;

    // Setup controls
    private JPanel        setupPanel;
    private JComboBox<String> cbDisease;
    private JSlider       sliderPop;
    private JSlider       sliderHospitals;
    private JSlider       sliderVaccinated;
    private JButton       btnStart;
    private JButton       btnPause;
    private JButton       btnReset;

    // Grid drawing panel (inner class)
    private GridPanel gridPanel;

    private int     tick    = 0;
    private boolean running = false;
    private boolean simulationEnded = false;
    private boolean finalResultsPrinted = false;

    // -------------------------------------------------------------------------
    public ContagionGUI() {
        super("Contagion Zone Simulation");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeSimulation();
            }
        });
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(20, 20, 30));

        // Title
        JLabel title = new JLabel("CONTAGION ZONE", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(new Color(220, 60, 60));
        title.setBorder(new EmptyBorder(10, 0, 4, 0));
        add(title, BorderLayout.NORTH);

        // Grid panel
        gridPanel = new GridPanel();
        gridPanel.setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
        gridPanel.setBackground(new Color(10, 10, 18));

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(new Color(10, 10, 18));
        gridWrapper.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));
        gridWrapper.add(gridPanel, BorderLayout.CENTER);
        add(gridWrapper, BorderLayout.CENTER);

        // Right panel
        JPanel rightPanel = buildRightPanel();
        rightPanel.setPreferredSize(new Dimension(240, ROWS * CELL));
        add(rightPanel, BorderLayout.EAST);

        // Simulation timer (200ms per tick)
        simTimer = new Timer(200, e -> step());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    // -------------------------------------------------------------------------
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(25, 25, 38));
        panel.setBorder(new EmptyBorder(10, 8, 10, 8));

        // -- Setup section --
        setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.Y_AXIS));
        setupPanel.setBackground(new Color(25, 25, 38));
        setupPanel.setAlignmentX(LEFT_ALIGNMENT);

        setupPanel.add(sectionLabel("SETUP"));
        setupPanel.add(Box.createVerticalStrut(6));

        setupPanel.add(smallLabel("Disease type:"));
        cbDisease = new JComboBox<String>(new String[]{"Airborne", "Contact"});
        cbDisease.setBackground(new Color(40, 40, 60));
        cbDisease.setForeground(new Color(220, 220, 255));
        cbDisease.setFont(new Font("Monospaced", Font.PLAIN, 12));
        cbDisease.setMaximumSize(new Dimension(220, 28));
        cbDisease.setAlignmentX(LEFT_ALIGNMENT);
        setupPanel.add(cbDisease);
        setupPanel.add(Box.createVerticalStrut(8));

        sliderPop        = addSlider(setupPanel, "Population:",   20, 200, 100);
        sliderHospitals  = addSlider(setupPanel, "Hospitals:",     0,   5,   2);
        sliderVaccinated = addSlider(setupPanel, "Initial infected %:",  1,  20,  5);

        setupPanel.add(Box.createVerticalStrut(10));

        btnStart = makeButton("START", new Color(50, 180, 80), 13);
        btnStart.setAlignmentX(LEFT_ALIGNMENT);
        btnStart.addActionListener(e -> initAndStart());
        setupPanel.add(btnStart);

        panel.add(setupPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(makeDivider());

        // -- Pause / Reset row --
        JPanel ctrlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        ctrlRow.setBackground(new Color(25, 25, 38));
        ctrlRow.setAlignmentX(LEFT_ALIGNMENT);

        btnPause = makeButton("Pause", new Color(200, 160, 30), 11);
        btnPause.setEnabled(false);
        btnPause.addActionListener(e -> togglePause());

        btnReset = makeButton("Reset", new Color(180, 60, 60), 11);
        btnReset.addActionListener(e -> resetSim());

        ctrlRow.add(btnPause);
        ctrlRow.add(btnReset);
        panel.add(ctrlRow);
        panel.add(makeDivider());

        // -- Legend --
        panel.add(Box.createVerticalStrut(6));
        panel.add(sectionLabel("LEGEND"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(legendRow(new Color(100, 220, 60),  "Young   - Healthy", "circle"));
        panel.add(legendRow(new Color(34,  180, 34),  "Adult   - Healthy", "square"));
        panel.add(legendRow(new Color(0,   140, 60),  "Elderly - Healthy", "triangle"));
        panel.add(legendRow(Color.RED,                "Any     - Infected", "square"));
        panel.add(legendRow(new Color(0,   100, 220), "Any     - Vaccinated", "square"));
        panel.add(legendRow(Color.CYAN,               "Any     - Immune", "square"));
        panel.add(legendRow(Color.MAGENTA,            "Any     - Hospital", "square"));
        panel.add(legendRow(Color.GRAY,               "Any     - Dead", "x"));
        panel.add(legendRow(new Color(200, 230, 255), "Hospital", "hospital"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(makeDivider());

        // -- Stats --
        panel.add(Box.createVerticalStrut(6));
        panel.add(sectionLabel("STATISTICS"));
        panel.add(Box.createVerticalStrut(4));

        lblTick         = statLabel("Tick:         0");
        lblHealthy      = statLabel("Healthy:      -");
        lblInfected     = statLabel("Infected:     -");
        lblImmune       = statLabel("Immune:       -");
        lblVaccinated   = statLabel("Vaccinated:   -");
        lblDead         = statLabel("Dead:         -");
        lblHospitalised = statLabel("In Hospital:  -");

        panel.add(lblTick);
        panel.add(lblHealthy);
        panel.add(lblInfected);
        panel.add(lblImmune);
        panel.add(lblVaccinated);
        panel.add(lblDead);
        panel.add(lblHospitalised);

        return panel;
    }

    // -------------------------------------------------------------------------
    private void initAndStart() {
        int population   = sliderPop.getValue();
        int numHospitals = sliderHospitals.getValue();
        int infectedPct  = sliderVaccinated.getValue();
        String diseaseType = (String) cbDisease.getSelectedItem();

        grid      = new Person[ROWS][COLS];
        people    = new ArrayList<Person>();
        hospitals = new ArrayList<Hospital>();

        // Place hospitals and record their cells
        HashSet<String> hospitalCells = new HashSet<String>();
        Random rng = new Random();

        for (int h = 0; h < numHospitals; h++) {
            int hx, hy;
            int attempts = 0;
            do {
                hx = 2 + rng.nextInt(COLS - 6);
                hy = 2 + rng.nextInt(ROWS - 6);
                attempts++;
            } while (hospitalCells.contains(hx + "," + hy) && attempts < 100);

            Hospital hosp = new Hospital(hx, hy, 5);
            hospitals.add(hosp);
            for (int row = hy; row < hy + 3; row++) {
                for (int col = hx; col < hx + 3; col++) {
                    hospitalCells.add(col + "," + row);
                }
            }
        }

        // Collect free locations
        ArrayList<Location> allLocs = new ArrayList<Location>();
        for (int x = 0; x < COLS; x++) {
            for (int y = 0; y < ROWS; y++) {
                if (!hospitalCells.contains(x + "," + y)) {
                    allLocs.add(new Location(x, y));
                }
            }
        }
        Collections.shuffle(allLocs);

        int infectedCount = Math.max(1, (int)(population * infectedPct / 100.0));
        Contagion disease;
        if ("Airborne".equals(diseaseType)) {
            disease = new AirborneContagion();
        } else {
            disease = new ContactContagion();
        }

        for (int i = 0; i < population && i < allLocs.size(); i++) {
            Location loc = allLocs.get(i);
            Person p = makeRandomPerson(loc, rng);

            if (i < infectedCount) {
                p.infect(disease);
            }

            people.add(p);
            grid[loc.getY()][loc.getX()] = p;
        }

        tick    = 0;
        running = true;
        simulationEnded = false;
        finalResultsPrinted = false;

        printInitialGrid();
        printInitialStats();

        btnPause.setEnabled(true);
        btnPause.setText("Pause");
        setupPanel.setVisible(false);
        simTimer.start();
        updateStats();
        gridPanel.repaint();
    }

    private void printInitialGrid() {
        System.out.println("Simulation started at tick " + tick);
        System.out.println("Initial grid:");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Person p = grid[row][col];

                if (p == null) {
                    System.out.print(".");
                } else {
                    System.out.print(p.toString());
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    private void printInitialStats() {
        int healthy = 0;
        int infected = 0;
        int vaccinated = 0;

        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);

            if (p.isInfected()) {
                infected++;
            } else if (p.isVaccinated()) {
                vaccinated++;
            } else {
                healthy++;
            }
        }

        System.out.println("Initial healthy: " + healthy);
        System.out.println("Initial infected: " + infected);
        System.out.println("Initial vaccinated: " + vaccinated);
        System.out.println();
    }

    private Person makeRandomPerson(Location loc, Random rng) {
        int roll = rng.nextInt(3);
        if (roll == 0) return new YoungPerson(loc);
        if (roll == 1) return new AdultPerson(loc);
        return new ElderlyPerson(loc);
    }

    // -------------------------------------------------------------------------
    private void step() {
        if (!running || simulationEnded || people == null) return;

        if (countInfected() == 0) {
            endSimulation();
            return;
        }

        tick++;

        // Move all alive people
        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);
            if (p.isAlive()) {
                p.move(grid);
            }
        }

        // Disease tick
        for (int i = 0; i < people.size(); i++) {

            Person p = people.get(i);

            if (!p.isAlive() || !p.isInfected()) {
                continue;
            }

            if (p.getDisease() != null) {
                p.getDisease().spread(p, grid);
            }

            p.attemptRecovery();

            if (p.isAlive() && p.isInfected() && p.getDisease() != null) {
                p.getDisease().attemptKill(p);
            }
        }
        // Hospital tick
        for (int i = 0; i < people.size(); i++) {
            people.get(i).setHospitalized(false);
        }

        for (int i = 0; i < hospitals.size(); i++) {
            hospitals.get(i).update(people);
        }

        updateStats();
        gridPanel.repaint();

        // Auto-stop when no infected remain
        if (countInfected() == 0) {
            endSimulation();
        }
    }

    private int countInfected() {
        int infectedCount = 0;

        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).isInfected()) {
                infectedCount++;
            }
        }

        return infectedCount;
    }

    private void endSimulation() {
        if (simulationEnded) {
            return;
        }

        simTimer.stop();
        running = false;
        simulationEnded = true;
        btnPause.setEnabled(false);
        btnPause.setText("Ended");
        updateStats();
        gridPanel.repaint();
        printFinalResults();
    }

    private void closeSimulation() {
        simTimer.stop();
        running = false;
        simulationEnded = true;

        if (people != null) {
            printFinalResults();
        }

        dispose();
        System.exit(0);
    }

    private void printFinalResults() {
        if (finalResultsPrinted) {
            return;
        }

        printEndingGrid();
        printFinalStats();
        finalResultsPrinted = true;
    }

    private void printEndingGrid() {
        System.out.println("Ending grid:");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Person p = grid[row][col];

                if (p == null) {
                    System.out.print(".");
                } else if (!p.isAlive()) {
                    System.out.print("D");
                } else if (p.isImmune()) {
                    System.out.print("I");
                } else if (p.isVaccinated()) {
                    System.out.print("V");
                } else if (p.isInfected()) {
                    System.out.print("X");
                } else {
                    System.out.print(p.toString());
                }
            }

            System.out.println();
        }

        System.out.println();
    }

    private void printFinalStats() {
        int healthy = 0;
        int immune = 0;
        int vaccinated = 0;
        int dead = 0;

        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);

            if (!p.isAlive()) {
                dead++;
            } else if (p.isImmune()) {
                immune++;
            } else if (p.isVaccinated()) {
                vaccinated++;
            } else {
                healthy++;
            }
        }

        System.out.println("Simulation ended at tick " + tick);
        System.out.println("Final healthy: " + healthy);
        System.out.println("Final immune: " + immune);
        System.out.println("Final vaccinated: " + vaccinated);
        System.out.println("Final dead: " + dead);
        System.out.println();
    }

    private void togglePause() {
        if (simulationEnded) {
            return;
        }

        if (running) {
            simTimer.stop();
            running = false;
            btnPause.setText("Resume");
        } else {
            simTimer.start();
            running = true;
            btnPause.setText("Pause");
        }
    }

    private void resetSim() {
        simTimer.stop();
        running   = false;
        simulationEnded = false;
        finalResultsPrinted = false;
        people    = null;
        hospitals = new ArrayList<Hospital>();
        grid      = new Person[ROWS][COLS];
        tick      = 0;
        setupPanel.setVisible(true);
        btnPause.setEnabled(false);
        btnPause.setText("Pause");
        lblTick.setText("Tick:         0");
        lblHealthy.setText("Healthy:      -");
        lblInfected.setText("Infected:     -");
        lblImmune.setText("Immune:       -");
        lblVaccinated.setText("Vaccinated:   -");
        lblDead.setText("Dead:         -");
        lblHospitalised.setText("In Hospital:  -");
        gridPanel.repaint();
    }

    // -------------------------------------------------------------------------
    private void updateStats() {
        if (people == null) return;
        int healthy = 0, infected = 0, immune = 0, vaccinated = 0, dead = 0, hosp = 0;

        for (int i = 0; i < people.size(); i++) {
            Person p = people.get(i);
            if (!p.isAlive())          dead++;
            else if (p.isInfected())   infected++;
            else if (p.isImmune())     immune++;
            else if (p.isVaccinated()) vaccinated++;
            else                       healthy++;
        }

        for (int i = 0; i < hospitals.size(); i++) {
            hosp += hospitals.get(i).getCurrentPatients();
        }

        lblTick.setText("Tick:         " + tick);
        lblHealthy.setText("Healthy:      " + healthy);
        lblInfected.setText("Infected:     " + infected);
        lblImmune.setText("Immune:       " + immune);
        lblVaccinated.setText("Vaccinated:   " + vaccinated);
        lblDead.setText("Dead:         " + dead);
        lblHospitalised.setText("In Hospital:  " + hosp);

        lblInfected.setForeground(infected > 0
                ? new Color(255, 100, 100)
                : new Color(160, 200, 160));
        lblDead.setForeground(dead > 0
                ? Color.GRAY
                : new Color(160, 200, 160));
    }

    // -------------------------------------------------------------------------
    // Inner class — draws the grid
    private class GridPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // Grid lines
            g2.setColor(new Color(30, 30, 45));
            for (int x = 0; x <= COLS; x++)
                g2.drawLine(x * CELL, 0, x * CELL, ROWS * CELL);
            for (int y = 0; y <= ROWS; y++)
                g2.drawLine(0, y * CELL, COLS * CELL, y * CELL);

            // Hospitals
            if (hospitals != null) {
                for (int i = 0; i < hospitals.size(); i++) {
                    hospitals.get(i).render(g2, CELL);
                }
            }

            // People
            if (people != null) {
                for (int i = 0; i < people.size(); i++) {
                    Person p = people.get(i);
                    Location loc = p.getLocation();
                    int px   = loc.getX() * CELL + 1;
                    int py   = loc.getY() * CELL + 1;
                    int size = CELL - 2;

                   g2.setColor(p.getColor());

                    if (!p.isAlive()) {

                        g2.drawLine(px, py, px + size, py + size);
                        g2.drawLine(px + size, py, px, py + size);

                    }
                    else if (p instanceof YoungPerson) {

                        g2.fillOval(px, py, size, size);

                    }
                    else if (p instanceof AdultPerson) {

                        g2.fillRect(px, py, size, size);

                    }
                    else {

                        int[] xs = {
                            px + size / 2,
                            px + size,
                            px
                        };

                        int[] ys = {
                            py,
                            py + size,
                            py + size
                        };

                        g2.fillPolygon(xs, ys, 3);
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // UI helper methods

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        l.setForeground(new Color(200, 200, 255));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(new Color(160, 160, 190));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JLabel statLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, 11));
        l.setForeground(new Color(160, 200, 160));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel legendRow(Color color, String label, String shape) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        row.setBackground(new Color(25, 25, 38));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel swatch = new LegendSwatch(color, shape);
        swatch.setPreferredSize(new Dimension(14, 14));
        swatch.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 100)));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 10));
        lbl.setForeground(new Color(190, 190, 210));

        row.add(swatch);
        row.add(lbl);
        return row;
    }

    private class LegendSwatch extends JPanel {
        private Color color;
        private String shape;

        public LegendSwatch(Color color, String shape) {
            this.color = color;
            this.shape = shape;
            setBackground(new Color(25, 25, 38));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if ("circle".equals(shape)) {
                g2.fillOval(x, y, size, size);
            } else if ("triangle".equals(shape)) {
                int[] xs = {x + size / 2, x + size, x};
                int[] ys = {y, y + size, y + size};
                g2.fillPolygon(xs, ys, 3);
            } else if ("x".equals(shape)) {
                g2.drawLine(x, y, x + size, y + size);
                g2.drawLine(x + size, y, x, y + size);
            } else if ("hospital".equals(shape)) {
                g2.fillRect(x, y, size, size);
                g2.setColor(new Color(220, 30, 30));
                int thick = Math.max(2, size / 4);
                g2.fillRect(x + thick, y + size / 2 - thick / 2,
                            size - 2 * thick, thick);
                g2.fillRect(x + size / 2 - thick / 2, y + thick,
                            thick, size - 2 * thick);
            } else {
                g2.fillRect(x, y, size, size);
            }
        }
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 80));
        sep.setMaximumSize(new Dimension(220, 2));
        return sep;
    }

    private JSlider addSlider(JPanel parent, String labelText,
                               int min, int max, int val) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(new Color(25, 25, 38));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(220, 44));

        JLabel lbl    = smallLabel(labelText);
        JLabel valLbl = smallLabel(String.valueOf(val));
        valLbl.setForeground(new Color(220, 220, 100));

        JSlider slider = new JSlider(min, max, val);
        slider.setBackground(new Color(25, 25, 38));
        slider.addChangeListener(e -> valLbl.setText(String.valueOf(slider.getValue())));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(25, 25, 38));
        top.add(lbl,    BorderLayout.WEST);
        top.add(valLbl, BorderLayout.EAST);

        row.add(top,    BorderLayout.NORTH);
        row.add(slider, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createVerticalStrut(4));
        return slider;
    }

    private JButton makeButton(String text, Color bg, int fontSize) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, fontSize));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

}

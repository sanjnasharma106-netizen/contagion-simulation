import java.awt.*;

/**
 * Building — a static structure placed on the grid.
 * Subclassed by Hospital.
 */
public class Building {

    private int x;       // top-left grid column
    private int y;       // top-left grid row
    private int width;   // in grid cells
    private int height;  // in grid cells

    public Building(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    public int getX()      { return x;      }
    public int getY()      { return y;      }
    public int getWidth()  { return width;  }
    public int getHeight() { return height; }

    /** Returns true if the grid cell (col, row) is inside this building. */
    public boolean contains(int col, int row) {
        return col >= x && col < x + width && row >= y && row < y + height;
    }

    /** Subclasses override to return a specific colour. */
    public Color getColor() { return new Color(160, 130, 90); }

    /** Called every simulation tick; subclasses may override. */
    public void update(java.util.ArrayList<Person> people) { }

    public void render(Graphics g, int cellSize) {
        g.setColor(getColor());
        g.fillRect(x * cellSize, y * cellSize, width * cellSize, height * cellSize);
        g.setColor(Color.BLACK);
        g.drawRect(x * cellSize, y * cellSize, width * cellSize, height * cellSize);
    }
}

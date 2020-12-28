package commander;

import cell.Cell;
import game.EnergySource;
import group.Colony;
import group.Group;
import group.Squadron;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Space;

import java.util.*;

import static processing.core.PVector.dist;

public abstract class Commander {
    private final Space<Cell> cell_map;
    private final Space<EnergySource> source_map;

    private final List<Squadron> squadrons;
    private final List<Colony> colonies;

    private final List<Cell> cells;
    private final List<Cell> spawned;
    private final int colour;

    public Commander(Space<Cell> cell_map, Space<EnergySource> source_map, int colour) {
        this.cell_map = cell_map;
        this.source_map = source_map;

        this.colonies = new ArrayList<>();
        this.squadrons = new ArrayList<>();

        this.cells = new ArrayList<>();
        this.spawned = new ArrayList<>();

        this.colour = colour;
    }

    public void place_squadron(List<Cell> cells, PVector position) {
        Squadron squadron = squadrons.stream()
                .filter(s -> dist(position, s.rally) < s.radius())
                .findFirst()
                .orElseGet(() -> {
                    Squadron s = new Squadron(this, position);
                    squadrons.add(s);
                    return s;
                });

        squadron.attach(cells);
    }

    public void place_colony(List<Cell> cells, PVector position) {
        Colony colony = colonies.stream()
                .filter(c -> dist(position, c.center) < c.radius())
                .findFirst()
                .orElseGet(() -> {
                    Colony c = new Colony(position);
                    colonies.add(c);
                    return c;
                });

        colony.attach(cells);
    }

    public List<Cell> cells() {
        return cells;
    }

    public int size() {
        return cells.size();
    }

    public boolean inactive() {
        return colonies.isEmpty() && squadrons.isEmpty();
    }

    public void update() {
        colonies.removeIf(colony -> {
            colony.update();
            return colony.empty();
        });

        squadrons.removeIf(squadron -> {
            squadron.update();
            return squadron.empty();
        });
    }

    public List<Colony> getColonies() {
        return colonies;
    }

    public List<Squadron> getSquadrons() {
        return squadrons;
    }

    public void highlightCells(PGraphics g) {
        colonies.forEach(colony -> colony.highlightCells(g));
        squadrons.forEach(squadron -> squadron.highlightCells(g));
    }

    public void drawColonies(PGraphics g) {
        colonies.forEach(colony -> colony.draw(g));
    }

    public void drawSquadrons(PGraphics g) {
        squadrons.forEach(squadron -> squadron.draw(g));
    }

    public int getColour() {
        return colour;
    }

    public Space<Cell> cellMap() {
        return cell_map;
    }

    public Space<EnergySource> sourceMap() {
        return source_map;
    }

    public void spawn(Cell cell) {
        this.spawned.add(cell);
    }

    public List<Cell> getSpawned() {
        return spawned;
    }
}
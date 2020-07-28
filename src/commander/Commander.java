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
    private Space<Cell> cell_map;
    private Space<EnergySource> source_map;
    private List<Squadron> squadrons;
    private List<Colony> colonies;
    private Map<Group, Cell> spawned;
    private int colour;

    public Commander(Space<Cell> cell_map, Space<EnergySource> source_map, int colour) {
        this.cell_map = cell_map;
        this.source_map = source_map;

        this.colonies = new ArrayList<>();
        this.squadrons = new ArrayList<>();

        this.spawned = new HashMap<>();

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
        List<Cell> cells = new ArrayList<>();

        colonies.stream().map(Group::cells).forEach(cells::addAll);
        squadrons.stream().map(Group::cells).forEach(cells::addAll);

        return cells;
    }

    public int size() {
        return colonies.stream().mapToInt(Group::size).sum()
                + squadrons.stream().mapToInt(Group::size).sum();
    }

    public boolean inactive() {
        return colonies.isEmpty() && squadrons.isEmpty();
    }

    public void update() {
        spawned.forEach(Group::attach);
        spawned.clear();

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

    public void spawn(Group group, Cell cell) {
        spawned.put(group, cell);
    }

    public Collection<Cell> getSpawned() {
        return spawned.values();
    }
}
package group;

import cell.Cell;
import individual.Individual;
import processing.core.PGraphics;

import java.util.*;

import static cell.Cell.MEMBRANE_RADIUS;
import static processing.core.PApplet.sq;
import static processing.core.PApplet.sqrt;

public abstract class Group {
    private final List<Cell> additions;
    private final Map<Cell, Individual> members;

    public Group() {
        this.additions = new ArrayList<>();
        this.members = new HashMap<>();
    }

    public void attach(List<Cell> group) {
        group.forEach(this::attach);
    }

    public void attach(Cell cell) {
        additions.add(cell);
    }

    public void detach(List<Cell> group) {
        group.forEach(this::detach);
    }

    public void detach(Cell cell) {
        members.remove(cell);
    }

    public List<Cell> take(int n) {
        List<Cell> taken = new ArrayList<>();

        for (Cell cell : cells()) {
            if (taken.size() < n) taken.add(cell);
            else break;
        }

        detach(taken);

        return taken;
    }

    public Set<Cell> cells() {
        return members.keySet();
    }

    public float radius() {
        return MEMBRANE_RADIUS + sqrt(members.size() * sq(MEMBRANE_RADIUS));
    }

    public int size() {
        return members.size();
    }

    public boolean empty() {
        return members.isEmpty();
    }

    public void highlightCells(PGraphics g) {
        cells().forEach(cell -> cell.highlight(g));
    }

    abstract Individual member(Cell cell);

    public void update() {
        members.values().forEach(Individual::update);

        for (Cell cell : additions) {
            if (members.containsKey(cell)) {
                continue;
            }

            cell.getGroup().detach(cell);
            cell.setGroup(this);
            members.put(cell, member(cell));
        }

        additions.clear();
    }

    public abstract void draw(PGraphics g);
}


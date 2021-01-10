package group;

import cell.Cell;
import commander.Commander;
import individual.Combat;
import individual.Individual;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static cell.Cell.CELL_SPEED;
import static game.Colours.*;
import static processing.core.PVector.dist;

public class Squadron extends Group {
    private final Commander commander;
    private final List<Cell> targets;

    public final PVector rally;

    private int idle;

    public Squadron(Commander commander, PVector rally) {
        this.commander = commander;
        this.targets = new ArrayList<>();
        this.rally = rally.copy();

        this.idle = 0;
    }

    @Override
    Individual member(Cell cell) {
        return new Combat(cell, this);
    }

    public void update() {
        super.update();

        // TODO: 04/01/2021 drift based on average group position
        PVector drift = new PVector();

        getTargets().clear();
        List<Cell> cells = commander.cellMap().get(rally, radius());
        for (Cell cell : cells) {
            if (cell.getCommander() != commander && !cell.energy().empty() && dist(cell.getPosition(), rally) < radius()) {
                getTargets().add(cell);
                drift.add(PVector.sub(cell.getPosition(), rally));
            }
        }

        drift.normalize();
        rally.add(drift);

        if (targets.isEmpty()) idle++;
        else idle = 0;
    }

    public boolean idle() {
        return idle > 100;
    }

    public float radius() {
        return super.radius() * 2;
    }

    public void draw(PGraphics g) {
        g.fill(RED);
        g.stroke(0,0);
        g.circle(rally.x, rally.y, radius() * 2);

        g.stroke(WHITE);

        for (Cell target : getTargets()) {
            PVector position = target.getPosition();
            g.line(position.x, position.y, rally.x, rally.y);
        }

        g.fill(WHITE, 255);
        g.text(idle, rally.x, rally.y);
    }

    public List<Cell> getTargets() {
        return targets;
    }

    public Commander getCommander() {
        return commander;
    }
}

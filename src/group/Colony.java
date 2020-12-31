package group;

import cell.Cell;
import individual.Colonist;
import individual.Individual;
import processing.core.PGraphics;
import processing.core.PVector;

import static game.Colours.*;

public class Colony extends Group {
    public final PVector center;

    public Colony(PVector center) {
        this.center = center.copy();
    }

    @Override
    Individual member(Cell cell) {
        return new Colonist(cell, this);
    }

    @Override
    public void detach(Cell cell) {
        super.detach(cell);
        cell.getAttachments().clear();
    }

    public void draw(PGraphics g) {
        g.fill(BLUE);
        g.stroke(0,0);
        g.circle(center.x, center.y, radius() * 2);
    }
}

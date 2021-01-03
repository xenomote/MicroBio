package game;

import cell.Cell;
import space.Area;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Space;
import space.Spatial;

import java.util.ArrayList;
import java.util.List;

import static game.Colours.*;

public class EnergySource implements Spatial {
    private final PVector position;
    private final float radius;
    private final float rate;

    private final Area<Cell> area;
    private final List<Cell> collisions;

    EnergySource(PVector position, float radius, float rate, Space<Cell> cells) {
        this.position = position;
        this.radius = radius;
        this.rate = rate;

        this.collisions = new ArrayList<>();
        this.area = new Area<>(this, cells, collisions);

    }

    void update() {
        collisions.forEach(cell -> cell.energy().add(rate));
    }

    void draw(PGraphics g) {
        g.fill(GREY);
        g.stroke(0,0);
        g.circle(position.x, position.y, radius * 2);
    }

    public Area<Cell> getCollider() {
        return area;
    }

    @Override
    public PVector getPosition() {
        return position;
    }

    @Override
    public float getRadius() {
        return radius;
    }
}
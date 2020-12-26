package game;

import cell.Cell;
import cell.Collider;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Space;
import space.Spatial;

import java.util.ArrayList;
import java.util.List;

import static game.Colours.*;

public class EnergySource implements Spatial {
    private PVector position;
    private float radius;
    float rate;

    private Collider<Cell> collider;
    private List<Cell> collisions;

    EnergySource(PVector position, float radius, float rate, Space<Cell> cells) {
        this.position = position;
        this.radius = radius;
        this.rate = rate;

        this.collisions = new ArrayList<>();
        this.collider = new Collider<>(this, cells, collisions);

    }

    void update() {
        collisions.forEach(cell -> cell.addEnergy(rate));
    }

    void draw(PGraphics g) {
        g.fill(GREY);
        g.stroke(0,0);
        g.circle(position.x, position.y, radius * 2);
    }

    public Collider<Cell> getCollider() {
        return collider;
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
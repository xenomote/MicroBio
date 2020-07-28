package cell;

import processing.core.PVector;
import space.Space;
import space.Spatial;

import java.util.List;

public class Collider<T extends Spatial> {
    private Spatial item;
    private Space<T> map;
    private List<T> collisions;

    public Collider(Spatial item, Space<T> map, List<T> collisions) {
        this.item = item;
        this.map = map;
        this.collisions = collisions;
    }

    public void update() {
        collisions.clear();
        List<T> items = map.get(item.getPosition(), item.getRadius() * 2);

        for (T other : items) {
            if (colliding(other)) collisions.add(other);
        }
    }

    private boolean colliding(T other) {
        return PVector.dist(item.getPosition(), other.getPosition()) < item.getRadius() + other.getRadius();
    }
}

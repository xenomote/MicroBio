package space;

import processing.core.PVector;

import java.util.List;

public class Area<T extends Spatial> {
    private final Spatial item;
    private final Space<T> map;
    private final List<T> collisions;

    public Area(Spatial item, Space<T> map, List<T> collisions) {
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

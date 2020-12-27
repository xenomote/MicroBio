package space;

import processing.core.PVector;

import java.util.ArrayList;

public abstract class QuadSpace<T extends Spatial> implements Space<T> {
    static final int ITEMS_PER_REGION = 10;

    public static int QUAD_INSTANCE;

    final PVector min;
    final PVector max;
    final PVector mid;

    boolean fresh = true;

    QuadSpace(PVector min, PVector max) {
        this.min = min;
        this.max = max;
        this.mid = PVector.add(min, max).div(2);

        QUAD_INSTANCE++;
    }

    QuadSpace(PVector mid, float size) {
        this.mid = mid;
        this.min = new PVector(mid.x - size, mid.y - size);
        this.max = new PVector(mid.x + size, mid.y + size);

        QUAD_INSTANCE++;
    }

    boolean contains(PVector point, float range) {
        return min.x <= point.x - range
                && max.x > point.x + range
                && min.y <= point.y - range
                && max.y > point.y + range;
    }

    boolean intersects(PVector p, float r) {
        return min.x < p.x + r && min.y < p.y + r && p.x - r < max.x && p.y - r < max.y;
    }

    boolean intersects(PVector min, PVector max) {
        return this.min.x <= max.x
                && this.min.y <= max.y
                && this.max.x >= min.x
                && this.max.y >= min.y;
    }

    boolean out_of_bounds(T item) {
        if (!contains(item.getPosition(), 0)) {
            System.out.println(
                    "item out of bounds\n" +
                    item.getPosition() + "\n" +
                    min + "\n" +
                    max
            );
            return true;
        }

        return false;
    }

    abstract ArrayList<T> get_items();
}

package space;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static game.Colours.*;
import static processing.core.PApplet.str;
import static processing.core.PConstants.CORNERS;
import static processing.core.PConstants.RADIUS;
import static processing.core.PVector.dist;

public class QuadSpaceRegion<T extends Spatial> extends QuadSpace<T> {
    private final ArrayList<T> items;

    QuadSpaceRegion(PVector min, PVector max) {
        super(min, max);
        this.items = new ArrayList<>(ITEMS_PER_REGION);
    }

    QuadSpaceRegion(QuadSpace<T> group) {
        this(group.min, group.max);

        items.addAll(group.get_items());
    }

    public void place(T item) {
        if (out_of_bounds(item)) return;

        items.add(item);
    }

    public void remove(T item) {
        if (out_of_bounds(item)) return;

        items.remove(item);
    }

    public ArrayList<T> get_items() {
        return items;
    }

    @Override
    public ArrayList<T> get(PVector min, PVector max) {
        ArrayList<T> intersecting = new ArrayList<>(itemCount());

        for (T item : items) {
            float x = item.getPosition().x;
            float y = item.getPosition().y;

            if (x > min.x && x < max.x && y > min.y && y < max.y) {
                intersecting.add(item);
            }
        }

        return intersecting;
    }

    @Override
    public ArrayList<T> get(PVector position, float radius) {
        ArrayList<T> intersecting = new ArrayList<>(itemCount());

        for (T item : items) {
            if (dist(position, item.getPosition()) < radius) {
                intersecting.add(item);
            }
        }

        return intersecting;
    }

    @Override
    public int itemCount() {
        return items.size();
    }

    public void draw(PGraphics g){
        if (fresh) g.fill(RED);
        else g.noFill();

        fresh = false;

        g.stroke(255);
        g.strokeWeight(10);

        // TODO: 08/01/2021 rect is expensive, use lines
        g.rectMode(CORNERS);
        g.rect(min.x, min.y, max.x, max.y);
        g.rectMode(RADIUS);

        g.fill(WHITE);
        g.textSize(50);
        g.text(str(items.size()), mid.x, mid.y);
    }
}

package space;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

import static game.Game.RED;
import static game.Game.WHITE;
import static processing.core.PApplet.str;
import static processing.core.PConstants.CORNERS;
import static processing.core.PConstants.RADIUS;

public class QuadSpaceRegion<T extends Spatial> extends QuadSpace<T> {
    ArrayList<T> items;

    QuadSpaceRegion(PVector min, PVector max) {
        super(min, max);
        this.items = new ArrayList<>(ITEMS_PER_REGION);
    }

    QuadSpaceRegion(QuadSpaceGroup<T> group) {
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
    public ArrayList<T> get(PVector a, PVector b) {
        return get_items();
    }

    @Override
    public ArrayList<T> get(PVector position, float radius) {
        return get_items();
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

        g.rectMode(CORNERS);
        g.rect(min.x, min.y, max.x, max.y);
        g.rectMode(RADIUS);

        g.fill(WHITE);
        g.textSize(50);
        g.text(str(items.size()), mid.x, mid.y);
    }
}

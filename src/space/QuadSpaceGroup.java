package space;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class QuadSpaceGroup<T extends Spatial> extends QuadSpace<T> {
    private QuadSpace<T> TL;
    private QuadSpace<T> TR;
    private QuadSpace<T> BL;
    private QuadSpace<T> BR;

    private int itemCount;

    public QuadSpaceGroup(PVector mid, float size) {
        super(mid, size);

        TL = new QuadSpaceRegion<>(min, mid);
        TR = new QuadSpaceRegion<>(new PVector(mid.x, min.y), new PVector(max.x, mid.y));
        BL = new QuadSpaceRegion<>(new PVector(min.x, mid.y), new PVector(mid.x, max.y));
        BR = new QuadSpaceRegion<>(mid, max);

        itemCount = 0;
    }

    QuadSpaceGroup(QuadSpace<T> region) {
        this(region.mid, region.max.x - region.mid.x);

        region.get_items().forEach(this::place);
    }

    @Override
    public void place(T item) {
        PVector position = item.getPosition();

        if (position.x < mid.x) {
            if (position.y < mid.y) {
                TL = place(TL, item);
            }

            else {
                BL = place(BL, item);
            }
        }

        else {
            if (position.y < mid.y) {
                TR = place(TR, item);
            }

            else {
                BR = place(BR, item);
            }
        }

        itemCount++;
    }

    private QuadSpace<T> place(QuadSpace<T> space, T item) {
        if (space.itemCount() == ITEMS_PER_REGION && space instanceof QuadSpaceRegion)
            space = new QuadSpaceGroup<>(space);

        space.place(item);

        return space;
    }

    @Override
    public void remove(T item) {
        PVector position = item.getPosition();
        if (out_of_bounds(item)) return;

        if (position.x < mid.x) {
            if (position.y < mid.y) {
                TL = remove(TL, item);
            }

            else {
                BL = remove(BL, item);
            }
        }

        else {
            if (position.y < mid.y) {
                TR = remove(TR, item);
            }

            else {
                BR = remove(BR, item);
            }
        }

        itemCount--;
    }

    // TODO: 30/07/2020 reclaim unused regions
    private QuadSpace<T> remove(QuadSpace<T> space, T item) {
        if (space.itemCount() == 1 && space instanceof QuadSpaceGroup) {
            space = new QuadSpaceRegion<>(space);
        }

        space.remove(item);

        return space;
    }

    ArrayList<T> get_items() {
        ArrayList<T> items = new ArrayList<>(itemCount());
        items.addAll(TL.get_items());
        items.addAll(TR.get_items());
        items.addAll(BL.get_items());
        items.addAll(BR.get_items());
        return items;
    }

    @Override
    public ArrayList<T> get(PVector a, PVector b) {
        ArrayList<T> intersecting = new ArrayList<>(itemCount());

        if (TL.intersects(a, b)) intersecting.addAll(TL.get(a, b));
        if (TR.intersects(a, b)) intersecting.addAll(TR.get(a, b));
        if (BL.intersects(a, b)) intersecting.addAll(BL.get(a, b));
        if (BR.intersects(a, b)) intersecting.addAll(BR.get(a, b));

        return intersecting;
    }

    @Override
    public ArrayList<T> get(PVector point, float range) {
        ArrayList<T> intersecting = new ArrayList<>(itemCount());

        if (TL.intersects(point, range)) intersecting.addAll(TL.get(point, range));
        if (TR.intersects(point, range)) intersecting.addAll(TR.get(point, range));
        if (BL.intersects(point, range)) intersecting.addAll(BL.get(point, range));
        if (BR.intersects(point, range)) intersecting.addAll(BR.get(point, range));

        return intersecting;
    }

    @Override
    public int itemCount() {
        return itemCount;
    }

    public void draw(PGraphics g) {
        TL.draw(g);
        TR.draw(g);
        BL.draw(g);
        BR.draw(g);

        g.text(itemCount, mid.x, mid.y);
    }
}

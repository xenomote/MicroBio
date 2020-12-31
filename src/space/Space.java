package space;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public interface Space<T extends Spatial> {

    void place(T item);
    void remove(T item);

    ArrayList<T> get(PVector min, PVector max);
    ArrayList<T> get(PVector position, float radius);
    // TODO: 31/12/2020 add distance sorted iterator method

    void draw(PGraphics g);

    int itemCount();
}

package commander;

import cell.Cell;
import controller.Mouse;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Space;

import java.util.List;

import static game.Colours.WHITE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static processing.core.PConstants.CORNERS;

public class SelectionBox {
    private final Mouse mouse;
    private final PVector a;
    private final PVector b;
    private final Space<Cell> cell_map;
    private final List<Cell> selection;

    public SelectionBox(Mouse mouse, Space<Cell> cell_map, List<Cell> selection) {
        this.a = new PVector();
        this.b = new PVector();

        this.mouse = mouse;
        this.cell_map = cell_map;
        this.selection = selection;
    }

    public void update() {
        if (mouse.left().pressed()) {
            if (mouse.left().held() == 0) {
                a.set(mouse.coordinates());
            }

            b.set(mouse.coordinates());
        }

        if (mouse.left().released()) {
            PVector min = new PVector(min(a.x, b.x), min(a.y, b.y));
            PVector max = new PVector(max(a.x, b.x), max(a.y, b.y));

            selection.clear();
            selection.addAll(cell_map.get(min, max));
        }
    }

    public void draw(PGraphics g) {
        if (mouse.left().pressed()) {
            PVector x = new PVector(min(a.x, b.x), min(a.y, b.y));
            PVector y = new PVector(max(a.x, b.x), max(a.y, b.y));

            g.push();
            g.rectMode(CORNERS);
            g.fill(WHITE, 256/2f);
            g.rect(x.x, x.y, y.x, y.y);
            g.pop();
        }
    }
}

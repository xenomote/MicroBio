package commander;

import cell.Cell;
import controller.Keyboard;
import controller.Mouse;
import game.EnergySource;
import processing.core.PGraphics;
import processing.core.PVector;
import space.Space;

import java.util.ArrayList;
import java.util.List;

import static game.Game.*;
import static game.Colours.*;

public class PlayerCommander extends Commander {
    private final Mouse mouse;
    private final Keyboard keyboard;
    private List<Cell> selection;

    private final PVector a;
    private final PVector b;

    public PlayerCommander(Space<Cell> cell_map, Space<EnergySource> source_map, int colour, Mouse mouse, Keyboard keyboard) {
        super(cell_map, source_map, colour);

        this.mouse = mouse;
        this.keyboard = keyboard;
        this.selection = new ArrayList<>();

        this.a = new PVector();
        this.b = new PVector();
    }

    public void update() {
        super.update();

        if (!mouse.left().pressed())
            a.set(mouse.coordinates());
        b.set(mouse.coordinates());

        if (mouse.left().released()) {
            PVector x = new PVector(min(a.x, b.x), min(a.y, b.y));
            PVector y = new PVector(max(a.x, b.x), max(a.y, b.y));

            selection = cellMap().get(x, y);
            selection.retainAll(cells());

            a.set(0, 0);
            b.set(0, 0);
        }

        else if (keyboard.key(' ').held() == 1) {
            if (selection.isEmpty()) selection = cells();
            else selection.clear();

            a.set(0, 0);
            b.set(0, 0);
        }

        // TODO: 26/12/2020 remove cells from original groups before adding new group
        if (!selection.isEmpty()) {
            if (keyboard.key('c').pressed()) place_colony(selection, mouse.coordinates());
            else if (keyboard.key('x').pressed()) place_squadron(selection, mouse.coordinates());
        }
    }

    public void drawMouse(PGraphics g) {
        PVector position = mouse.position();

        g.noFill();
        g.stroke(WHITE);
        g.strokeWeight(2);
        g.square(position.x, position.y, 10);
    }

    public void drawSelection(PGraphics g) {
        if (mouse.left().pressed()) {
            PVector x = new PVector(min(a.x, b.x), min(a.y, b.y));
            PVector y = new PVector(max(a.x, b.x), max(a.y, b.y));

            g.rectMode(CORNERS);
            g.fill(WHITE, 256/2f);
            g.rect(x.x, x.y, y.x, y.y);
            g.rectMode(RADIUS);

            g.fill(RED);
            g.circle(x.x, x.y, 20);

            g.fill(BLUE);
            g.circle(y.x, y.y, 20);
        }
    }

    public void highlightSelection(PGraphics g) {
        for (Cell cell : selection)
            cell.highlight(g);
    }
}
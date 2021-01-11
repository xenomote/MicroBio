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
    private final List<Cell> selection;

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
            selection.addAll(cellMap().get(min, max));
            selection.retainAll(cells());
        }

        else if (keyboard.key(' ').held() == 1) {
            if (selection.isEmpty()) selection.addAll(cells());
            else selection.clear();
        }

        selection.removeIf(Cell::dead);

        // TODO: 29/12/2020 move existing group if it is it is selected
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

            g.push();
            g.rectMode(CORNERS);
            g.fill(WHITE, 256/2f);
            g.rect(x.x, x.y, y.x, y.y);
            g.pop();
        }
    }

    public void highlightSelection(PGraphics g) {
        for (Cell cell : selection)
            cell.highlight(g);
    }
}
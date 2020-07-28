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

import static game.Game.WHITE;

public class PlayerCommander extends Commander {
    private Mouse mouse;
    private Keyboard keyboard;
    private List<Cell> selection;

    private PVector a;
    private PVector b;

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

        if (mouse.left().held() > 0)
            b.set(mouse.coordinates());

        else if (mouse.left().pressed())
            a.set(mouse.coordinates());

        else if (mouse.left().released())
            selection = cellMap().get(a, b);

        if (keyboard.key(' ').pressed())
            selection = cells();

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

    }
}
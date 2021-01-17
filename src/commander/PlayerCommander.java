package commander;

import cell.Cell;
import controller.Keyboard;
import controller.Mouse;
import game.EnergySource;
import processing.core.PGraphics;
import space.Space;

import java.util.ArrayList;
import java.util.List;

public class PlayerCommander extends Commander {
    private final Mouse mouse;
    private final Keyboard keyboard;

    // TODO: 14/01/2021 refactor selection box into separate class
    private final List<Cell> selection;
    private final SelectionBox box;
    private final SelectionDrag drag;

    public PlayerCommander(Space<Cell> cell_map, Space<EnergySource> source_map, int colour, Mouse mouse, Keyboard keyboard) {
        super(cell_map, source_map, colour);

        this.mouse = mouse;
        this.keyboard = keyboard;
        this.selection = new ArrayList<>();

        this.box = new SelectionBox(mouse, cell_map, selection);
        this.drag = new SelectionDrag(mouse, cell_map, selection);
    }

    public void update() {
        super.update();
        drag.update();

        if (keyboard.key(' ').held() == 1) {
            selection.clear();
            selection.addAll(cells());
        }

        // TODO: 29/12/2020 move existing group if it is it is selected
        if (!selection.isEmpty()) {
            selection.retainAll(cells());
            selection.removeIf(Cell::dead);

            if (keyboard.key('c').pressed()) {
                place_colony(selection, mouse.coordinates());
            } else if (keyboard.key('x').pressed()) {
                place_squadron(selection, mouse.coordinates());
            }
        }
    }

    public void highlightSelection(PGraphics g) {
        for (Cell cell : selection) {
            cell.highlight(g);
        }
    }

    public SelectionBox getBox() {
        return box;
    }

    public SelectionDrag getDrag() {
        return drag;
    }
}
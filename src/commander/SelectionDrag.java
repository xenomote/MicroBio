package commander;

import cell.Cell;
import controller.Mouse;
import processing.core.PGraphics;
import space.Space;

import java.util.List;

import static cell.Cell.MEMBRANE_RADIUS;

public class SelectionDrag {
    private final Mouse mouse;
    private final Space<Cell> cell_map;
    private final List<Cell> selection;

    public SelectionDrag(Mouse mouse, Space<Cell> cell_map, List<Cell> selection) {
        this.mouse = mouse;
        this.cell_map = cell_map;
        this.selection = selection;
    }

    public void update() {
        if (mouse.left().pressed()) {
            if (mouse.left().held() == 0) {
                selection.clear();
            }

            List<Cell> cells = cell_map.get(mouse.coordinates(), MEMBRANE_RADIUS);
            for (Cell cell : cells) {
                if (!selection.contains(cell)) {
                    selection.add(cell);
                }
            }
        }
    }

    public void draw(PGraphics g) {

    }
}

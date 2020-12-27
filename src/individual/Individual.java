package individual;

import cell.Cell;

public abstract class Individual {

    protected final Cell cell;

    Individual(Cell cell) {
        this.cell = cell;
    }

    public abstract void update();

    public boolean dead() {
        return cell.dead();
    }
}
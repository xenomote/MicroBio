package individual;

import cell.Cell;

// TODO: 30/12/2020 remove individual class, maybe implement interface instead
public abstract class Individual {

    protected final Cell cell;

    Individual(Cell cell) {
        this.cell = cell;
    }

    public abstract void update();
}
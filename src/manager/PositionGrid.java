package manager;

import processing.core.PVector;

import java.util.ArrayList;

import static java.lang.Math.*;

public class PositionGrid {
    private final float maxR;

    private final ReadList<PVector> positions;
    private final ReadList<Float> radii;

    private final ArrayList<ArrayList<ArrayList<Integer>>> grid;

    public PositionGrid(
            float maxX, float maxY, float maxR,
            ReadList<PVector> positions,
            ReadList<Float> radii
    ) {
        this.positions = positions;
        this.radii = radii;
        this.maxR = maxR;

        int x = (int) ceil(maxX / maxR);
        int y = (int) ceil(maxY / maxR);

        this.grid = new ArrayList<>(x);

        for (int i = 0; i < x; i++) {
            ArrayList<ArrayList<Integer>> column = new ArrayList<>(y);

            for (int j = 0; j < y; j++) {
                ArrayList<Integer> region = new ArrayList<>();

                column.add(region);
            }

            this.grid.add(column);
        }
    }

    public void update() {
        depopulate();
        populate();
    }

    private void depopulate() {
        for (ArrayList<ArrayList<Integer>> column : grid) {
            for (ArrayList<Integer> region : column) {
                region.clear();
            }
        }
    }

    private void populate() {
        assert(positions.size() == radii.size());

        for (int i = 0; i < positions.size(); i++) {
            PVector position = positions.get(i);
            float radius = radii.get(i);

            int a = max((int) floor((position.x - radius) / maxR), 0);
            int b = min((int) floor((position.x + radius) / maxR) + 1, grid.size());

            for (int x = a; x < b; x++) {
                ArrayList<ArrayList<Integer>> column = grid.get(x);

                int m = max((int) floor((position.y - radius) / maxR), 0);
                int n = min((int) floor((position.y + radius) / maxR) + 1, column.size());

                for (int y = m; y < n; y++) {
                    ArrayList<Integer> region = column.get(y);

                    region.add(i);
                }
            }
        }
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> getGrid() {
        return grid;
    }
}

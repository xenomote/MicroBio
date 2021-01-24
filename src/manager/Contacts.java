package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class Contacts {
    private final PositionGrid positionGrid;
    private final ReadList<PVector> positions;
    private final ReadList<Float> radii;
    private final ReadList<PVector> forces;
    private final float k;

    public Contacts(
            PositionGrid positionGrid,
            ReadList<PVector> positions,
            ReadList<Float> radii,
            ReadList<PVector> forces,
            float k) {
        this.positionGrid = positionGrid;
        this.positions = positions;
        this.radii = radii;
        this.forces = forces;
        this.k = k;
    }

    public void update() {
        for (ArrayList<ArrayList<Integer>> column : positionGrid.getGrid()) {
            for (ArrayList<Integer> region : column) {
                for (int i = 0; i < region.size() - 1; i++) {
                    for (int j = i + 1; j < region.size(); j++) {
                        int a = region.get(i);
                        int b = region.get(j);

                        PVector delta = PVector.sub(positions.get(a), positions.get(b));
                        float dist = radii.get(a) + radii.get(b);
                        float diff = delta.mag();

                        if (diff < dist) {
                            delta.setMag(dist - diff);

                            forces.get(a).add(PVector.mult(delta, k));
                            forces.get(b).add(PVector.mult(delta, -k));
                        }
                    }
                }
            }
        }
    }
}

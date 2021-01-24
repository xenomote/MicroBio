package manager;

import processing.core.PVector;

import java.util.ArrayList;

import static manager.Cells.ATTACHMENT_RADIUS;

public class Attachments {
    private final PositionGrid positionGrid;
    private final ReadList<PVector> membranes;
    private final ReadList<PVector> nuclei;
    private final ReadList<Float> radii;
    private final ReadList<Integer> colours;

    public Attachments(
            PositionGrid positionGrid,
            ReadList<PVector> membranes,
            ReadList<PVector> nuclei,
            ReadList<Float> radii,
            ReadList<Integer> colours
    ) {
        this.positionGrid = positionGrid;
        this.membranes = membranes;
        this.nuclei = nuclei;
        this.radii = radii;
        this.colours = colours;
    }

    public void update() {
        for (ArrayList<ArrayList<Integer>> column : positionGrid.getGrid()) {
            for (ArrayList<Integer> region : column) {
                for (int i = 0; i < region.size() - 1; i++) {
                    for (int j = i + 1; j < region.size(); j++) {
                        int a = region.get(i);
                        int b = region.get(j);

                        if (colours.get(a).equals(colours.get(b))) {

                            PVector delta = PVector.sub(membranes.get(a), membranes.get(b));
                            float dist = radii.get(a) + radii.get(b);
                            float diff = delta.mag();

                            if (diff < dist + ATTACHMENT_RADIUS) {
                                delta.setMag((dist - diff)/2);

                                membranes.get(a).add(delta);
                                membranes.get(b).sub(delta);

                                nuclei.get(a).add(delta);
                                nuclei.get(b).sub(delta);
                            }
                        }
                    }
                }
            }
        }
    }
}

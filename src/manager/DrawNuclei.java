package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import static manager.Cells.MAX_ENERGY;
import static manager.Cells.NUCLEUS_RADIUS;
import static processing.core.PConstants.RADIUS;

public class DrawNuclei {
    private final ReadList<PVector> positions;
    private final ReadList<Float> energies;

    public DrawNuclei(
            ReadList<PVector> positions,
            ReadList<Float> energies
    ) {
        this.positions = positions;
        this.energies = energies;
    }

    public void draw(PGraphics g) {
        assert(positions.size() == energies.size());

        g.push();
        g.ellipseMode(RADIUS);
        g.noStroke();

        for (int i = 0; i < positions.size(); i++) {
            g.fill(255 * (energies.get(i) / MAX_ENERGY));

            PVector position = positions.get(i);
            g.circle(position.x, position.y, NUCLEUS_RADIUS);
        }

        g.pop();
    }
}

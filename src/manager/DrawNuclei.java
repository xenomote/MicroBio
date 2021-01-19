package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PConstants.RADIUS;

public class DrawNuclei {
    public static final float NUCLEUS_RADIUS = 1;

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
        g.rectMode(RADIUS);
        g.noStroke();

        for (int i = 0; i < positions.size(); i++) {
            // TODO: 19/01/2021 scale shade to maximum energy
            g.fill(energies.get(i));

            PVector position = positions.get(i);
            g.square(position.x, position.y, NUCLEUS_RADIUS);
        }

        g.pop();
    }
}

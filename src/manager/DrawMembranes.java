package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.RADIUS;

public class DrawMembranes {
    private final ReadList<PVector> positions;
    private final ReadList<Integer> colours;
    private final ReadList<Float> radii;
    private final ReadList<Float> healths;

    public DrawMembranes(ReadList<PVector> positions, ReadList<Integer> colours, ReadList<Float> radii, ReadList<Float> healths) {
        this.positions = positions;
        this.colours = colours;
        this.radii = radii;
        this.healths = healths;
    }

    public void draw(PGraphics g) {
        assert(positions.size() == radii.size() && radii.size() == colours.size());

        g.push();
        g.rectMode(RADIUS);

        for (int i = 0; i < positions.size(); i++) {
            // TODO: 19/01/2021 scale health to max health to tint cell
            float health = healths.get(i);
            int colour = colours.get(i);

            // TODO: 19/01/2021 darken colour without separating components, HSB?
            g.fill(
                    g.red(colour) * health,
                    g.green(colour) * health,
                    g.blue(colour) * health
            );

            PVector position = positions.get(i);
            g.square(position.x, position.y, radii.get(i));
        }

        g.pop();
    }
}

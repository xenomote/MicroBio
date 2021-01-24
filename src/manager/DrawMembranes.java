package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import static manager.Cells.MAX_HEALTH;
import static processing.core.PConstants.RADIUS;

public class DrawMembranes {
    private final ReadList<PVector> positions;
    private final ReadList<Integer> colours;
    private final ReadList<Float> radii;
    private final ReadList<Float> healths;

    public DrawMembranes(
            ReadList<PVector> positions,
            ReadList<Integer> colours,
            ReadList<Float> radii,
            ReadList<Float> healths
    ) {
        this.positions = positions;
        this.colours = colours;
        this.radii = radii;
        this.healths = healths;
    }

    public void draw(PGraphics g) {
        assert(positions.size() == radii.size() && radii.size() == colours.size());

        g.push();
        g.ellipseMode(RADIUS);
        g.noStroke();

        for (int i = 0; i < positions.size(); i++) {
            float health = healths.get(i) / MAX_HEALTH;
            int colour = colours.get(i);

            g.fill(g.hue(colour), g.saturation(colour), g.brightness(colour) * health);

            PVector position = positions.get(i);
            g.circle(position.x, position.y, radii.get(i));
        }

        g.pop();
    }
}

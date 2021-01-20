package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;


public class Cells {
    public static final float MEMBRANE_MASS = 10;
    public static final float MEMBRANE_RADIUS = 5;

    public static final float NUCLEUS_MASS = 10;
    public static final float NUCLEUS_RADIUS = 2;

    public static final float MAX_HEALTH = 100;
    public static final float MAX_ENERGY = 100;

    public static final float CELL_SPRING = 100;

    private final PhysicsStore membranes;
    private final PhysicsStore nuclei;

    private final Springs springs;

    private final DrawMembranes drawMembranes;
    private final DrawNuclei drawNuclei;

    private final Register<Float> energies;
    private final Register<Integer> colours;
    private final Register<Float> radii;
    private final Register<Float> healths;

    public Cells(int n, ArrayList<Integer> deletions) {
        membranes = new PhysicsStore(n, deletions);
        nuclei = new PhysicsStore(n, deletions);

        springs = new Springs(membranes, nuclei, CELL_SPRING);

        ArrayList<Float> energies = new ArrayList<>(n);
        ArrayList<Integer> colours = new ArrayList<>(n);
        ArrayList<Float> radii = new ArrayList<>(n);
        ArrayList<Float> healths = new ArrayList<>(n);

        drawMembranes = new DrawMembranes(membranes.getPositions(), new ReadList<>(colours), new ReadList<>(radii), new ReadList<>(healths));
        drawNuclei = new DrawNuclei(nuclei.getPositions(), new ReadList<>(energies));

        this.energies = new Register<>(energies, deletions);
        this.colours = new Register<>(colours, deletions);
        this.radii = new Register<>(radii, deletions);
        this.healths = new Register<>(healths, deletions);
    }

    public void create(float x, float y, float energy, int colour) {
        membranes.getRegistry().create(MEMBRANE_MASS, x, y);
        nuclei.getRegistry().create(NUCLEUS_MASS, x, y);

        energies.add(energy);
        colours.add(colour);

        radii.add(MEMBRANE_RADIUS);
        healths.add(MAX_HEALTH);
    }

    public void update(float time) {
        for (PVector force : nuclei.getForces()) {
            force.set(PVector.random2D().mult(100));
        }

        membranes.getPipeline().update(time);
        nuclei.getPipeline().update(time);

        springs.update();

        membranes.getRegistry().update();
        nuclei.getRegistry().update();

        energies.update();
        colours.update();
        radii.update();
        healths.update();
    }

    public void draw(PGraphics g) {
        drawMembranes.draw(g);
        drawNuclei.draw(g);
    }
}

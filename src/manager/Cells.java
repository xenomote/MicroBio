package manager;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

import static manager.Game.*;


public class Cells {
    public static final float MEMBRANE_MASS = 2;
    public static final float MEMBRANE_RADIUS = 5;

    public static final float NUCLEUS_MASS = 1;
    public static final float NUCLEUS_RADIUS = 2;

    public static final float MAX_HEALTH = 100;
    public static final float MAX_ENERGY = 100;

    public static final float CELL_SPRING = 100;
    private static final float CONTACT_SPRING = 1;

    private final PhysicsStore membranes;
    private final PhysicsStore nuclei;

    private final PositionGrid positionGrid;
    private final Contacts contacts;

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

        energies = new Register<>(new ArrayList<>(n), deletions);
        colours = new Register<>(new ArrayList<>(n), deletions);
        radii = new Register<>(new ArrayList<>(n), deletions);
        healths = new Register<>(new ArrayList<>(n), deletions);

        drawMembranes = new DrawMembranes(membranes.getPositions(), colours.read(), radii.read(), healths.read());
        drawNuclei = new DrawNuclei(nuclei.getPositions(), energies.read());

        positionGrid = new PositionGrid(WIDTH, HEIGHT, 100, membranes.getPositions(), radii.read());
        contacts = new Contacts(positionGrid, membranes.getPositions(), radii.read(), membranes.getForces(), CONTACT_SPRING);
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
            force.add(PVector.random2D().mult(10f));
        }

        membranes.getPipeline().update(time);
        nuclei.getPipeline().update(time);

        positionGrid.update();
        contacts.update();

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
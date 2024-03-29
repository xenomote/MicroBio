package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class PhysicsRegistry {
    private final Register<Float> massRegister;
    private final Register<PVector> forceRegister;
    private final Register<PVector> accelerationRegister;
    private final Register<PVector> velocityRegister;
    private final Register<PVector> positionRegister;

    PhysicsRegistry(
            ArrayList<Float> masses,
            ArrayList<PVector> forces,
            ArrayList<PVector> accelerations,
            ArrayList<PVector> velocities,
            ArrayList<PVector> positions,
            ArrayList<Integer> deletions
    ) {
        massRegister = new Register<>(masses, deletions);
        forceRegister = new Register<>(forces, deletions);
        accelerationRegister = new Register<>(accelerations, deletions);
        velocityRegister = new Register<>(velocities, deletions);
        positionRegister = new Register<>(positions, deletions);
    }

    public void create(float mass, float x, float y) {
        massRegister.add(mass);
        forceRegister.add(new PVector());
        accelerationRegister.add(new PVector());
        velocityRegister.add(new PVector());
        positionRegister.add(new PVector(x, y));
    }

    public void update() {
        massRegister.update();
        forceRegister.update();
        accelerationRegister.update();
        velocityRegister.update();
        positionRegister.update();
    }
}

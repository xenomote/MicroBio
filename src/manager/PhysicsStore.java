package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class PhysicsStore {
    private final PhysicsPipeline pipeline;
    private final PhysicsRegistry registry;

    public PhysicsStore(int n) {
        ArrayList<Float> masses = new ArrayList<>(n);
        ArrayList<PVector> forces = new ArrayList<>(n);
        ArrayList<PVector> accelerations = new ArrayList<>(n);
        ArrayList<PVector> velocities = new ArrayList<>(n);
        ArrayList<PVector> positions = new ArrayList<>(n);

        pipeline = new PhysicsPipeline(masses, forces, accelerations, velocities, positions);
        registry = new PhysicsRegistry(masses, forces, accelerations, velocities, positions);
    }

    public PhysicsPipeline getPipeline() {
        return pipeline;
    }

    public PhysicsRegistry getRegistry() {
        return registry;
    }
}

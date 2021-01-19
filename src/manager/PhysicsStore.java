package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class PhysicsStore {
    private final PhysicsPipeline pipeline;
    private final PhysicsRegistry registry;

    private final ReadList<Float> masses;
    private final ReadList<PVector> forces;
    private final ReadList<PVector> accelerations;
    private final ReadList<PVector> velocities;
    private final ReadList<PVector> positions;

    public PhysicsStore(int n, ArrayList<Integer> deletions) {
        ArrayList<Float> masses = new ArrayList<>(n);
        ArrayList<PVector> forces = new ArrayList<>(n);
        ArrayList<PVector> accelerations = new ArrayList<>(n);
        ArrayList<PVector> velocities = new ArrayList<>(n);
        ArrayList<PVector> positions = new ArrayList<>(n);

        registry = new PhysicsRegistry(masses, forces, accelerations, velocities, positions, deletions);

        this.masses = new ReadList<>(masses);
        this.forces = new ReadList<>(forces);
        this.accelerations = new ReadList<>(accelerations);
        this.velocities = new ReadList<>(velocities);
        this.positions = new ReadList<>(positions);

        pipeline = new PhysicsPipeline(this.masses, this.forces, this.accelerations, this.velocities, this.positions);
    }

    public PhysicsPipeline getPipeline() {
        return pipeline;
    }

    public PhysicsRegistry getRegistry() {
        return registry;
    }

    public ReadList<Float> getMasses() {
        return masses;
    }

    public ReadList<PVector> getForces() {
        return forces;
    }

    public ReadList<PVector> getAccelerations() {
        return accelerations;
    }

    public ReadList<PVector> getVelocities() {
        return velocities;
    }

    public ReadList<PVector> getPositions() {
        return positions;
    }
}

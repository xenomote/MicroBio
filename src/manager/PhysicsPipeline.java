package manager;

import processing.core.PVector;

public class PhysicsPipeline {
    private static final float DRAG = 0.99f;

    private final Integrate accelerate;
    private final Integrate displace;
    private final Divide impulse;

    private final ReadList<PVector> forces;
    private final ReadList<PVector> velocities;

    PhysicsPipeline(
            ReadList<Float> masses,
            ReadList<PVector> forces,
            ReadList<PVector> accelerations,
            ReadList<PVector> velocities,
            ReadList<PVector> positions
    ) {
        this.impulse = new Divide(forces, masses, accelerations);
        this.accelerate = new Integrate(accelerations, velocities);
        this.displace = new Integrate(velocities, positions);

        this.forces = forces;
        this.velocities = velocities;
    }

    public void update(float time) {
        impulse.update();
        accelerate.update(time);
        displace.update(time);

        for (PVector force : forces) {
            force.set(0, 0);
        }

        for (PVector velocity : velocities) {
            velocity.mult(DRAG);
        }
    }
}

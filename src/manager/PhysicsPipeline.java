package manager;

import processing.core.PVector;

public class PhysicsPipeline {
    private final Integrate accelerate;
    private final Integrate displace;
    private final Scale impulse;

    PhysicsPipeline(
            ReadList<Float> masses,
            ReadList<PVector> forces,
            ReadList<PVector> accelerations,
            ReadList<PVector> velocities,
            ReadList<PVector> positions
    ) {
        this.impulse = new Scale(forces, masses, accelerations);
        this.accelerate = new Integrate(accelerations, velocities);
        this.displace = new Integrate(velocities, positions);
    }

    public void update(float time) {
        impulse.update();
        accelerate.update(time);
        displace.update(time);
    }
}

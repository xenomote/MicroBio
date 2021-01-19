package manager;

import processing.core.PVector;

import java.util.ArrayList;

public class PhysicsPipeline {
    private final Integrate accelerate;
    private final Integrate displace;
    private final Scale impulse;

    PhysicsPipeline(
            ArrayList<Float> masses,
            ArrayList<PVector> forces,
            ArrayList<PVector> accelerations,
            ArrayList<PVector> velocities,
            ArrayList<PVector> positions
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

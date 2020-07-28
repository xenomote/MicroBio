package cell;

import processing.core.PVector;

import static processing.core.PVector.dist;

public class Movement {
    private PVector target;
    private PVector position;
    private Physics physics;
    private float force;

    public Movement(PVector target, Physics physics, float force) {
        this.target = target;
        this.physics = physics;
        this.force = force;

        this.position = physics.getPosition();
    }

    public void update() {
        if (moving()) physics.force(PVector.sub(target, position).setMag(force));
        else target.set(position);
    }

    public boolean moving() {
        return dist(target, position) > force;
    }
}

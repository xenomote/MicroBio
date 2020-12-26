package cell;

import processing.core.PVector;

import static processing.core.PVector.dist;

public class Movement {
    private PVector target;
    private PVector position;
    private PointMass pointMass;
    private float force;

    public Movement(PVector target, PointMass pointMass, float force) {
        this.target = target;
        this.pointMass = pointMass;
        this.force = force;

        this.position = pointMass.getPosition();
    }

    public void update() {
        if (moving()) pointMass.force(PVector.sub(target, position).setMag(force));
        else target.set(position);
    }

    public boolean moving() {
        return dist(target, position) > force;
    }
}

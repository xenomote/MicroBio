package cell;

import processing.core.PVector;

public class Physics {
    private PVector acceleration;
    private PVector velocity;
    private PVector position;

    private float mass;
    private float damping;

    private PVector force_total;

    public Physics(PVector position, float mass, float damping) {
        assert(mass > 0);
        assert(damping >= 0);
        assert(damping <= 1);

        this.acceleration = new PVector();
        this.velocity = new PVector();
        this.position = position;
        this.mass = mass;
        this.damping = damping;

        this.force_total = new PVector();
    }

    public void force(PVector force) {
        force_total.add(force);
    }


    public void update() {
        acceleration.set(force_total).div(mass);
        velocity.mult(damping).add(acceleration);
        position.add(velocity);

        force_total.set(0, 0);

        // TODO: 19/07/2020 bounds check
    }

    public float getMass() {
        return mass;
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public PVector getAcceleration() {
        return acceleration;
    }

    private PVector to(Physics b) {
        return PVector.sub(position, b.position);
    }

    static void spring(Physics a, Physics b, float d, float k) {
        float force = -(dist(a, b) - d) * k;
        a.force(a.to(b).setMag(force));
        b.force(b.to(a).setMag(force));
    }

    static float dist(Physics a, Physics b) {
        return PVector.dist(a.position, b.position);
    }

    public PVector getForce() {
        return force_total;
    }
}




